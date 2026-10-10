package com.example.wecareto_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.wecareto_project.ui.navigation.AppNavigation
import com.example.wecareto_project.ui.theme.WeCareTOProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeCareTOProjectTheme {
                AppNavigation()
            }
        }
    }
}