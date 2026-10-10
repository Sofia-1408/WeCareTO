package com.example.wecareto_project.ui.navigation

//Imports Navigation Compose components
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

//Imports screens
import com.example.wecareto_project.ui.screens.SplashScreen
import com.example.wecareto_project.ui.screens.HomeScreen
import com.example.wecareto_project.ui.screens.AssistanceTypeScreen
import com.example.wecareto_project.ui.screens.RecommendedResourcesScreen

//Used for temporary messages (testing purposes)
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

//For Compose UI
import androidx.compose.runtime.Composable

@Composable
fun AppNavigation() {

    //Controller that manages navigation
    val navController = rememberNavController()
    //Toast messages
    val context = LocalContext.current
    //Defines screen destinations
    NavHost(
        navController = navController,
        //Splash is the first screen that is displayed
        startDestination = Screen.SPLASH
    ) {
        //SplashScreen
        composable(Screen.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    //Automatically navigate to HomeScreen
                    navController.navigate(Screen.HOME) {
                        //Remove Splash from navigation history so we don't end up being unable to exit the app
                        popUpTo(Screen.SPLASH) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        //HomeScreen
        composable(Screen.HOME) {
            HomeScreen(
                onGetHelpClick = {
                    //Navigates to AssistanceTypeScreen
                    navController.navigate(Screen.ASSISTANCE_TYPE)
                }
            )
        }
        //AssistanceTypeScreen
        composable(Screen.ASSISTANCE_TYPE) {
            AssistanceTypeScreen(
                onContinueClick = { _ ->
                    //Temporarily navigates to recommended resources for UI testing,
                    //intake screen will be added later
                    navController.navigate(Screen.RECOMMENDED_RESOURCES)
                }
            )
        }
        //RecommendedResourcesScreen
        composable(Screen.RECOMMENDED_RESOURCES) {
            RecommendedResourcesScreen(
                onViewDetailsClick = { resourceName ->
                    //Temporary message to test View Details buttons,
                    //details screen will be added later
                    Toast.makeText(
                        context,
                        "Selected: $resourceName",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}