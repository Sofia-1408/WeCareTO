package com.example.wecareto_project.data

import androidx.test.platform.app.InstrumentationRegistry
import com.example.wecareto_project.WeCareApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test

class FirebaseLiveReadTest {
    @Test fun existingUserCanRetrieveASavedIntake(): Unit = runBlocking {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val arguments = InstrumentationRegistry.getArguments()
        assumeTrue(arguments.getString("firebaseLiveRead") == "true")
        val reference = requireNotNull(arguments.getString("intakeId"))
        val expectedUser = requireNotNull(arguments.getString("expectedUserId"))
        val application = instrumentation.targetContext.applicationContext as WeCareApplication
        val repository = application.container.intakeRepository
        val user = FirebaseAuth.getInstance().currentUser
        assertNotNull("The original signed-in session must still exist; no new account will be created.", user)
        assertTrue("The signed-in user must own the selected test submission.", user!!.uid == expectedUser)

        val result = repository.getById(reference)
        assertTrue("The repository must retrieve the intake from Firebase: ${if (result is DataResult.Failure) result.message else ""}",
            result is DataResult.Success)
        val intake = (result as DataResult.Success).value
        assertNotNull("The saved intake must exist.", intake)
        assertTrue("The saved reference must match.", intake!!.id == reference)
        assertNull("The stored answers must satisfy the app’s validation.", intake.validationError())

        // Compare against a fresh server response without printing any answers.
        val document = withTimeout(20_000) {
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .collection("intakes").document(reference).get(Source.SERVER).await()
        }
        assertTrue("The document must exist on the server.", document.exists())
        assertTrue("Category must match the stored answer.", intake.category == document.getString("category"))
        assertTrue("Recipient must match the stored answer.", intake.helpingSomeoneElse == document.getBoolean("helpingSomeoneElse"))
        assertTrue("Name must match the stored answer.", intake.name == document.getString("name"))
        assertTrue("Phone must match the stored answer.", intake.phone == document.getString("phone"))
        assertTrue("Location must match the stored answer.", intake.location == document.getString("location"))
        assertTrue("Details must match the stored answer.", intake.details == document.getString("details"))
        assertTrue("Submission time must match.", intake.createdAtMillis == document.getLong("createdAtMillis"))
    }
}
