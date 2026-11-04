package com.example.wecareto_project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wecareto_project.di.AppContainer

class BackendViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            ResourceViewModel::class.java -> ResourceViewModel(container.resourceRepository)
            IntakeSubmissionViewModel::class.java -> IntakeSubmissionViewModel(container.intakeRepository)
            else -> error("Unknown ViewModel: ${modelClass.name}")
        }
        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}
