package com.example.wecareto_project.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.wecareto_project.data.ResourceRepository

@Composable
fun ResourceDetailsScreen(
    resourceName: String,
    onBackClick: () -> Unit,
    onGetDirectionsClick: () -> Unit
) {
    val resource = ResourceRepository.getResourceByName(resourceName)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = resourceName,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Resource Information",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = resource?.description
                ?: "Resource details are not available yet."
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Category: ${resource?.category ?: "Not available"}"
        )

        Text(
            text = "Address: ${resource?.address ?: "Not available"}"
        )

        Text(
            text = "Phone: ${resource?.phone ?: "Not available"}"
        )

        Text(
            text = "Hours: ${resource?.operatingHours ?: "Not available"}"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onGetDirectionsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Directions")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}