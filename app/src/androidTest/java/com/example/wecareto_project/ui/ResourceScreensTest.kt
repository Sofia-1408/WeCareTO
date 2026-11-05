package com.example.wecareto_project.ui
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.wecareto_project.data.ReferralResources
import com.example.wecareto_project.ui.screens.IntakeScreen
import com.example.wecareto_project.ui.screens.IntakeConfirmationScreen
import com.example.wecareto_project.ui.screens.RecommendedResourcesScreen
import com.example.wecareto_project.ui.viewmodel.IntakeFormViewModel
import com.example.wecareto_project.viewmodel.IntakeSubmissionState
import com.example.wecareto_project.viewmodel.ResourceUiState
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ResourceScreensTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun anonymousIntakeCanBeReviewedWithoutSaving() {
        //reviewing a form shouldn't require opting into firebase
        val form = IntakeFormViewModel().apply {
            startNewIntake("Food / Essentials")
        }

        var reviewed = false

        compose.setContent {
            IntakeScreen(
                form = form,
                savingAvailable = true,
                onReviewClick = {
                    reviewed = true
                },
                onBackClick = {}
            )
        }

        compose.onNodeWithText("Review Information")
            .performScrollTo()
            .performClick()
        compose.runOnIdle {
            assertTrue(reviewed)
            assertFalse(form.saveOnline)
        }
    }
    @Test
    fun confirmationAllowsBrowsingWithoutFirebaseSubmission() {
        //Anonymous browsing reaches resources without calling save callback
        val form = IntakeFormViewModel().apply {
            startNewIntake("Food / Essentials")
        }
        var continued = false
        var submitted = false
        compose.setContent {
            IntakeConfirmationScreen(
                form = form,
                submission = IntakeSubmissionState(),
                savingAvailable = true,
                onSaveIntake = {
                    submitted = true
                },
                onViewResourcesClick = {
                    continued = true
                },
                onEditClick = {}
            )
        }

        compose.onNodeWithText("View Resources Without Saving")
            .performScrollTo()
            .performClick()

        compose.runOnIdle {
            assertTrue(continued)
            assertFalse(submitted)
        }
    }

    @Test
    fun resourceFilterUsesRepositoryRowsAndNavigatesById() {
        //Only matching categories appear and IDs are passed onward
        var selected: String? = null
        compose.setContent {
            RecommendedResourcesScreen(
                initialCategory = "Food / Essentials",
                state = ResourceUiState(
                    resources = ReferralResources.all(),
                    isLoading = false
                ),
                onRetry = {},
                onViewDetailsClick = {
                    selected = it
                }
            )
        }

        compose.onNodeWithText("211 Ontario — Food / Essentials")
            .assertExists()

        compose.onNodeWithText("211 Ontario — Transportation")
            .assertDoesNotExist()

        compose.onNodeWithText("View Details")
            .performScrollTo()
            .performClick()

        compose.runOnIdle {
            assertEquals("211-food", selected)
        }
    }
}