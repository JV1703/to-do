package com.example.to_dolistclone.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.to_dolistclone.core.data.local.dao.TodoDao
import com.example.to_dolistclone.core.data.local.model.AttachmentEntity
import com.example.to_dolistclone.core.data.local.model.NoteEntity
import com.example.to_dolistclone.core.data.local.model.TaskEntity
import com.example.to_dolistclone.core.data.local.model.TodoEntity

@Database(
    entities = [TodoEntity::class, TaskEntity::class, NoteEntity::class, AttachmentEntity::class],
    version = 1
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}