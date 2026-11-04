package com.example.wecareto_project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wecareto_project.WeCareApplication
import com.example.wecareto_project.ui.screens.*
import com.example.wecareto_project.viewmodel.BackendViewModelFactory
import com.example.wecareto_project.viewmodel.IntakeSubmissionViewModel
import com.example.wecareto_project.viewmodel.ResourceViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as WeCareApplication
    val factory = remember(application) { BackendViewModelFactory(application.container) }
    val resourcesViewModel: ResourceViewModel = viewModel(factory = factory)
    val resources by resourcesViewModel.state.collectAsStateWithLifecycle()
    var selectedCategory by rememberSaveable { mutableStateOf("") }

    NavHost(navController = navController, startDestination = Screen.SPLASH) {
        composable(Screen.SPLASH) {
            SplashScreen(onSplashFinished = {
                navController.navigate(Screen.HOME) { popUpTo(Screen.SPLASH) { inclusive = true } }
            })
        }
        composable(Screen.HOME) {
            HomeScreen(onGetHelpClick = { navController.navigate(Screen.ASSISTANCE_TYPE) })
        }
        composable(Screen.ASSISTANCE_TYPE) {
            AssistanceTypeScreen(onContinueClick = { category ->
                selectedCategory = category
                navController.navigate(Screen.INTAKE)
            })
        }
        composable(Screen.INTAKE) {
            val intakeViewModel: IntakeSubmissionViewModel = viewModel(factory = factory)
            val submission by intakeViewModel.state.collectAsStateWithLifecycle()
            IntakeScreen(
                selectedCategory = selectedCategory,
                onFindResourcesClick = { navController.navigate(Screen.RECOMMENDED_RESOURCES) },
                onSaveIntake = intakeViewModel::submit,
                submission = submission,
                savingAvailable = intakeViewModel.isConfigured,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.RECOMMENDED_RESOURCES) {
            RecommendedResourcesScreen(
                initialCategory = selectedCategory, state = resources,
                onRetry = resourcesViewModel::refresh,
                onViewDetailsClick = { id -> navController.navigate("resource_details/$id") }
            )
        }
        composable("resource_details/{resourceId}", arguments = listOf(navArgument("resourceId") { type = NavType.StringType })) { entry ->
            val id = entry.arguments?.getString("resourceId")
            ResourceDetailsScreen(
                resource = resources.resources.firstOrNull { it.id == id },
                isLoading = resources.isLoading,
                onBackClick = { navController.popBackStack() },
                onViewMapClick = { id?.let { navController.navigate("map/$it") } }
            )
        }
        composable("map/{resourceId}", arguments = listOf(navArgument("resourceId") { type = NavType.StringType })) { entry ->
            val id = entry.arguments?.getString("resourceId")
            val resource = resources.resources.firstOrNull { it.id == id }
            MapScreen(
                resourceName = resource?.name ?: if (resources.isLoading) "Loading resource…" else "Resource unavailable",
                onBackClick = { navController.popBackStack() },
                onGetDirectionsClick = { }
            )
        }
    }
}
