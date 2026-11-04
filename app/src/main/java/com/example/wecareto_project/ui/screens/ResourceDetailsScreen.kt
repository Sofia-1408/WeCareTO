package com.example.wecareto_project.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.wecareto_project.data.Resource
import java.text.DateFormat
import java.util.Date

@Composable
fun ResourceDetailsScreen(
    resource: Resource?,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onViewMapClick: () -> Unit
) {
    val context = LocalContext.current
    fun open(intent: Intent) {
        try { context.startActivity(intent) }
        catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "No app is available to open this action.", Toast.LENGTH_SHORT).show()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(onClick = onBackClick) { Text("← Back") }
        if (resource == null) {
            if (isLoading) CircularProgressIndicator()
            else Text("This resource is no longer in the saved list. Go back and refresh resources.")
        } else {
            Text(resource.name, style = MaterialTheme.typography.headlineMedium)
            Text(resource.description)
            Text("Category: ${resource.category}")
            Text("Address: ${resource.address}")
            Text("Phone: ${resource.phone.ifBlank { "Not supplied by this source" }}")
            Text("Hours: ${resource.operatingHours}")
            Text("Source: ${resource.source}")
            if (resource.reportedDate.isNotBlank()) Text("Reported: ${resource.reportedDate}")
            if (resource.updatedAtMillis > 0) {
                Text("Downloaded: ${DateFormat.getDateTimeInstance().format(Date(resource.updatedAtMillis))}")
            }
            if (resource.phone.isNotBlank()) {
                Button(
                    onClick = { open(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", resource.phone, null))) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Call ${resource.phone}") }
            }
            OutlinedButton(
                onClick = { open(Intent(Intent.ACTION_VIEW, Uri.parse(resource.sourceUrl))) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("View source") }
            OutlinedButton(
                onClick = onViewMapClick,
                modifier = Modifier.fillMaxWidth()
            ) { Text("View Map") }
            Text("Map preview only. GPS and directions are planned for a later version.", style = MaterialTheme.typography.bodySmall)
        }
    }
}
