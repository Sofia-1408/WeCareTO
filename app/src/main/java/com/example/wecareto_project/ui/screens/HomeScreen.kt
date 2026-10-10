package com.example.wecareto_project.ui.screens

//Imports images
import com.example.wecareto_project.R

//For displaying images and arranging elements
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

//UI components
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text

//Compose functionality
import androidx.compose.runtime.Composable

//UI colors styling etc..
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onGetHelpClick: () -> Unit) {
    //Container for the HomeScreen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Border around the logo
        Box(
            modifier = Modifier
                .size(190.dp)
                .border(
                    width = 3.dp,
                    color = Color.DarkGray,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            //Displays WeCareTO logo
            Image(
                painter = painterResource(
                    id = R.drawable.wecareto_logo
                ),
                contentDescription = "WeCareTO logo",
                modifier = Modifier.size(140.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        //Displays the app name
        Text(
            text = "WeCareTO",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        //App description
        Text(
            text = "Find immediate help and community resources " +
                    "for housing, food, and essential needs. " +
                    "Connect with local organizations and get " +
                    "the support you need.",
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        //Button that navigates to AssistanceTypeScreen
        Button(
            onClick = onGetHelpClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF333333),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Get Help",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}