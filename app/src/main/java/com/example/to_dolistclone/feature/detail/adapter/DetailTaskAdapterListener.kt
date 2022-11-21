package com.example.to_dolistclone.feature.detail.adapter

interface DetailTaskAdapterListener {

    fun updateTaskTitle(taskId: String?, title: String)
    fun updateTaskCompletion(taskId: String?, isComplete: Boolean)
    fun deleteTask(taskId: String)

}