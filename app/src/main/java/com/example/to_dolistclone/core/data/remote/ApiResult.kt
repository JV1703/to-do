package com.example.to_dolistclone.core.data.remote

import android.util.Log
import com.example.to_dolistclone.core.domain.Async
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException

sealed class ApiResult<out T> {

    data class Success<out T>(val value: T) : ApiResult<T>()
    data class Error(val errorCode: Int? = null, val errorMsg: String? = null) :
        ApiResult<Nothing>()

//    object NetworkError : ApiResult<Nothing>()
//    object Blank: ApiResult<Nothing>()

}

val NETWORK_TIMEOUT = 6000L
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher, apiCall: suspend () -> T?
): ApiResult<T?> {
    return withContext(dispatcher) {
        try {
            // throws TimeoutCancellationException
            withTimeout(NETWORK_TIMEOUT) {
                ApiResult.Success(apiCall.invoke())
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            Log.e("safeApiCall", "${throwable.message}")
            when (throwable) {
                is TimeoutCancellationException -> {
                    val code = 408 // timeout error code
                    ApiResult.Error(code, "Network timeout - ${throwable.message}")
                }
                is IOException -> {
                    ApiResult.Error(errorCode = null, errorMsg = "IOException - network error - ${throwable.message}")
                }
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    ApiResult.Error(
                        code, errorResponse
                    )
                }
                else -> {
                    ApiResult.Error(
                        null, throwable.message ?: "Unknown network error"
                    )
                }
            }
        }
    }
}

fun <T> handleApiResponse(apiResult: ApiResult<T?>): Async<T> {
    return when (apiResult) {
        is ApiResult.Success -> {
            if (apiResult.value == null) {
                Async.Empty
            } else {
                Async.Success(apiResult.value)
            }
        }
        is ApiResult.Error -> {
            Async.Error(code = apiResult.errorCode, errorMsg = apiResult.errorMsg)
        }
    }
}

private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        "Unknown error"
    }
}