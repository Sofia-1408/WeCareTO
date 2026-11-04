package com.example.wecareto_project.data.remote

import com.example.wecareto_project.data.IntakeResponse

interface IntakeRemoteDataSource {
    val isConfigured: Boolean
    suspend fun save(response: IntakeResponse): String
    suspend fun getById(id: String): IntakeResponse?
}
