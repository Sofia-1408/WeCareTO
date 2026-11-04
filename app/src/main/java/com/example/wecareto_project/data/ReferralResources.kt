package com.example.wecareto_project.data

object ReferralResources {
    // These are referral services, not invented providers or guaranteed placements.
    fun all() = AssistanceCategory.entries.map { category ->
        Resource(
            id = "211-${category.name.lowercase()}",
            name = "211 Ontario — ${category.label}",
            category = category.label,
            description = "Contact 211 for help finding social services and community supports for ${category.label.lowercase()} in Toronto. This entry is a referral service.",
            address = "Toronto and Ontario; contact 211 for local referrals",
            phone = "211", operatingHours = "Contact 211 for service information.",
            sourceUrl = "https://211ontario.ca/", source = "211 Ontario", updatedAtMillis = 0
        )
    }
}
