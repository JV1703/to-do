package com.example.to_dolistclone.utils

import android.content.Context
import android.content.res.AssetManager
import com.example.to_dolistclone.core.data.remote.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class TestDataGenerator @Inject constructor(@ApplicationContext private val context: Context) {

    fun produceListOfTodoNetwork(): List<TodoNetwork> {
        return Gson().fromJson(
            getFromFile("todo_list.json"), object : TypeToken<List<TodoNetwork>>() {}.type
        )
    }

    fun produceListOfTaskNetwork(): List<TaskNetwork> {
        return Gson().fromJson(
            getFromFile("task_list.json"), object : TypeToken<List<TaskNetwork>>() {}.type
        )
    }

    fun produceListOfNoteNetwork(): List<NoteNetwork> {
        return Gson().fromJson(
            getFromFile("note_list.json"), object : TypeToken<List<NoteNetwork>>() {}.type
        )
    }

    fun produceListOfAttachmentNetwork(): List<AttachmentNetwork> {
        return Gson().fromJson(
            getFromFile("attachment_list.json"),
            object : TypeToken<List<AttachmentNetwork>>() {}.type
        )
    }

    fun produceListOfTodoCategoryNetwork(): List<TodoCategoryNetwork> {
        return Gson().fromJson(
            getFromFile("todo_category_list.json"),
            object : TypeToken<List<TodoCategoryNetwork>>() {}.type
        )
    }

    private fun getFromFile(fileName: String): String? {
        return readJSONFromAsset(fileName)
    }

    private fun readJSONFromAsset(fileName: String): String? {
        var json: String? = null
        json = try {
            val inputStream: InputStream = (context.assets as AssetManager).open(fileName)
            inputStream.bufferedReader().use { it.readText() }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}