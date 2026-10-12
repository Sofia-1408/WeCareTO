package com.example.wecareto_project.data

object ResourceRepository {

    val resources = listOf(
        Resource(
            id = 1,
            name = "Hope Community Shelter",
            category = "Shelter",
            description = "Fictional shelter resource used for testing the WeCareTO application. Service details have not been verified.",
            address = "Not verified",
            phone = "Not available",
            operatingHours = "Not verified"
        ),
        Resource(
            id = 2,
            name = "Riverview Food Bank",
            category = "Food",
            description = "Fictional food assistance resource used for testing the WeCareTO application. Service details have not been verified.",
            address = "Not verified",
            phone = "Not available",
            operatingHours = "Not verified"
        ),
        Resource(
            id = 3,
            name = "Bright Futures Housing",
            category = "Housing",
            description = "Fictional housing assistance resource used for testing the WeCareTO application. Service details have not been verified.",
            address = "Not verified",
            phone = "Not available",
            operatingHours = "Not verified"
        )
    )

    fun getResourceByName(name: String): Resource? {
        return resources.find { it.name == name }
    }
}