package com.example.to_dolistclone.feature.detail.domain.implementation

import com.example.to_dolistclone.core.domain.model.Note
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.DetailNoteUseCase
import javax.inject.Inject

class DetailNoteUseCaseImpl @Inject constructor(private val todoRepository: TodoRepository) :
    DetailNoteUseCase {

    override suspend fun insertNote(note: Note): Long = todoRepository.insertNote(note)

    override suspend fun deleteNote(noteId: String): Int = todoRepository.deleteNote(noteId)

}