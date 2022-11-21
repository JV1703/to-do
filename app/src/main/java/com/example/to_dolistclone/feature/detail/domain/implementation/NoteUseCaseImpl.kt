package com.example.to_dolistclone.feature.detail.domain.implementation

import com.example.to_dolistclone.core.domain.model.Note
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.NoteUseCase
import javax.inject.Inject

class NoteUseCaseImpl @Inject constructor(private val todoRepository: TodoRepository) :
    NoteUseCase {

    override suspend fun insertNote(note: Note): Long = todoRepository.insertNote(note)

    override suspend fun deleteNote(noteId: String): Int = todoRepository.deleteNote(noteId)

}