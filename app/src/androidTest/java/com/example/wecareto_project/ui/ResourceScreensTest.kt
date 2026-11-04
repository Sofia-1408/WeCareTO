package com.example.wecareto_project.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import com.example.wecareto_project.data.ReferralResources
import com.example.wecareto_project.ui.screens.IntakeScreen
import com.example.wecareto_project.ui.screens.RecommendedResourcesScreen
import com.example.wecareto_project.viewmodel.IntakeSubmissionState
import com.example.wecareto_project.viewmodel.ResourceUiState
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ResourceScreensTest {
    @get:Rule val compose = createComposeRule()

    @Test fun findingResourcesWithoutConsentDoesNotSubmitTheIntake() {
        var continued = false
        var submitted = false
        compose.setContent {
            IntakeScreen(selectedCategory = "Food / Essentials", onFindResourcesClick = { continued = true },
                onSaveIntake = { submitted = true }, submission = IntakeSubmissionState(),
                savingAvailable = true, onBackClick = {})
        }
        compose.onNodeWithText("Find Resources Without Saving").performScrollTo().performClick()
        compose.runOnIdle { assertTrue(continued); assertFalse(submitted) }
    }

    @Test fun resourceFilterUsesRepositoryRowsAndNavigatesById() {
        var selected: String? = null
        compose.setContent {
            RecommendedResourcesScreen(initialCategory = "Food / Essentials",
                state = ResourceUiState(resources = ReferralResources.all(), isLoading = false),
                onRetry = {}, onViewDetailsClick = { selected = it })
        }
        compose.onNodeWithText("211 Ontario — Food / Essentials").assertExists()
        compose.onNodeWithText("211 Ontario — Transportation").assertDoesNotExist()
        compose.onNodeWithText("View Details").performScrollTo().performClick()
        compose.runOnIdle { assertEquals("211-food", selected) }
    }
}
