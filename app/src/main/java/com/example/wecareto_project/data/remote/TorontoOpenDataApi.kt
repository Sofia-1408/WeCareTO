package com.example.wecareto_project.data.remote

import com.example.wecareto_project.data.Resource
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

fun interface ResourceRemoteDataSource {
    suspend fun fetchResources(): List<Resource>
}

class TorontoOpenDataApi(
    private val client: OkHttpClient,
    private val baseUrl: HttpUrl = "https://ckan0.cf.opendata.inter.prod-toronto.ca/".toHttpUrl(),
    private val now: () -> Long = System::currentTimeMillis
) : ResourceRemoteDataSource {
    private val gson = Gson()

    override suspend fun fetchResources(): List<Resource> = withContext(Dispatchers.IO) {
        val latest = request(limit = 1).getAsJsonArray("records")?.firstOrNull()
            ?.asJsonObject?.get("OCCUPANCY_DATE")?.takeUnless { it.isJsonNull }?.asString
            ?: throw IOException("No dated shelter records were returned.")
        if (!latest.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) throw IOException("Invalid reporting date.")
        val rows = mutableListOf<TorontoResourceDto>()
        var offset = 0
        do {
            val result = request(limit = 500, offset = offset, date = latest)
            val records = result.getAsJsonArray("records") ?: throw IOException("Missing shelter records.")
            rows += records.map { gson.fromJson(it, TorontoResourceDto::class.java) }
            offset += records.size()
            val total = result.get("total")?.asInt ?: throw IOException("Missing result count.")
            if (offset >= total) break
            if (records.size() == 0 || offset >= 5000) throw IOException("Incomplete shelter results.")
        } while (true)
        val resources = rows.mapNotNull { it.toResource(now()) }.distinctBy { it.id }
        if (resources.isEmpty()) throw IOException("No usable shelter records were returned.")
        resources
    }

    private fun request(limit: Int, offset: Int = 0, date: String? = null): JsonObject {
        val url = baseUrl.newBuilder().addPathSegments("api/3/action/datastore_search")
            .addQueryParameter("resource_id", DATASET_ID)
            .addQueryParameter("limit", limit.toString())
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("sort", if (date == null) "OCCUPANCY_DATE desc" else "PROGRAM_ID asc")
            .apply { if (date != null) addQueryParameter("filters", gson.toJson(mapOf("OCCUPANCY_DATE" to date))) }
            .build()
        client.newCall(Request.Builder().url(url).build()).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Toronto API returned HTTP ${response.code}.")
            val json = try {
                gson.fromJson(response.body?.string() ?: throw IOException("Empty response."), JsonObject::class.java)
                    ?: throw IOException("Empty response.")
            } catch (error: JsonParseException) {
                throw IOException("Invalid Toronto API response.", error)
            }
            if (json.get("success")?.asBoolean != true) throw IOException("Toronto API could not complete the request.")
            return json.getAsJsonObject("result") ?: throw IOException("Missing API result.")
        }
    }

    companion object { const val DATASET_ID = "42714176-4f05-44e6-b157-2b57f29b856a" }
}
