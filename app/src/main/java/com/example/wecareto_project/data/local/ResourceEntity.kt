package com.example.wecareto_project.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wecareto_project.data.Resource

@Entity(tableName = "resources")
data class ResourceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val description: String,
    val address: String,
    val phone: String,
    val operatingHours: String,
    val sourceUrl: String,
    val source: String,
    val updatedAtMillis: Long,
    val reportedDate: String
) {
    fun toResource() = Resource(id, name, category, description, address, phone,
        operatingHours, sourceUrl, source, updatedAtMillis, reportedDate)
}

fun Resource.toEntity() = ResourceEntity(id, name, category, description, address,
    phone, operatingHours, sourceUrl, source, updatedAtMillis, reportedDate)
