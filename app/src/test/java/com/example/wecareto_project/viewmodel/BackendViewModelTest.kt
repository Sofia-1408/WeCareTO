package com.example.wecareto_project.viewmodel

import com.example.wecareto_project.data.*
import com.example.wecareto_project.data.remote.ResourceRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class BackendViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun repeatedSubmitDoesNotStartDuplicateSaves() = runTest(dispatcher) {
        val remote = FakeIntakeRemote(waitMillis = 10)
        val viewModel = IntakeSubmissionViewModel(IntakeRepository(remote))
        val draft = IntakeResponse(category = "Transportation")
        viewModel.submit(draft); viewModel.submit(draft.copy(id = "different"))
        assertTrue(viewModel.state.value.saving)
        advanceUntilIdle()
        assertEquals(draft.id, viewModel.state.value.savedId)
        assertEquals(draft, remote.saved)
    }

    @Test fun failedRefreshLeavesCachedRowsVisibleAndClearsLoading() = runTest(dispatcher) {
        val store = FakeResourceStore()
        store.upsert(ReferralResources.all())
        val repository = ResourceRepository(store, ResourceRemoteDataSource { error("offline") })
        val viewModel = ResourceViewModel(repository)
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(4, viewModel.state.value.resources.size)
        assertNotNull(viewModel.state.value.error)
    }
    @Test fun retryAfterAnUnconfirmedSaveKeepsTheOriginalDraftReference() = runTest(dispatcher) {
        val attempts = mutableListOf<IntakeResponse>()
        val remote = object : com.example.wecareto_project.data.remote.IntakeRemoteDataSource {
            override val isConfigured = true
            override suspend fun save(response: IntakeResponse): String {
                attempts += response
                if (attempts.size == 1) error("Connection lost before confirmation")
                return response.id
            }
            override suspend fun getById(id: String): IntakeResponse? = null
        }
        val viewModel = IntakeSubmissionViewModel(IntakeRepository(remote))
        val draft = IntakeResponse(category = "Food / Essentials")
        viewModel.submit(draft)
        advanceUntilIdle()
        assertNotNull(viewModel.state.value.error)
        viewModel.submit(draft.copy(id = "new-composition", createdAtMillis = draft.createdAtMillis + 1))
        advanceUntilIdle()
        assertEquals(draft.id, viewModel.state.value.savedId)
        assertEquals(listOf(draft, draft), attempts)
    }

}
