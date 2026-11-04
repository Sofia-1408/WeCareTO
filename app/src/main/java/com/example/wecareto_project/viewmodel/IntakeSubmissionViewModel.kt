package com.example.wecareto_project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wecareto_project.data.DataResult
import com.example.wecareto_project.data.IntakeRepository
import com.example.wecareto_project.data.IntakeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IntakeSubmissionState(
    val saving: Boolean = false,
    val savedId: String? = null,
    val error: String? = null
)

class IntakeSubmissionViewModel(private val repository: IntakeRepository) : ViewModel() {
    val isConfigured get() = repository.isConfigured
    private val mutableState = MutableStateFlow(IntakeSubmissionState())
    val state = mutableState.asStateFlow()
    private var pending: IntakeResponse? = null

    fun submit(response: IntakeResponse) {
        if (mutableState.value.saving || mutableState.value.savedId != null) return
        val previous = pending
        val draft = if (previous != null && previous.copy(id = response.id,
                createdAtMillis = response.createdAtMillis) == response) previous else response
        pending = draft
        mutableState.value = IntakeSubmissionState(saving = true)
        viewModelScope.launch {
            mutableState.value = when (val result = repository.submit(draft)) {
                is DataResult.Success -> IntakeSubmissionState(savedId = result.value)
                is DataResult.Failure -> IntakeSubmissionState(error = result.message)
            }
        }
    }
}
