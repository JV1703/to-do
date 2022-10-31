package com.example.to_dolistclone.feature.detail.domain.implementation.create_update

import com.example.to_dolistclone.core.domain.model.Note
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.create_update.InsertNote
import javax.inject.Inject

class InsertNoteImpl @Inject constructor(private val todoRepository: TodoRepository) :
    InsertNote {

    override suspend operator fun invoke(note: Note): Long =
        todoRepository.insertNote(note)

}