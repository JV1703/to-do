package com.example.to_dolistclone.feature.detail.domain.abstraction

import com.example.to_dolistclone.core.domain.model.Note

interface DetailNoteUseCase {

    suspend fun insertNote(note: Note): Long
    suspend fun deleteNote(noteId: String): Int

}