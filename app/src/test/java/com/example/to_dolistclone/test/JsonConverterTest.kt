package com.example.to_dolistclone.test

import com.example.to_dolistclone.core.common.worker.WorkerHelper
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class JsonConverterTest {

    val gson = Gson()
    fun toJson(data: Map<String, Any>) = gson.toJson(data)
    fun fromJson(data: String) = gson.fromJson(data, WorkerHelper::class.java)


    @Test
    fun toJson(){
        val key = "position"
        val value = 0
        val result = toJson(mapOf(key to value))
        assertEquals("", result)
    }

}