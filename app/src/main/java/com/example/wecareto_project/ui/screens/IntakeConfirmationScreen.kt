package com.example.wecareto_project.ui.screens
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wecareto_project.data.SafetyAnswer
import com.example.wecareto_project.ui.viewmodel.IntakeFormViewModel
import com.example.wecareto_project.viewmodel.IntakeSubmissionState

@Composable
fun IntakeConfirmationScreen(
    form: IntakeFormViewModel,
    submission: IntakeSubmissionState,
    savingAvailable: Boolean,
    onSaveIntake: () -> Unit,
    onViewResourcesClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val saved = submission.savedId != null //switched from save intake to view resources
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!saved) {
            OutlinedButton(
                onClick = onEditClick,
                enabled = !submission.saving
            ) {
                Text("← Edit Information")
            }
        }
        Text(
            text = "Review Your Information",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Review your answers below. You can still browse " +
                    "resources without saving your information."
        )
        //Display current form answers before optional submission
        SummaryItem("Assistance type", form.category)
        SummaryItem(
            "Assistance for",
            if (form.helpingSomeoneElse) "Someone else" else "Myself"
        )
        SummaryItem(
            "Name",
            form.name.trim().ifEmpty { "Not provided" }
        )
        SummaryItem(
            "Phone",
            form.phone.trim().ifEmpty { "Not provided" }
        )
        SummaryItem(
            "Toronto location",
            form.location.trim().ifEmpty { "Not provided" }
        )
        SummaryItem(
            "Additional details",
            form.details.trim().ifEmpty { "Not provided" }
        )
        SummaryItem(
            "Immediate danger",
            form.immediateDanger.toReadableAnswer()
        )
        SummaryItem(
            "Suicide-related concern",
            form.suicideConcern.toReadableAnswer()
        )
        SummaryItem(
            "Online saving",
            if (form.saveOnline) {
                "Requested (excluding safety question answers)"
            } else {
                "Not requested"
            }
        )
        SafetySupport(
            immediateDanger = form.immediateDanger,
            suicideConcern = form.suicideConcern
        )
        Text(
            text = "This app provides resource information only. " +
                    "A saved intake is not a referral, notification " +
                    "to a provider, emergency call, or dispatch request.",
            style = MaterialTheme.typography.bodySmall
        )
        if (submission.saving) {
            CircularProgressIndicator()
            Text("Saving your intake...")
        }
        submission.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }
        submission.savedId?.let { id ->
            Text(
                text = "Intake saved to Firebase",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Reference: $id")
            Text(
                "No service provider or emergency responder " +
                        "has been contacted."
            )
        }
        if (form.saveOnline && !saved) { //save only if opted in for it
            if (!savingAvailable) {
                Text(
                    "Online saving is unavailable. Go back, " +
                            "uncheck online saving, and continue without saving."
                )
            }
            Button(
                onClick = onSaveIntake,
                enabled = savingAvailable &&
                        !submission.saving &&
                        form.validationError == null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (submission.saving) {
                        "Saving..."
                    } else {
                        "Save Intake to Firebase"
                    }
                )
            }
        } else {
            Button(
                onClick = onViewResourcesClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (saved) {
                        "View Recommended Resources"
                    } else {
                        "View Resources Without Saving"
                    }
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge
    )
    Text(value)
}

private fun SafetyAnswer?.toReadableAnswer(): String {
    return when (this) {
        SafetyAnswer.YES -> "Yes"
        SafetyAnswer.NO -> "No"
        SafetyAnswer.NOT_SURE -> "Not sure / Prefer not to say"
        null -> "Not answered"
    }
}