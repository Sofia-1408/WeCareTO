package com.example.wecareto_project.ui.screens

//For displaying images and arranging elements
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

//UI components
import androidx.compose.material3.*

//For remembering selected category
import androidx.compose.runtime.*

//UI colors styling etc..
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecommendedResourcesScreen(
    onViewDetailsClick: (String) -> Unit
) {
    //Stores texted entered in the search bar and category selected
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    //Categories
    val categories = listOf(
        "All",
        "Shelter",
        "Food",
        "Housing",
        "Essentials"
    )
    fun matchesResource(
        name: String,
        category: String,
        description: String,
        location: String
    ): Boolean {
        val matchesCategory =
            selectedFilter == "All" || selectedFilter == category
        val query = searchText.trim()
        val matchesSearch =
            query.isEmpty() ||
                    name.contains(query, ignoreCase = true) ||
                    category.contains(query, ignoreCase = true) ||
                    description.contains(query, ignoreCase = true) ||
                    location.contains(query, ignoreCase = true)
        //Returns value only if both search and category matches
        return matchesCategory && matchesSearch
    }
    //Determines weather the card should be displayed
    val showShelter = matchesResource(
        name = "Hope Community Shelter",
        category = "Shelter",
        description = "Emergency shelter and supportive services for individuals and families",
        location = "Sample location"
    )
    val showFood = matchesResource(
        name = "Riverview Food Bank",
        category = "Food",
        description = "Provides free groceries, meals, and essential items to community members",
        location = "Sample location"
    )
    val showHousing = matchesResource(
        name = "Bright Futures Housing",
        category = "Housing",
        description = "Assistance with rent, subsidized housing programs, and housing applications",
        location = "Sample location"
    )
    //Main layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Recommended Resources",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        //Search field
        OutlinedTextField(
            value = searchText,
            //Updates searchText live whenever user types
            onValueChange = {
                searchText = it
            },
            placeholder = {
                Text("Search resources, city, or keyword...")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            //Creates a button for each category
            categories.forEach { category ->
                val isSelected = selectedFilter == category
                Button(
                    onClick = {
                        selectedFilter = category
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) {
                            Color.DarkGray
                        } else {
                            Color.White
                        },
                        contentColor = if (isSelected) {
                            Color.White
                        } else {
                            Color.Black
                        }
                    ),
                    border = BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Text(text = category)
                }
            }
        }
        //Temporary resource cards, these are used just to test the UI
        //functionality and are not real data, real data will be connected
        //in the future version of the application
        if (showShelter) {
            ResourceCard(
                name = "Hope Community Shelter",
                category = "Shelter",
                description = "Emergency shelter and supportive services for individuals and families",
                location = "Sample location",
                symbol = "S",
                onClick = {
                    onViewDetailsClick("Hope Community Shelter")
                }
            )
        }
        if (showFood) {
            ResourceCard(
                name = "Riverview Food Bank",
                category = "Food",
                description = "Provides free groceries, meals, and essential items to community members.",
                location = "Sample location",
                symbol = "F",
                onClick = {
                    onViewDetailsClick("Riverview Food Bank")
                }
            )
        }
        if (showHousing) {
            ResourceCard(
                name = "Bright Futures Housing",
                category = "Housing",
                description = "Assistance with rent, subsidized housing programs, and housing applications.",
                location = "Sample location",
                symbol = "H",
                onClick = {
                    onViewDetailsClick("Bright Futures Housing")
                }
            )
        }
        //If none of the categories match or if the search doesn't match this will be shown
        if (!showShelter && !showFood && !showHousing) {

            Text(
                text = "No resources found",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        }
    }
}

//Reusable function for resource cards
@Composable
fun ResourceCard(
    name: String,
    category: String,
    description: String,
    location: String,
    symbol: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        //Card information
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Icon placeholder (Since this is mockup data I saw no reason to try to get actual icons for this demo)
                Box(
                    modifier = Modifier
                        .size(85.dp)
                        .background(
                            Color(0xFFEAEAEA),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                //Organization info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    //Organization name
                    Text(
                        text = name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    //Category
                    Text(
                        text = category,
                        fontSize = 15.sp,
                        color = Color.DarkGray
                    )
                    //Description
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    //Location
                    Text(
                        text = location,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            //Should open details once implemented, right now just gives a pop up message
            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color.DarkGray),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "View Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}