package com.example.to_dolistclone.core.data.local.model.relations.one_to_many

import androidx.room.Embedded
import androidx.room.Relation
import com.example.to_dolistclone.core.data.local.model.TaskEntity
import com.example.to_dolistclone.core.data.local.model.TodoEntity
import com.example.to_dolistclone.core.data.local.model.toTask
import com.example.to_dolistclone.core.data.local.model.toTodo
import com.example.to_dolistclone.core.domain.model.relation.one_to_many.TodoWithTasks

data class TodoWithTasksEntity(
    @Embedded val todo: TodoEntity,
    @Relation(
        parentColumn = "todoId",
        entityColumn = "todoRefId"
    )
    val tasks: List<TaskEntity>
)

fun TodoWithTasksEntity.toSubTasks() = this.tasks.map {
    it.toTask()
}

fun TodoWithTasksEntity.toTodoWithTasks() = TodoWithTasks(
    todo = todo.toTodo(), tasks = tasks.map { it.toTask() }
)