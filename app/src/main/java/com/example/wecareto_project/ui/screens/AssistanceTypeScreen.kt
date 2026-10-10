package com.example.wecareto_project.ui.screens

//Imports images
import com.example.wecareto_project.R

//For displaying images and arranging elements
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

//UI components
import androidx.compose.material3.*

//For remembering selected category
import androidx.compose.runtime.*

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
fun AssistanceTypeScreen(onContinueClick: (String) -> Unit) {

    //Stores selected assistance category or null
    var selectedCategory by remember {
        mutableStateOf<String?>(null)
    }

    //Main container for the screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "What kind of help\n" +
                    "do you need?",
            fontSize = 30.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(32.dp))
        //Scrollable container for the four category cards
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //Housing and shelter card
            AssistanceCategoryCard(
                title = "Housing / Shelter",
                description = "Find emergency shelters, affordable housing, and housing support programs.",
                //Displays icon
                iconRes = R.drawable.icon_housing,
                selected = selectedCategory == "Housing / Shelter",
                onClick = {
                    //Saves the selected category
                    selectedCategory = "Housing / Shelter"
                }
            )

            //Food and essentials card
            AssistanceCategoryCard(
                title = "Food / Essentials",
                description = "Find food banks and basic needs resources like food, toiletries, clothing, and more.",
                //Displays icon
                iconRes = R.drawable.icon_food,
                selected = selectedCategory == "Food / Essentials",
                onClick = {
                    //Saves the selected category
                    selectedCategory = "Food / Essentials"
                }
            )

            //Transportation card
            AssistanceCategoryCard(
                title = "Transportation",
                description = "Find transportation assistance and related resources.",
                //Displays icon
                iconRes = R.drawable.icon_transportation,
                selected = selectedCategory == "Transportation",
                onClick = {
                    //Saves the selected category
                    selectedCategory = "Transportation"
                }
            )

            //Other support card
            AssistanceCategoryCard(
                title = "Other Support",
                description = "Find additional resources and support services.",
                //Displays icon
                iconRes = R.drawable.icon_support,
                selected = selectedCategory == "Other Support",
                onClick = {
                    //Saves the selected category
                    selectedCategory = "Other Support"
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ///Continue button only selectable if a category is selected
        Button(
            onClick = {
                selectedCategory?.let { category ->
                    onContinueClick(category)
                }
            },
            //Disabled until a category is selected
            enabled = selectedCategory != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF333333),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFAAAAAA),
                disabledContentColor = Color.White
            )
        ) {
            Text(
                text = "Continue",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

//Reusable component for category cards
@Composable
fun AssistanceCategoryCard(
    title: String,
    description: String,
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                //Makes the card selectable
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        //Changes background when selected
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                Color(0xFFF0F0F0)
            } else {
                Color.White
            }
        ),
        //Changed the border when card is selected
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) {
                Color.Black
            } else {
                Color.DarkGray
            }
        )
    ) {
        //Styling
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Backgroudn behind the icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Color(0xFFEAEAEA),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            //Displays category title and description and arrow on the right side of the card
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    color = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "›",
                fontSize = 32.sp,
                color = Color.DarkGray
            )
        }
    }
}