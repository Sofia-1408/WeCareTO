package com.example.wecareto_project.data

import com.example.wecareto_project.data.local.ResourceStore
import com.example.wecareto_project.data.remote.ResourceRemoteDataSource
import com.example.wecareto_project.data.remote.TorontoResourceDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ResourceRepository(
    private val store: ResourceStore,
    private val remote: ResourceRemoteDataSource
) {
    val resources = store.observeResources()
    private val refreshLock = Mutex()

    suspend fun seedReferrals() = store.upsert(ReferralResources.all())
    suspend fun getResourceById(id: String) = store.findById(id)

    suspend fun refresh(): DataResult<Int> = refreshLock.withLock {
        try {
            val resources = remote.fetchResources()
            require(resources.isNotEmpty()) { "No usable resources were returned." }
            store.replaceSource(TorontoResourceDto.SOURCE, resources)
            DataResult.Success(resources.size)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            DataResult.Failure("Couldn’t update Toronto resources. Previously saved resources are still available. Please try again.")
        }
    }
}
