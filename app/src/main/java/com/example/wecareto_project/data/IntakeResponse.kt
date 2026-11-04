package com.example.wecareto_project.data

import java.util.UUID

data class IntakeResponse(
    val id: String = UUID.randomUUID().toString(),
    val category: String,
    val helpingSomeoneElse: Boolean = false,
    val name: String = "",
    val phone: String = "",
    val location: String = "",
    val details: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
) {
    fun validationError(): String? = when {
        id.isBlank() || id.length > 100 -> "The submission reference is invalid."
        createdAtMillis <= 0 -> "The submission date is invalid."
        AssistanceCategory.entries.none { it.label == category } -> "Choose an assistance category."
        name.length > 100 -> "Keep the name under 100 characters."
        phone.isNotBlank() && (!phone.matches(Regex("[+0-9() .-]+")) ||
            phone.count(Char::isDigit) != 10) -> "Enter a 10-digit phone number or leave it blank."
        phone.length > 30 -> "The phone number is too long."
        location.length > 200 -> "Keep the location under 200 characters."
        details.length > 2000 -> "Keep additional details under 2,000 characters."
        else -> null
    }
}
