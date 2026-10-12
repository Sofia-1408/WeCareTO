package com.example.wecareto_project.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun IntakeScreen(
    selectedCategory: String,
    onFindResourcesClick: () -> Unit,
    onBackClick: () -> Unit
) {
    // User can request assistance for themselves or someone else
    var helpingSomeoneElse by rememberSaveable {
        mutableStateOf(false)
    }

    // All personal information is optional
    var name by rememberSaveable {
        mutableStateOf("")
    }

    var phone by rememberSaveable {
        mutableStateOf("")
    }

    var location by rememberSaveable {
        mutableStateOf("")
    }

    var details by rememberSaveable {
        mutableStateOf("")
    }

    // Phone number must contain 10 digits if provided
    val phoneValid = phone.isBlank() ||
            phone.count { it.isDigit() } == 10

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Back button
        TextButton(onClick = onBackClick) {
            Text("← Back")
        }

        // Screen title
        Text(
            text = "Request Assistance",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Find community resources without creating " +
                    "an account or sharing your identity."
        )

        // Category selected on the previous screen
        Text(
            text = "Selected assistance: $selectedCategory",
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Assistance recipient
        Text(
            text = "Who needs assistance?",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = !helpingSomeoneElse,
                onClick = {
                    helpingSomeoneElse = false
                }
            )

            Text("Myself")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = helpingSomeoneElse,
                onClick = {
                    helpingSomeoneElse = true
                }
            )

            Text("Someone Else")
        }

        // Optional information section
        Text(
            text = "Optional Information",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "You can leave these fields blank " +
                    "and continue anonymously."
        )

        // Name
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = {
                Text("Name (Optional)")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Phone number
        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
            },
            label = {
                Text("Phone Number (Optional)")
            },
            isError = !phoneValid,
            supportingText = {
                if (!phoneValid) {
                    Text(
                        "Enter a 10-digit phone number " +
                                "or leave this field blank."
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Location
        OutlinedTextField(
            value = location,
            onValueChange = {
                location = it
            },
            label = {
                Text("Toronto Neighbourhood or Postal Code")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Additional information
        OutlinedTextField(
            value = details,
            onValueChange = {
                details = it
            },
            label = {
                Text("Additional Details (Optional)")
            },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        // Safety information
        Text(
            text = "WeCareTO helps you discover community resources. " +
                    "This prototype does not submit requests or " +
                    "dispatch emergency responders. " +
                    "If someone is in immediate danger, call 911.",
            style = MaterialTheme.typography.bodySmall
        )

        // Continue to recommended resources
        Button(
            onClick = onFindResourcesClick,
            enabled = phoneValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Find Resources")
        }
    }
}