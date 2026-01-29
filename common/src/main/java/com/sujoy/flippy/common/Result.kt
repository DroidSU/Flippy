package com.sujoy.flippy.common

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val message: String) : Result<Nothing>()
}
