package com.example.wecareto_project.data

import com.example.wecareto_project.data.local.ResourceStore
import com.example.wecareto_project.data.remote.ResourceRemoteDataSource
import com.example.wecareto_project.data.remote.TorontoResourceDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class ResourceRepositoryTest {
    private val resource = Resource("toronto-1", "Shelter", "Shelter", "Description",
        sourceUrl = TorontoResourceDto.SOURCE_URL, source = TorontoResourceDto.SOURCE, updatedAtMillis = 42)

    @Test fun refreshReplacesOnlyTorontoData() = runTest {
        val store = FakeResourceStore()
        store.upsert(ReferralResources.all() + resource.copy(id = "old"))
        val repository = ResourceRepository(store, ResourceRemoteDataSource { listOf(resource) })
        assertEquals(DataResult.Success(1), repository.refresh())
        assertEquals(5, store.rows.value.size)
        assertFalse(store.rows.value.any { it.id == "old" })
        assertEquals(resource, repository.getResourceById(resource.id))
    }

    @Test fun networkFailureKeepsTheCache() = runTest {
        val store = FakeResourceStore(); store.upsert(listOf(resource))
        val repository = ResourceRepository(store, ResourceRemoteDataSource { throw IOException("offline") })
        assertTrue(repository.refresh() is DataResult.Failure)
        assertEquals(listOf(resource), store.rows.value)
    }

    @Test fun emptyResponseDoesNotEraseTheCache() = runTest {
        val store = FakeResourceStore(); store.upsert(listOf(resource))
        val repository = ResourceRepository(store, ResourceRemoteDataSource { emptyList() })
        assertTrue(repository.refresh() is DataResult.Failure)
        assertEquals(listOf(resource), store.rows.value)
    }

    @Test fun cancellationIsNotShownAsANetworkError() = runTest {
        val repository = ResourceRepository(FakeResourceStore(), ResourceRemoteDataSource { throw CancellationException() })
        try { repository.refresh(); fail("Cancellation should propagate") }
        catch (_: CancellationException) { }
    }
}

class FakeResourceStore : ResourceStore {
    val rows = MutableStateFlow<List<Resource>>(emptyList())
    override fun observeResources() = rows
    override suspend fun findById(id: String) = rows.value.find { it.id == id }
    override suspend fun upsert(resources: List<Resource>) {
        rows.value = (rows.value + resources).associateBy { it.id }.values.toList()
    }
    override suspend fun replaceSource(source: String, resources: List<Resource>) {
        rows.value = rows.value.filterNot { it.source == source } + resources
    }
}
