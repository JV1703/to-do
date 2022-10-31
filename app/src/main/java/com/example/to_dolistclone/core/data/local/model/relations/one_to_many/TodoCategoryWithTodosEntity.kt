package com.example.to_dolistclone.core.data.local.model.relations.one_to_many

import androidx.room.Embedded
import androidx.room.Relation
import com.example.to_dolistclone.core.data.local.model.TodoCategoryEntity
import com.example.to_dolistclone.core.data.local.model.TodoEntity
import com.example.to_dolistclone.core.data.local.model.toTodo
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoCategoryWithTodos

data class TodoCategoryWithTodosEntity(
    @Embedded val todoCategory: TodoCategoryEntity,
    @Relation(
        parentColumn = "todoCategoryId",
        entityColumn = "todoCategoryRefId"
    )
    val todos: List<TodoEntity>
)

fun TodoCategoryWithTodosEntity.toTodo() = this.todos.map {
    it.toTodo()
}

fun TodoCategoryWithTodosEntity.toTodoCategoryWithTodos() = TodoCategoryWithTodos(
    todoCategory = this.todoCategory,
    todos = this.todos
)