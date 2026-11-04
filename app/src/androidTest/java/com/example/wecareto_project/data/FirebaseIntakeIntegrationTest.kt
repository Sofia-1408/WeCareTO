package com.example.wecareto_project.data

import androidx.test.platform.app.InstrumentationRegistry
import com.example.wecareto_project.data.remote.FirebaseIntakeDataSource
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.tasks.await
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test

class FirebaseIntakeIntegrationTest {
    @Test fun anonymousUserCanSaveAndRetrieveAnIntakeThroughTheAndroidSdk(): Unit = runBlocking {
        assumeTrue(InstrumentationRegistry.getArguments().getString("firebaseEmulator") == "true")
        val host = InstrumentationRegistry.getArguments().getString("firebaseHost") ?: "10.0.2.2"
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val app = FirebaseApp.initializeApp(context, FirebaseOptions.Builder()
            .setProjectId("demo-wecareto").setApplicationId("1:1234567890:android:wecareto")
            .setApiKey("fake-api-key").build(), "intake-test-${java.util.UUID.randomUUID()}")
        val auth = FirebaseAuth.getInstance(app).apply { useEmulator(host, 9099) }
        val firestore = FirebaseFirestore.getInstance(app).apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build()).build()
            useEmulator(host, 8080)
        }
        try {
            // Give the local Auth emulator time to start before measuring a save.
            withTimeout(60_000) { auth.signInAnonymously().await() }
            val repository = IntakeRepository(FirebaseIntakeDataSource(auth, firestore))
            val intake = IntakeResponse(category = "Food / Essentials", details = "Synthetic test submission")
            assertEquals(DataResult.Success(intake.id), repository.submit(intake))
            assertEquals(DataResult.Success(intake.id), repository.submit(intake))
            assertEquals(DataResult.Success(intake), repository.getById(intake.id))
            firestore.collection("users").document(auth.currentUser!!.uid)
                .collection("intakes").document(intake.id).delete().await()
        } finally {
            auth.currentUser?.delete()?.await()
            firestore.terminate().await()
            app.delete()
        }
    }
}
