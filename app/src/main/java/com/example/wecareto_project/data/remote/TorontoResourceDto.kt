package com.example.wecareto_project.data.remote

import com.google.gson.annotations.SerializedName
import com.example.wecareto_project.data.Resource

data class TorontoResourceDto(
    @SerializedName("PROGRAM_ID") val programId: String? = null,
    @SerializedName("PROGRAM_NAME") val programName: String? = null,
    @SerializedName("ORGANIZATION_NAME") val organization: String? = null,
    @SerializedName("LOCATION_ADDRESS") val address: String? = null,
    @SerializedName("LOCATION_CITY") val city: String? = null,
    @SerializedName("LOCATION_POSTAL_CODE") val postalCode: String? = null,
    @SerializedName("SECTOR") val sector: String? = null,
    @SerializedName("PROGRAM_MODEL") val model: String? = null,
    @SerializedName("OCCUPANCY_DATE") val reportedDate: String? = null
) {
    fun toResource(now: Long): Resource? {
        val id = programId?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val name = programName?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val location = listOfNotNull(address?.takeIf { it.isNotBlank() },
            city?.takeIf { it.isNotBlank() }, postalCode?.takeIf { it.isNotBlank() })
        return Resource(
            id = "toronto-shelter-$id", name = name, category = "Shelter",
            description = listOfNotNull(organization, sector, model)
                .filter { it.isNotBlank() }.joinToString(" · ") +
                ". This is a reported shelter program, not a live vacancy or a confirmed placement. Contact the provider or 211 before travelling.",
            address = if (address.isNullOrBlank()) "Address not published" else location.joinToString(", "),
            sourceUrl = SOURCE_URL, source = SOURCE, updatedAtMillis = now,
            reportedDate = reportedDate.orEmpty()
        )
    }
    companion object {
        const val SOURCE = "Toronto Open Data"
        const val SOURCE_URL = "https://open.toronto.ca/dataset/daily-shelter-overnight-service-occupancy-capacity/"
    }
}
