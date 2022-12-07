package com.example.to_dolistclone.core.data.local

sealed class CacheResult<out T> {

    data class Success<out T>(val value: T): CacheResult<T>()
    data class Error(val errorMessage: String? = null): CacheResult<Nothing>()

}