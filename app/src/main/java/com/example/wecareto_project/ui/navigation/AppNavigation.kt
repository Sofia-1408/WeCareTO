package com.example.wecareto_project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.wecareto_project.ui.screens.SplashScreen
import com.example.wecareto_project.ui.screens.HomeScreen
import com.example.wecareto_project.ui.screens.AssistanceTypeScreen
import com.example.wecareto_project.ui.screens.IntakeScreen
import com.example.wecareto_project.ui.screens.RecommendedResourcesScreen
import com.example.wecareto_project.ui.screens.ResourceDetailsScreen
import com.example.wecareto_project.ui.screens.MapScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    // Store selected assistance category
    var selectedCategory by remember {
        mutableStateOf("")
    }

    // Store selected resource name
    var selectedResourceName by remember {
        mutableStateOf("")
    }

    NavHost(
        navController = navController,
        startDestination = Screen.SPLASH
    ) {

        // Splash Screen
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

        // Home Screen
        composable(Screen.HOME) {
            HomeScreen(
                onGetHelpClick = {
                    navController.navigate(Screen.ASSISTANCE_TYPE)
                }
            )
        }

        // Assistance Type Screen
        composable(Screen.ASSISTANCE_TYPE) {
            AssistanceTypeScreen(
                onContinueClick = { category ->
                    selectedCategory = category
                    navController.navigate(Screen.INTAKE)
                }
            )
        }

        // Intake Screen
        composable(Screen.INTAKE) {
            IntakeScreen(
                selectedCategory = selectedCategory,

                onFindResourcesClick = {
                    navController.navigate(
                        Screen.RECOMMENDED_RESOURCES
                    )
                },

                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Recommended Resources Screen
        composable(Screen.RECOMMENDED_RESOURCES) {
            RecommendedResourcesScreen(
                initialCategory = selectedCategory,

                onViewDetailsClick = { resourceName ->

                    selectedResourceName = resourceName

                    navController.navigate("resource_details")
                }
            )
        }

        // Resource Details Screen
        composable("resource_details") {
            ResourceDetailsScreen(
                resourceName = selectedResourceName,

                onBackClick = {
                    navController.popBackStack()
                },

                onGetDirectionsClick = {
                    navController.navigate("map")
                }
            )
        }

        // Map Screen
        composable("map") {
            MapScreen(
                resourceName = selectedResourceName,

                onBackClick = {
                    navController.popBackStack()
                },

                onGetDirectionsClick = {
                    // Directions integration will be added
                    // once resource addresses are verified
                }
            )
        }
    }
}