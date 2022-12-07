package com.example.to_dolistclone.core.data.remote

sealed class ApiResult<out T> {

    data class Success<out T>(val value: T) : ApiResult<T>()
    data class Error(val errorCode: String? = null, val errorMsg: String? = null): ApiResult<Nothing>()
    object NetworkError: ApiResult<Nothing>()
//    object Blank: ApiResult<Nothing>()

}