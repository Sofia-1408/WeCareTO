package com.example.wecareto_project.data

enum class AssistanceCategory(val label: String) {
    HOUSING("Housing / Shelter"), FOOD("Food / Essentials"),
    TRANSPORTATION("Transportation"), OTHER("Other Support");

    fun matches(category: String): Boolean = when (this) {
        HOUSING -> category in listOf(label, "Housing", "Shelter")
        FOOD -> category in listOf(label, "Food", "Essentials")
        else -> category == label
    }

    companion object {
        fun fromLabel(label: String): AssistanceCategory? = entries.firstOrNull {
            it.label.equals(label.trim(), ignoreCase = true) ||
                when (it) {
                    HOUSING -> label.trim().lowercase() in listOf("housing", "shelter")
                    FOOD -> label.trim().lowercase() in listOf("food", "essentials")
                    else -> false
                }
        }
    }
}
