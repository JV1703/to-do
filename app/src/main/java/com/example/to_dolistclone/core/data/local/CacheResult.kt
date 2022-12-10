package com.example.to_dolistclone.core.data.local

import android.util.Log
import com.example.to_dolistclone.core.domain.Async
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

sealed class CacheResult<out T> {

    data class Success<out T>(val value: T) : CacheResult<T>()
    data class Error(val errorMessage: String? = null) : CacheResult<Nothing>()

}

val CACHE_TIMEOUT = 2000L
suspend fun <T> safeCacheCall(
    dispatcher: CoroutineDispatcher, cacheCall: suspend () -> T?
): CacheResult<T?> {
    return withContext(dispatcher) {
        try {
            // throws TimeoutCancellationException
            withTimeout(CACHE_TIMEOUT) {
                CacheResult.Success(cacheCall.invoke())
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            Log.e("safeCacheCall", "${throwable.message}")
            when (throwable) {
                is TimeoutCancellationException -> {
                    CacheResult.Error("Cache timeout - ${throwable.message}")
                }
                else -> {
                    CacheResult.Error("Unknown cache error - ${throwable.message}")
                }
            }
        }
    }
}

abstract class CacheResponseHandler<Data>(private val cacheResult: CacheResult<Data?>) {
    suspend fun getResult(): Async<Data?> {
        return when (cacheResult) {
            is CacheResult.Error -> {
                Async.Error(code = null, errorMsg = cacheResult.errorMessage)
            }
            is CacheResult.Success -> {
                if (cacheResult.value == null) {
                    Async.Empty
                } else {
                    handleSuccess(resultObj = cacheResult.value)
                }
            }
        }
    }

    abstract suspend fun handleSuccess(resultObj: Data): Async<Data>
}

suspend fun <T> handleCacheResponse(
    cacheResult: CacheResult<T?>, handleSuccess: suspend (resultObj: T) -> Async<T>
): Async<T>{
    return when (cacheResult) {
        is CacheResult.Success -> {
            if (cacheResult.value == null) {
                Async.Error(code = null, errorMsg = "Cache data is null")
            } else {
                handleSuccess(cacheResult.value)
            }
        }
        is CacheResult.Error -> {
            Async.Error(code = null, errorMsg = cacheResult.errorMessage)
        }
    }
}