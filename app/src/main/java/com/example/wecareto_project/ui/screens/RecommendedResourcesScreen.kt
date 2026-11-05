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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wecareto_project.viewmodel.ResourceUiState

@Composable
fun RecommendedResourcesScreen(
    initialCategory: String,
    state: ResourceUiState,
    onRetry: () -> Unit,
    onViewDetailsClick: (String) -> Unit,
    preferredLocation: String = "",
    recommendationNote: String = ""
) {
    var searchText by rememberSaveable {
        mutableStateOf("")
    }
    //Automatically select the initial category
    var selectedFilter by rememberSaveable(initialCategory) {
        mutableStateOf(
            when (initialCategory.trim().lowercase()) {
                "housing / shelter" -> "Housing / Shelter"
                "food / essentials" -> "Food / Essentials"
                "transportation" -> "Transportation"
                "other support" -> "Other Support"
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
        "Housing / Shelter",
        "Food / Essentials",
        "Transportation",
        "Other Support",
        "Shelter",
        "Food",
        "Housing",
        "Essentials"
    )
    //Filter repository resources by category and search text
    val visibleResources = state.resources.filter { resource ->

        val matchesCategory = when (selectedFilter) {

            "All" -> true
            "Housing / Shelter" ->
                resource.category in listOf(
                    "Housing",
                    "Shelter",
                    "Housing / Shelter"
                )
            "Food / Essentials" ->
                resource.category in listOf(
                    "Food",
                    "Essentials",
                    "Food / Essentials"
                )
            "Transportation" ->
                resource.category == "Transportation"
            "Other Support" ->
                resource.category == "Other Support"
            else ->
                selectedFilter == resource.category
        }
        val query = searchText.trim()
        val matchesSearch =
            query.isEmpty() ||
                    resource.name.contains(
                        query,
                        ignoreCase = true
                    ) ||
                    resource.category.contains(
                        query,
                        ignoreCase = true
                    ) ||
                    resource.description.contains(
                        query,
                        ignoreCase = true
                    ) ||
                    resource.address.contains(
                        query,
                        ignoreCase = true
                    )
        matchesCategory && matchesSearch
    }.let { matches ->
        //Prioritize matching address text.
        val area = preferredLocation.trim()
        if (area.isEmpty()) {
            matches
        } else {
            matches.sortedByDescending {
                it.address.contains(
                    area,
                    ignoreCase = true
                )
            }
        }
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
        //Recommendation explanation
        if (recommendationNote.isNotBlank()) {
            Text(
                text = recommendationNote,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (preferredLocation.isNotBlank()) {
            Text(
                "Entered location: $preferredLocation " +
                        "(address-text preference only)"
            )
        }
        //Loading state
        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
            Text("Updating Toronto resources…")
        }
        //Repository error
        state.error?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }
        OutlinedButton(
            onClick = onRetry,
            enabled = !state.isLoading
        ) {
            Text("Refresh resources")
        }
        Text(
            "Shelter entries come from Toronto Open Data. " +
                    "Other categories include 211 referrals. " +
                    "Listings do not confirm availability."
        )
        //Search bar
        OutlinedTextField(
            value = searchText,
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
        //Category filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                    border = BorderStroke(
                        1.dp,
                        Color.DarkGray
                    )
                ) {
                    Text(category)
                }
            }
        }
        //Display matching resources
        visibleResources.forEach { resource ->

            ResourceCard(
                name = resource.name,
                category = resource.category,
                description = resource.description,
                location = resource.address,
                symbol = resource.category
                    .take(1)
                    .uppercase(),
                onClick = {
                    onViewDetailsClick(resource.id)
                }
            )
        }
        //Empty results
        if (
            visibleResources.isEmpty() &&
            !state.isLoading
        ) {
            Text(
                text = "No resources match the current filters. " +
                        "Try All or clear the search.",
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
        border = BorderStroke(
            1.dp,
            Color.LightGray
        )
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
                Spacer(
                    modifier = Modifier.width(14.dp)
                )
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
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),

                border = BorderStroke(
                    1.dp,
                    Color.DarkGray
                ),
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