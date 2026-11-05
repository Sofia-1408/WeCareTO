package com.example.wecareto_project.data
import org.junit.Assert.*
import org.junit.Test

class RecommendationEngineTest {

    @Test
    fun categoryAndLocationAreUsedWithoutClaimingDistance() { //verify that the original category and entered location are preserved
        val plan = RecommendationEngine.forIntake(
            IntakeResponse(
                category = "Housing / Shelter",
                location = "M5V"
            ),
            SafetyAnswer.NO,
            SafetyAnswer.NO
        )
        assertEquals("Housing / Shelter", plan.category)
        assertEquals("M5V", plan.preferredLocation)
        assertTrue(plan.explanation.contains("text matching"))
    }

    @Test
    fun explicitEmergencyAnswersGiveContactAdvice() { //verify that explicit safety answers produce 911 and 988 guidance
        val intake = IntakeResponse(
            category = "Other Support"
        )
        val emergency = RecommendationEngine.forIntake(
            intake,
            SafetyAnswer.YES,
            SafetyAnswer.NO
        )
        val suicideConcern = RecommendationEngine.forIntake(
            intake,
            SafetyAnswer.NO,
            SafetyAnswer.YES
        )
        assertTrue(emergency.explanation.contains("911"))
        assertTrue(suicideConcern.explanation.contains("988"))
    }
}