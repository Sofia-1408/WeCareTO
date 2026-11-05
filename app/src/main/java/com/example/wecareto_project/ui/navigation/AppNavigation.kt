package com.example.wecareto_project.ui.navigation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wecareto_project.WeCareApplication
import com.example.wecareto_project.data.RecommendationEngine
import com.example.wecareto_project.ui.screens.*
import com.example.wecareto_project.viewmodel.BackendViewModelFactory
import com.example.wecareto_project.ui.viewmodel.IntakeFormViewModel
import com.example.wecareto_project.viewmodel.IntakeSubmissionViewModel
import com.example.wecareto_project.viewmodel.ResourceViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val application =
        LocalContext.current.applicationContext as WeCareApplication
    val factory = remember(application) {
        BackendViewModelFactory(application.container)
    }
    val resourceViewModel: ResourceViewModel = viewModel(
        factory = factory
    )
    val resources by resourceViewModel.state
        .collectAsStateWithLifecycle()
    val intakeForm: IntakeFormViewModel = viewModel()
    val savingAvailable =
        application.container.intakeRepository.isConfigured
    NavHost(
        navController = navController,
        startDestination = Screen.SPLASH
    ) {

        //Splash
        composable(Screen.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.HOME) {
                        popUpTo(Screen.SPLASH) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        //Home
        composable(Screen.HOME) {
            HomeScreen(
                onGetHelpClick = {
                    navController.navigate(
                        Screen.ASSISTANCE_TYPE
                    )
                }
            )
        }

        //Assistance selection
        composable(Screen.ASSISTANCE_TYPE) {
            AssistanceTypeScreen(
                onContinueClick = { category ->
                    intakeForm.startNewIntake(category)
                    navController.navigate(Screen.INTAKE)
                }
            )
        }

        //Intake
        composable(Screen.INTAKE) {
            IntakeScreen(
                form = intakeForm,
                savingAvailable = savingAvailable,

                onReviewClick = {
                    navController.navigate(
                        Screen.INTAKE_CONFIRMATION
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        //Intake confirmation
        composable(Screen.INTAKE_CONFIRMATION) {
            val submissionViewModel: IntakeSubmissionViewModel =
                viewModel(factory = factory)
            val submission by submissionViewModel.state
                .collectAsStateWithLifecycle()
            IntakeConfirmationScreen(
                form = intakeForm,
                submission = submission,
                savingAvailable = savingAvailable,
                onSaveIntake = {
                    submissionViewModel.submit(
                        intakeForm.toIntakeResponse()
                    )
                },
                onViewResourcesClick = {
                    navController.navigate(
                        Screen.RECOMMENDED_RESOURCES
                    )
                },
                onEditClick = {
                    navController.popBackStack()
                }
            )
        }

        //Recommended resources
        composable(Screen.RECOMMENDED_RESOURCES) {
            val plan = RecommendationEngine.forIntake(
                intakeForm.toIntakeResponse(),
                intakeForm.immediateDanger,
                intakeForm.suicideConcern
            )
            RecommendedResourcesScreen(
                initialCategory = plan.category,
                state = resources,
                onRetry = resourceViewModel::refresh,
                preferredLocation = plan.preferredLocation,
                recommendationNote = plan.explanation,
                onViewDetailsClick = { id ->
                    navController.navigate(
                        "resource_details/$id"
                    )
                }
            )
        }

        //Resource Details
        composable(
            route = "resource_details/{resourceId}",
            arguments = listOf(
                navArgument("resourceId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val id = entry.arguments?.getString("resourceId")
            ResourceDetailsScreen(
                resource = resources.resources.firstOrNull {
                    it.id == id
                },
                isLoading = resources.isLoading,
                onBackClick = {
                    navController.popBackStack()
                },
                onViewMapClick = {
                    id?.let {
                        navController.navigate("map/$it")
                    }
                }
            )
        }

        //Map placeholder (Will be implemented in the future)
        composable(
            route = "map/{resourceId}",
            arguments = listOf(
                navArgument("resourceId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val id = entry.arguments?.getString("resourceId")
            val resource = resources.resources.firstOrNull {
                it.id == id
            }
            MapScreen(
                resourceName = resource?.name
                    ?: if (resources.isLoading) {
                        "Loading resource…"
                    } else {
                        "Resource unavailable"
                    },
                onBackClick = {
                    navController.popBackStack()
                },
                onGetDirectionsClick = {
                    //For the future
                }
            )
        }
    }
}