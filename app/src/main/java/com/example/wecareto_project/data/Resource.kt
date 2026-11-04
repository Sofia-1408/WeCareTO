package com.example.wecareto_project.data

data class Resource(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val address: String = "Address not supplied",
    val phone: String = "",
    val operatingHours: String = "Hours not supplied; contact the provider.",
    val sourceUrl: String,
    val source: String,
    val updatedAtMillis: Long,
    val reportedDate: String = ""
)
