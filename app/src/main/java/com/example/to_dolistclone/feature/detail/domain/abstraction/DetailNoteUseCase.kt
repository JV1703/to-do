package com.example.to_dolistclone.feature.detail.domain.abstraction

import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.Note

interface DetailNoteUseCase {

    suspend fun insertNote(userId: String, note: Note, todoUpdatedOn: Long): Async<Long>
    suspend fun deleteNote(userId: String, noteId: String, todoUpdatedOn: Long): Async<Int>

}