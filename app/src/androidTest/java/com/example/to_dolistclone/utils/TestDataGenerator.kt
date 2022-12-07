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
        val todos: List<TodoNetwork> = Gson().fromJson(
            getFromFile("todo_list.json"), object : TypeToken<List<TodoNetwork>>() {}.type
        )
        return todos
    }

    fun produceListOfTaskNetwork(): List<TaskNetwork> {
        val tasks: List<TaskNetwork> = Gson().fromJson(
            getFromFile("task_list.json"), object : TypeToken<List<TaskNetwork>>() {}.type
        )
        return tasks
    }

    fun produceListOfNoteNetwork(): List<NoteNetwork> {
        val notes: List<NoteNetwork> = Gson().fromJson(
            getFromFile("note_list.json"), object : TypeToken<List<NoteNetwork>>() {}.type
        )
        return notes
    }

    fun produceListOfAttachmentNetwork(): List<AttachmentNetwork> {
        val attachments: List<AttachmentNetwork> = Gson().fromJson(
            getFromFile("attachment_list.json"),
            object : TypeToken<List<AttachmentNetwork>>() {}.type
        )
        return attachments
    }

    fun produceListOfTodoCategoryNetwork(): List<TodoCategoryNetwork> {
        val todoCategoryNetwork: List<TodoCategoryNetwork> = Gson().fromJson(
            getFromFile("todo_category_list.json"),
            object : TypeToken<List<TodoCategoryNetwork>>() {}.type
        )
        return todoCategoryNetwork
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