package com.example.wecareto_project.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.wecareto_project.data.ResourceRepository

@Composable
fun RecommendedResourcesScreen(
    initialCategory: String,
    onViewDetailsClick: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    var selectedFilter by remember(initialCategory) {
        mutableStateOf(
            when (initialCategory.trim().lowercase()) {
                "shelter", "shelters", "emergency shelter" -> "Shelter"
                "food", "food assistance", "food support" -> "Food"
                "housing", "housing assistance", "housing support" -> "Housing"
                "essentials", "essential supplies" -> "Essentials"
                else -> "All"
            }
        )
    }
    val categories = listOf(
        "All",
        "Shelter",
        "Food",
        "Housing",
        "Essentials"
    )

    // Load resources from our shared repository
    val visibleResources = ResourceRepository.resources.filter { resource ->

        val matchesCategory =
            selectedFilter == "All" ||
                    selectedFilter == resource.category

        val query = searchText.trim()

        val matchesSearch =
            query.isEmpty() ||
                    resource.name.contains(query, ignoreCase = true) ||
                    resource.category.contains(query, ignoreCase = true) ||
                    resource.description.contains(query, ignoreCase = true) ||
                    resource.address.contains(query, ignoreCase = true)

        matchesCategory && matchesSearch
    }

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

        // Search bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = {
                Text("Search resources, city, or keyword...")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        // Category filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->

                val isSelected = selectedFilter == category

                Button(
                    onClick = { selectedFilter = category },
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

        // Display resources from repository
        visibleResources.forEach { resource ->

            ResourceCard(
                name = resource.name,
                category = resource.category,
                description = resource.description,
                location = resource.address,
                symbol = resource.category.take(1).uppercase(),
                onClick = {
                    onViewDetailsClick(resource.name)
                }
            )
        }

        // Empty results message
        if (visibleResources.isEmpty()) {
            Text(
                text = "No resources found",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        }
    }
}

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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = category,
                        fontSize = 15.sp,
                        color = Color.DarkGray
                    )

                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )

                    Text(
                        text = location,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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