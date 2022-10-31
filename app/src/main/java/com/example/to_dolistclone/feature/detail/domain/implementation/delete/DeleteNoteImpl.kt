package com.example.to_dolistclone.feature.detail.domain.implementation.delete

import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.delete.DeleteNote
import javax.inject.Inject

class DeleteNoteImpl @Inject constructor(private val todoRepository: TodoRepository) : DeleteNote {

    override suspend operator fun invoke(noteId: String): Int = todoRepository.deleteNote(noteId)

}