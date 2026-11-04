package com.example.wecareto_project.di

import android.content.Context
import androidx.room.Room
import com.example.wecareto_project.data.IntakeRepository
import com.example.wecareto_project.data.ResourceRepository
import com.example.wecareto_project.data.local.RoomResourceStore
import com.example.wecareto_project.data.local.WeCareDatabase
import com.example.wecareto_project.data.remote.FirebaseIntakeDataSource
import com.example.wecareto_project.data.remote.TorontoOpenDataApi
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(context, WeCareDatabase::class.java, "wecare.db").build()
    private val httpClient = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS).callTimeout(30, TimeUnit.SECONDS).build()
    val resourceRepository = ResourceRepository(RoomResourceStore(database.resourceDao()), TorontoOpenDataApi(httpClient))

    private val firebaseApp = FirebaseApp.initializeApp(context)
    private val auth = firebaseApp?.let { FirebaseAuth.getInstance(it) }
    private val firestore = firebaseApp?.let {
        FirebaseFirestore.getInstance(it).apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build()).build()
        }
    }
    val intakeRepository = IntakeRepository(FirebaseIntakeDataSource(auth, firestore))
}
