package com.example.wecareto_project.data

import com.example.wecareto_project.data.remote.TorontoOpenDataApi
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException
import java.util.concurrent.TimeUnit

class TorontoOpenDataApiTest {
    private fun envelope(records: String, total: Int = 1) =
        """{"success":true,"result":{"records":$records,"total":$total}}"""
    private val row = """{"PROGRAM_ID":"123","PROGRAM_NAME":"Example shelter","OCCUPANCY_DATE":"2026-11-01","LOCATION_ADDRESS":null,"SECTOR":"Families"}"""

    @Test fun latestDayIsFilteredAndMissingFieldsStayHonest() = runTest {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody(envelope("[$row]")))
            server.enqueue(MockResponse().setBody(envelope("[$row]")))
            val resources = TorontoOpenDataApi(OkHttpClient(), server.url("/"), now = { 42 }).fetchResources()
            assertEquals(1, resources.size)
            assertEquals("toronto-shelter-123", resources.single().id)
            assertEquals("Address not published", resources.single().address)
            assertEquals("", resources.single().phone)
            assertEquals(42L, resources.single().updatedAtMillis)
            server.takeRequest()
            val filters = server.takeRequest().requestUrl!!.queryParameter("filters")!!
            assertEquals("2026-11-01", JsonParser.parseString(filters).asJsonObject["OCCUPANCY_DATE"].asString)
        }
    }

    @Test fun paginationDoesNotSilentlyDropLaterRecords() = runTest {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody(envelope("[$row]")))
            server.enqueue(MockResponse().setBody(envelope("[$row]", total = 2)))
            server.enqueue(MockResponse().setBody(envelope("[${row.replace("123", "456")}]", total = 2)))
            val resources = TorontoOpenDataApi(OkHttpClient(), server.url("/")).fetchResources()
            assertEquals(2, resources.size)
            server.takeRequest(); server.takeRequest()
            assertEquals("1", server.takeRequest().requestUrl!!.queryParameter("offset"))
        }
    }

    @Test fun apiErrorAndMalformedPayloadAreRejected() = runTest {
        for (body in listOf("""{"success":false}""", "{}", "null")) {
            MockWebServer().use { server ->
                server.enqueue(MockResponse().setBody(body))
                try {
                    TorontoOpenDataApi(OkHttpClient(), server.url("/")).fetchResources()
                    fail("Invalid response should fail")
                } catch (_: IOException) { }
            }
        }
    }

    @Test fun realTorontoResponseFixtureMapsWithoutInventingAContactNumber() {
        val json = javaClass.getResource("/toronto-shelters.json")!!.readText()
        val dto = Gson().fromJson(JsonParser.parseString(json).asJsonObject["result"].asJsonObject["records"].asJsonArray[1],
            com.example.wecareto_project.data.remote.TorontoResourceDto::class.java)
        val resource = dto.toResource(42)!!
        assertEquals("Shelter", resource.category)
        assertTrue(resource.address.contains("Dixon"))
        assertEquals("", resource.phone)
    }

    @Test fun liveTorontoEndpointReturnsUniqueDatedPrograms() = runTest {
        org.junit.Assume.assumeTrue(java.lang.Boolean.getBoolean("wecare.liveApi"))
        val client = OkHttpClient.Builder().callTimeout(30, TimeUnit.SECONDS).build()
        val resources = TorontoOpenDataApi(client).fetchResources()
        assertTrue(resources.isNotEmpty())
        assertEquals(resources.size, resources.distinctBy { it.id }.size)
        assertEquals(1, resources.map { it.reportedDate }.distinct().size)
        assertTrue(resources.all { it.id.startsWith("toronto-shelter-") && it.name.isNotBlank() })
    }
}
