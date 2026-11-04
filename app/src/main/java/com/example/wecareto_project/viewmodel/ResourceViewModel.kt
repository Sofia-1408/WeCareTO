package com.example.wecareto_project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wecareto_project.data.DataResult
import com.example.wecareto_project.data.Resource
import com.example.wecareto_project.data.ResourceRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResourceUiState(
    val resources: List<Resource> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class ResourceViewModel(private val repository: ResourceRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(ResourceUiState())
    val state = mutableState.asStateFlow()

    private var refreshing = false

    init {
        viewModelScope.launch {
            try {
                repository.resources.collect { resources -> mutableState.update { it.copy(resources = resources) } }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { mutableState.update { it.copy(isLoading = false, error = "Couldn’t read saved resources. Please restart the app.") } }
        }
        refresh()
    }

    fun refresh() {
        if (refreshing) return
        refreshing = true
        viewModelScope.launch {
            mutableState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.seedReferrals()
                val result = repository.refresh()
                mutableState.update { it.copy(error = (result as? DataResult.Failure)?.message) }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { mutableState.update { it.copy(error = "Couldn’t access the resource database. Please try again.") } }
            finally {
                refreshing = false
                mutableState.update { it.copy(isLoading = false) }
            }
        }
    }
}
