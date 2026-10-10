package com.example.wecareto_project.ui.screens

//Imports images
import com.example.wecareto_project.R

//For displaying images and arranging elements
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape

//UI components
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text

//Compose functionailty
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

//UI colors styling etc..
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//Simulates loading
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    //Runs when the splash screen is loaded
    LaunchedEffect(Unit) {
        //Keep Splash visible for 2 seconds (to simulate loading)
        delay(2000)
        //Navigates to HomeScreen
        onSplashFinished()
    }
    //Styling
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        //Logo and application name
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Border
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .border(
                        width = 3.dp,
                        color = Color.DarkGray,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                //Displayed the logo
                Image(
                    painter = painterResource(
                        id = R.drawable.wecareto_logo
                    ),
                    contentDescription = "WeCareTO logo",
                    modifier = Modifier.size(125.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            //Displays the app name
            Text(
                text = "WeCareTO",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        //Loading indicator near the bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(42.dp),
                color = Color.DarkGray,
                strokeWidth = 4.dp
            )
            //Text below the loading indicator
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading...",
                fontSize = 18.sp,
                color = Color.DarkGray
            )
        }
    }
}