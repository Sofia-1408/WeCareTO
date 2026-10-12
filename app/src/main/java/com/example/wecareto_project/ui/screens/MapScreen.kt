package com.example.wecareto_project.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MapScreen(
    resourceName: String,
    onBackClick: () -> Unit,
    onGetDirectionsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Screen title
        Text(
            text = "Resource Location",
            style = MaterialTheme.typography.headlineMedium
        )

        // Selected resource
        Text(
            text = "Selected resource: $resourceName",
            style = MaterialTheme.typography.titleMedium
        )

        // Map placeholder
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Color(0xFFE6EEF2)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Toronto Map",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Map preview coming soon"
            )
        }

        // Location information
        Text(
            text = "Location and directions will be available once the resource address is verified."
        )

        // Get Directions button
        Button(
            onClick = onGetDirectionsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Directions")
        }

        // Back button
        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
