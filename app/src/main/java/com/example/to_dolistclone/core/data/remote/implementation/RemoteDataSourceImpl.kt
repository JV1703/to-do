package com.example.to_dolistclone.core.data.remote.implementation

import com.example.to_dolistclone.core.data.remote.firebase.*
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val todo: TodoFirestoreImpl,
    private val task: TaskFirestoreImpl,
    private val note: NoteFirestoreImpl,
    private val attachment: AttachmentFirestoreImpl,
    private val todoCategory: TodoCategoryFirestoreImpl
) {


}