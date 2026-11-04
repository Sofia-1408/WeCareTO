package com.example.wecareto_project.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.wecareto_project.MainActivity
import org.junit.Rule
import org.junit.Test

class AppFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun anonymousBrowsingReachesDetailsAndMapAndSurvivesActivityRecreation() {
        compose.waitUntil(20_000) {
            compose.onAllNodesWithText("Get Help").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Get Help").performClick()
        compose.onNodeWithText("Food / Essentials").performScrollTo().performClick()
        compose.onNodeWithText("Continue").performClick()
        compose.onNodeWithText("Find Resources Without Saving").performScrollTo().performClick()
        compose.waitUntil(20_000) {
            compose.onAllNodesWithText("211 Ontario — Food / Essentials").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("View Details").performScrollTo().performClick()
        compose.onNodeWithText("Call 211").assertExists()
        compose.activityRule.scenario.recreate()
        compose.waitUntil(20_000) {
            compose.onAllNodesWithText("Call 211").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Source: 211 Ontario").assertExists()
        compose.onNodeWithText("View Map").performScrollTo().performClick()
        compose.onNodeWithText("Map preview coming soon").assertExists()
        compose.onNodeWithText("Get Directions").assertIsNotEnabled()
        compose.activityRule.scenario.recreate()
        compose.waitUntil(20_000) {
            compose.onAllNodesWithText("Map preview coming soon").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Back").performScrollTo().performClick()
        compose.onNodeWithText("Call 211").assertExists()
    }
}
