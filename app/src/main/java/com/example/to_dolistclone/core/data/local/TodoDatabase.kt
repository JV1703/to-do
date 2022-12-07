package com.example.to_dolistclone.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.to_dolistclone.core.data.local.dao.TodoDao
import com.example.to_dolistclone.core.data.local.model.*

@Database(
    entities = [TodoEntity::class, TaskEntity::class, NoteEntity::class, AttachmentEntity::class, TodoCategoryEntity::class],
    version = 1
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    companion object{
        val DATABASE_NAME: String = "todo_database"
    }
}