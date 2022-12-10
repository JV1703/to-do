package com.example.to_dolistclone.core.common.worker

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JsonConverter {

    private val gson = Gson()

    fun toJson(data: WorkerHelper): String = gson.toJson(data)
    fun fromJson(data: String): WorkerHelper = gson.fromJson(data, object : TypeToken<WorkerHelper>() {}.type)

}

data class WorkerHelper(
    val key: String, val value: Any?
)