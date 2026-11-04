package com.example.wecareto_project.data

sealed interface DataResult<out T> {
    data class Success<T>(val value: T) : DataResult<T>
    data class Failure(val message: String) : DataResult<Nothing>
}
