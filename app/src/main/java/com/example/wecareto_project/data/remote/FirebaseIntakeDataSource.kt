package com.example.wecareto_project.data.remote

import com.example.wecareto_project.data.IntakeResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

class FirebaseIntakeDataSource(
    private val auth: FirebaseAuth?,
    private val firestore: FirebaseFirestore?
) : IntakeRemoteDataSource {
    override val isConfigured get() = auth != null && firestore != null

    private suspend fun userId(): String {
        check(isConfigured) { "Firebase is not configured." }
        return auth!!.currentUser?.uid ?: auth.signInAnonymously().await().user!!.uid
    }

    override suspend fun save(response: IntakeResponse): String {
        val uid = userId()
        val fields = mapOf(
            "id" to response.id, "category" to response.category,
            "helpingSomeoneElse" to response.helpingSomeoneElse,
            "name" to response.name, "phone" to response.phone,
            "location" to response.location, "details" to response.details,
            "createdAtMillis" to response.createdAtMillis
        )
        // Reusing the draft ID lets a retry confirm the same submission.
        firestore!!.collection("users").document(uid).collection("intakes")
            .document(response.id).set(fields).await()
        return response.id
    }

    override suspend fun getById(id: String): IntakeResponse? {
        val uid = userId()
        val doc = firestore!!.collection("users").document(uid).collection("intakes")
            .document(id).get(Source.SERVER).await()
        if (!doc.exists()) return null
        return IntakeResponse(
            id = doc.id, category = doc.getString("category").orEmpty(),
            helpingSomeoneElse = doc.getBoolean("helpingSomeoneElse") ?: false,
            name = doc.getString("name").orEmpty(), phone = doc.getString("phone").orEmpty(),
            location = doc.getString("location").orEmpty(), details = doc.getString("details").orEmpty(),
            createdAtMillis = doc.getLong("createdAtMillis") ?: 0
        )
    }
}
