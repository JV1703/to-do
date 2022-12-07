package com.example.to_dolistclone.utils

import android.content.Context
import android.content.res.AssetManager
import com.example.to_dolistclone.core.data.remote.model.TodoNetwork
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class TestDataGenerator @Inject constructor(@ApplicationContext private val context: Context) {

    fun produceListOfTodoNetwork(): List<TodoNetwork> {
        val todos: List<TodoNetwork> = Gson().fromJson(
            getFromFile("todo_list.json"),
            object : TypeToken<List<TodoNetwork>>() {}.type
        )
        return todos
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