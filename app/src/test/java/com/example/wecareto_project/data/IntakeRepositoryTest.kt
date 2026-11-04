package com.example.wecareto_project.data

import com.example.wecareto_project.data.remote.IntakeRemoteDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class IntakeRepositoryTest {
    private val draft = IntakeResponse(category = "Food / Essentials")

    @Test fun optionalFieldsCanBeEmpty() {
        assertNull(draft.validationError())
        assertNull(draft.copy(phone = "(416) 555-0100").validationError())
        assertNotNull(draft.copy(phone = "letters4165550100").validationError())
        assertNotNull(draft.copy(phone = "123").validationError())
        assertNotNull(draft.copy(details = "x".repeat(2001)).validationError())
        assertNotNull(draft.copy(category = "unknown").validationError())
    }

    @Test fun invalidDraftNeverReachesTheRemoteStore() = runTest {
        val remote = FakeIntakeRemote()
        val result = IntakeRepository(remote).submit(draft.copy(phone = "123"))
        assertTrue(result is DataResult.Failure)
        assertNull(remote.saved)
    }

    @Test fun missingConfigurationDoesNotPretendToSave() = runTest {
        val remote = FakeIntakeRemote(configured = false)
        assertTrue(IntakeRepository(remote).submit(draft) is DataResult.Failure)
        assertNull(remote.saved)
    }

    @Test fun savedIntakeCanBeRetrievedAndRetryUsesTheSameId() = runTest {
        val remote = FakeIntakeRemote(); val repository = IntakeRepository(remote)
        assertEquals(DataResult.Success(draft.id), repository.submit(draft))
        assertEquals(DataResult.Success(draft.id), repository.submit(draft))
        assertEquals(DataResult.Success(draft), repository.getById(draft.id))
        assertEquals(DataResult.Success<IntakeResponse?>(null), repository.getById("missing"))
    }

    @Test fun serverErrorsAreReportedWithoutExposingPersonalData() = runTest {
        val repository = IntakeRepository(FakeIntakeRemote(failure = IllegalStateException("private name")))
        val result = repository.submit(draft) as DataResult.Failure
        assertFalse(result.message.contains("private name"))
    }

    @Test fun timeoutDoesNotReportSuccess() = runTest {
        val remote = FakeIntakeRemote(waitMillis = 30_000)
        val result = IntakeRepository(remote).submit(draft)
        assertTrue(result is DataResult.Failure)
    }

    @Test fun setupAndPermissionErrorsDoNotMasqueradeAsNetworkFailures() {
        assertTrue(intakeAuthErrorMessage("ERROR_OPERATION_NOT_ALLOWED").contains("disabled"))
        assertTrue(intakeAuthErrorMessage("ERROR_CONFIGURATION_NOT_FOUND").contains("hasn’t been set up"))
        assertTrue(intakeDatabaseErrorMessage("PERMISSION_DENIED").contains("access rules"))
        assertFalse(intakeDatabaseErrorMessage("PERMISSION_DENIED").contains("connection"))
        assertTrue(intakeAuthErrorMessage("ERROR_UNKNOWN").contains("ERROR_UNKNOWN"))
    }

    @Test fun cancellationPropagates() = runTest {
        val repository = IntakeRepository(FakeIntakeRemote(failure = CancellationException()))
        try { repository.submit(draft); fail("Cancellation should propagate") }
        catch (_: CancellationException) { }
    }
}

class FakeIntakeRemote(
    private val configured: Boolean = true,
    private val failure: Exception? = null,
    private val waitMillis: Long = 0
) : IntakeRemoteDataSource {
    var saved: IntakeResponse? = null
    override val isConfigured get() = configured
    override suspend fun save(response: IntakeResponse): String {
        delay(waitMillis)
        failure?.let { throw it }
        saved = response
        return response.id
    }
    override suspend fun getById(id: String) = saved?.takeIf { it.id == id }
}
