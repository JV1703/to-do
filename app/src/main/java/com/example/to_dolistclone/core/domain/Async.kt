package com.example.to_dolistclone.core.domain

sealed class Async<out T> {

    object Loading : Async<Nothing>()
    data class Success<out T>(val data: T) : Async<T>()
    data class Error(val code: Int? = null, val errorMsg: String? = null) : Async<Nothing>()

}