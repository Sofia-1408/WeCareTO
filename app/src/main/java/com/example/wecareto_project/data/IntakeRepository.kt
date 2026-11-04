package com.example.wecareto_project.data

import com.example.wecareto_project.data.remote.IntakeRemoteDataSource
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

class IntakeRepository(private val remote: IntakeRemoteDataSource) {
    val isConfigured get() = remote.isConfigured

    suspend fun submit(response: IntakeResponse): DataResult<String> {
        response.validationError()?.let { return DataResult.Failure(it) }
        if (!isConfigured) return DataResult.Failure("Online saving isn’t set up yet. You can still browse resources without saving your information.")
        return try {
            DataResult.Success(withTimeout(20_000) { remote.save(response) })
        } catch (_: TimeoutCancellationException) {
            DataResult.Failure("Saving is taking longer than expected. The save may still finish; retry to confirm the same submission.")
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: FirebaseAuthException) {
            DataResult.Failure(intakeAuthErrorMessage(error.errorCode))
        } catch (error: FirebaseFirestoreException) {
            DataResult.Failure(intakeDatabaseErrorMessage(error.code.name))
        } catch (_: FirebaseNetworkException) {
            DataResult.Failure("Couldn’t connect to Firebase. Check your internet connection and try again.")
        } catch (error: Exception) {
            DataResult.Failure("We couldn’t confirm the save (${error.javaClass.simpleName}). Please try again.")
        }
    }

    suspend fun getById(id: String): DataResult<IntakeResponse?> {
        if (!isConfigured) return DataResult.Failure("Online saving isn’t set up yet.")
        return try {
            DataResult.Success(withTimeout(20_000) { remote.getById(id) })
        } catch (_: TimeoutCancellationException) {
            DataResult.Failure("Couldn’t retrieve the saved intake. Please try again.")
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            DataResult.Failure("Couldn’t retrieve the saved intake. Please try again.")
        }
    }
}

internal fun intakeAuthErrorMessage(code: String): String = when (code) {
    "ERROR_OPERATION_NOT_ALLOWED", "ERROR_ADMIN_RESTRICTED_OPERATION" ->
        "Anonymous sign-in is disabled for this Firebase project. Enable it in Authentication → Sign-in method."
    "ERROR_CONFIGURATION_NOT_FOUND" ->
        "Firebase Authentication hasn’t been set up for this project."
    "ERROR_INVALID_API_KEY", "ERROR_APP_NOT_AUTHORIZED" ->
        "The app’s Firebase configuration isn’t authorized for this project ($code)."
    else -> "Firebase sign-in failed ($code). Please try again."
}

internal fun intakeDatabaseErrorMessage(code: String): String = when (code) {
    "PERMISSION_DENIED" -> "Firebase rejected this save. Check the project’s published Firestore access rules."
    "UNAUTHENTICATED" -> "Firebase couldn’t verify your sign-in session. Please try again."
    "UNAVAILABLE", "DEADLINE_EXCEEDED" -> "The database couldn’t be reached. Check your connection and try again."
    "RESOURCE_EXHAUSTED" -> "The database’s usage limit has been reached. Please try again later."
    else -> "The database couldn’t confirm the save ($code). Please try again."
}
