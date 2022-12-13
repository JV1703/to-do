package com.example.to_dolistclone.core.common.worker

import com.example.to_dolistclone.core.domain.model.Attachment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JsonConverter {

    private val gson = Gson()

    fun toJson(data: WorkerHelper): String = gson.toJson(data)
    fun fromJson(data: String): WorkerHelper =
        gson.fromJson(data, object : TypeToken<WorkerHelper>() {}.type)

    fun Attachment.toJson(): String = gson.toJson(this)
    fun Attachment.fromJson(data: String): Attachment = gson.fromJson(data, object : TypeToken<Attachment>() {}.type)

}

data class WorkerHelper(
    val key: String, val value: Any?
)