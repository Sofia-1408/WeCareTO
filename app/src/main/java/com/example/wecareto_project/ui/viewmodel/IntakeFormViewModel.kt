package com.example.wecareto_project.ui.viewmodel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.wecareto_project.data.IntakeResponse
import com.example.wecareto_project.data.SafetyAnswer
import java.util.UUID

//The user's answers remain local until they
//explicitly choose to save them to the firebase
class IntakeFormViewModel : ViewModel() {

    var category by mutableStateOf("")
        private set

    var helpingSomeoneElse by mutableStateOf(false)
        private set

    var name by mutableStateOf("")
        private set

    var phone by mutableStateOf("")
        private set

    var location by mutableStateOf("")
        private set

    var details by mutableStateOf("")
        private set

    var immediateDanger by mutableStateOf<SafetyAnswer?>(null)
        private set

    var suicideConcern by mutableStateOf<SafetyAnswer?>(null)
        private set

    var saveOnline by mutableStateOf(false)
        private set

    private var requestId = UUID.randomUUID().toString()
    private var createdAtMillis = System.currentTimeMillis()

    //Clear the previous request
    fun startNewIntake(newCategory: String) {
        category = newCategory
        helpingSomeoneElse = false
        name = ""
        phone = ""
        location = ""
        details = ""
        immediateDanger = null
        suicideConcern = null
        saveOnline = false
        requestId = UUID.randomUUID().toString()
        createdAtMillis = System.currentTimeMillis()
    }

    fun updateHelpingSomeoneElse(value: Boolean) {
        helpingSomeoneElse = value
    }

    fun updateName(value: String) {
        name = value
    }

    fun updatePhone(value: String) {
        phone = value
    }

    fun updateLocation(value: String) {
        location = value
    }

    fun updateDetails(value: String) {
        details = value
    }

    fun updateImmediateDanger(value: SafetyAnswer) {
        immediateDanger = value
    }

    fun updateSuicideConcern(value: SafetyAnswer) {
        suicideConcern = value
    }

    fun updateSaveOnline(value: Boolean) {
        saveOnline = value
    }

    fun toIntakeResponse() = IntakeResponse(
        id = requestId,
        category = category,
        helpingSomeoneElse = helpingSomeoneElse,
        name = name.trim(),
        phone = phone.trim(),
        location = location.trim(),
        details = details.trim(),
        createdAtMillis = createdAtMillis
    )

    //Validation
    val nameError: String?
        get() = if (name.trim().length > 100) {
            "Keep the name under 100 characters."
        } else {
            null
        }

    val phoneError: String?
        get() {
            val value = phone.trim()
            return when {
                value.isBlank() -> null
                !value.matches(Regex("[+0-9() .-]+")) ||
                        value.count(Char::isDigit) != 10 ->
                    "Enter a 10digit phone number or leave it blank."
                value.length > 30 ->
                    "The phone number is too long."
                else -> null
            }
        }

    val locationError: String?
        get() = if (location.trim().length > 200) {
            "Keep the location under 200 characters."
        } else {
            null
        }

    val detailsError: String?
        get() = if (details.trim().length > 2000) {
            "Keep additional details under 2000 characters."
        } else {
            null
        }

    //Check validation before letting user review it
    val validationError: String?
        get() = toIntakeResponse().validationError()
}