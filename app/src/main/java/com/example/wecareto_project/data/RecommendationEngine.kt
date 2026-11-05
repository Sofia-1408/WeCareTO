package com.example.wecareto_project.data

//Rule based suggestion
data class RecommendationPlan(
    val category: String,
    val preferredLocation: String,
    val explanation: String
)

object RecommendationEngine {

    fun forIntake(
        intake: IntakeResponse,
        immediateDanger: SafetyAnswer?,
        suicideConcern: SafetyAnswer?
    ): RecommendationPlan { //Convert selected label into resource category
        val category =
            AssistanceCategory.fromLabel(intake.category)?.label
                ?: "All"
        val location = intake.location.trim()
        val locationExplanation = if (location.isBlank()) {
            "No location preference was entered."
        } else {
            "Addresses containing your entered location are listed " +
                    "first when available; this is text matching, " +
                    "not a distance calculation."
        }
        val safetyExplanation = when { //Safety selection only affect guidence
            immediateDanger == SafetyAnswer.YES ||
                    immediateDanger == SafetyAnswer.NOT_SURE ->
                "If someone may be in immediate danger, call 911 " +
                        "rather than relying on these listings."
            suicideConcern == SafetyAnswer.YES ->
                "For suicide related concerns, call or text 988 " +
                        "for crisis support. Call 911 if there is immediate danger."
            else -> ""
        }
        return RecommendationPlan( //The screen uses this to choose a filter and explain the suggestion
            category = category,
            preferredLocation = location,
            explanation =
                "Resources are based on your selected assistance type " +
                        "($category). " +
                        locationExplanation + " " +
                        safetyExplanation +
                        " The free text details are not automatically assessed " +
                        "listings do not confirm service availability."
        )
    }
}