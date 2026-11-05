package com.example.wecareto_project.ui.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.wecareto_project.data.SafetyAnswer
import com.example.wecareto_project.ui.viewmodel.IntakeFormViewModel

@Composable
fun IntakeScreen(
    form: IntakeFormViewModel,
    savingAvailable: Boolean,
    onReviewClick: () -> Unit,
    onBackClick: () -> Unit
) {
    //this screen reads and updates through IntakeFormViewModel
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onBackClick) {
            Text("← Back")
        }
        Text(
            text = "Request Assistance",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Find community resources without an account " +
                    "or sharing your identity."
        )
        Text(
            text = "Selected assistance: ${form.category}",
            fontWeight = FontWeight.SemiBold
        )
        //Assistance recipient
        Text(
            text = "Who needs assistance?",
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = !form.helpingSomeoneElse,
                onClick = {
                    form.updateHelpingSomeoneElse(false)
                }
            )
            Text("Myself")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = form.helpingSomeoneElse,
                onClick = {
                    form.updateHelpingSomeoneElse(true)
                }
            )
            Text("Someone Else")
        }
        //Optional information
        Text(
            text = "Optional Information",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            "You can leave personal details blank " +
                    "and browse anonymously."
        )
        //Name
        OutlinedTextField(
            value = form.name,
            onValueChange = form::updateName,
            label = {
                Text("Name (Optional)")
            },
            isError = form.nameError != null,
            supportingText = {
                form.nameError?.let {
                    Text(it)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        //Phone
        OutlinedTextField(
            value = form.phone,
            onValueChange = form::updatePhone,
            label = {
                Text("Phone Number (Optional)")
            },
            isError = form.phoneError != null,
            supportingText = {
                form.phoneError?.let {
                    Text(it)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        //Location
        OutlinedTextField(
            value = form.location,
            onValueChange = form::updateLocation,
            label = {
                Text("Toronto Neighbourhood or Postal Code (Optional)")
            },
            isError = form.locationError != null,
            supportingText = {
                form.locationError?.let {
                    Text(it)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        //Additional details
        OutlinedTextField(
            value = form.details,
            onValueChange = form::updateDetails,
            label = {
                Text("Additional Details (Optional)")
            },
            isError = form.detailsError != null,
            supportingText = {
                form.detailsError?.let {
                    Text(it)
                }
            },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        //Safety questions
        Text(
            text = "Safety questions (optional)",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            "These answers help us show urgent contact " +
                    "information. WeCareTO does not perform a " +
                    "clinical assessment or dispatch services."
        )
        SafetyQuestion(
            question = "Is anyone in immediate danger or needing urgent medical help?",
            answer = form.immediateDanger,
            onAnswer = form::updateImmediateDanger,
            unsureLabel = "Not sure"
        )
        SafetyQuestion(
            question = "Is there a concern about suicide or self-harm?",
            answer = form.suicideConcern,
            onAnswer = form::updateSuicideConcern,
            unsureLabel = "Not sure / Prefer not to say"
        )
        SafetySupport(
            form.immediateDanger,
            form.suicideConcern
        )
        Text(
            text = "If someone is in immediate danger, call 911. " +
                    "For suicide-related support in Canada, call or text 988. " +
                    "Saving information here does not contact a service provider.",
            style = MaterialTheme.typography.bodySmall
        )
        //Optional Firebase saving
        if (savingAvailable) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = form.saveOnline,
                    onCheckedChange = form::updateSaveOnline
                )
                Text(
                    "I choose to save my assistance type, recipient " +
                            "and optional contact, location and details " +
                            "to this app's Firebase database. " +
                            "Safety-question answers are not stored. " +
                            "This does not request emergency or service dispatch."
                )
            }
        } else {
            Text(
                "Online saving is not configured. " +
                        "You can still browse resources without saving."
            )
        }
        //Validate using the existing IntakeResponse model
        val error = form.validationError
        if (
            error != null &&
            error !in listOfNotNull(
                form.nameError,
                form.phoneError,
                form.locationError,
                form.detailsError
            )
        ) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }
        //Navigate to confirmation
        Button(
            onClick = onReviewClick,
            enabled = error == null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Review Information")
        }
    }
}

@Composable
private fun SafetyQuestion( //reusable radio button group for each safety question
    question: String,
    answer: SafetyAnswer?,
    onAnswer: (SafetyAnswer) -> Unit,
    unsureLabel: String
) {
    Text(
        text = question,
        fontWeight = FontWeight.SemiBold
    )
    listOf(
        SafetyAnswer.YES to "Yes",
        SafetyAnswer.NO to "No",
        SafetyAnswer.NOT_SURE to unsureLabel
    ).forEach { (value, label) ->
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = answer == value,
                onClick = {
                    onAnswer(value)
                }
            )
            Text(label)
        }
    }
}
