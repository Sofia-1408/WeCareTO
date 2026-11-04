package com.example.wecareto_project.data.local

import com.example.wecareto_project.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ResourceStore {
    fun observeResources(): Flow<List<Resource>>
    suspend fun findById(id: String): Resource?
    suspend fun upsert(resources: List<Resource>)
    suspend fun replaceSource(source: String, resources: List<Resource>)
}

class RoomResourceStore(private val dao: ResourceDao) : ResourceStore {
    override fun observeResources() = dao.observeResources().map { rows -> rows.map { it.toResource() } }
    override suspend fun findById(id: String) = dao.findById(id)?.toResource()
    override suspend fun upsert(resources: List<Resource>) = dao.upsert(resources.map { it.toEntity() })
    override suspend fun replaceSource(source: String, resources: List<Resource>) =
        dao.replaceSource(source, resources.map { it.toEntity() })
}
