package com.example.to_dolistclone

import com.example.to_dolistclone.core.data.local.model.TodoEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataFactory(
    private val testClassLoader: ClassLoader
) {

    fun produceListOfTodos(): List<TodoEntity> {
        val todos: List<TodoEntity> = Gson().fromJson(
                getDataFromFile("todo_list.json"), object : TypeToken<List<TodoEntity>>() {}.type
            )

        return todos
    }

    fun getDataFromFile(fileName: String): String {
        return testClassLoader.getResource(fileName).readText()
    }

}