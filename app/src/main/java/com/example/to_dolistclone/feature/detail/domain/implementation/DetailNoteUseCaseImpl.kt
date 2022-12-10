package com.example.to_dolistclone.feature.detail.domain.implementation

import com.example.to_dolistclone.core.common.worker.WorkerManager
import com.example.to_dolistclone.core.data.local.GenericCacheError.GENERIC_CACHE_ERROR
import com.example.to_dolistclone.core.data.local.handleCacheResponse
import com.example.to_dolistclone.core.domain.Async
import com.example.to_dolistclone.core.domain.model.Note
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.domain.abstraction.DetailNoteUseCase
import com.example.to_dolistclone.feature.detail.domain.abstraction.DetailTodoUseCase
import javax.inject.Inject

class DetailNoteUseCaseImpl @Inject constructor(private val todoRepository: TodoRepository, private val detailTodoUseCase: DetailTodoUseCase, private val workerManager: WorkerManager) :
    DetailNoteUseCase {

    override suspend fun insertNote(userId: String, note: Note, todoUpdatedOn: Long): Async<Long> {
        val cacheResult = todoRepository.insertNote(note)
        return handleCacheResponse(cacheResult){resultObj ->
            detailTodoUseCase.updateTodoUpdatedOn(
                userId = userId,
                todoId = note.noteId,
                updatedOn = todoUpdatedOn
            )
            if(resultObj > 0){
                workerManager.upsertNote(userId, note.noteId)
                Async.Success(resultObj)
            }else{
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

    override suspend fun deleteNote(
        userId: String,
        noteId: String,
        todoUpdatedOn: Long
    ): Async<Int> {
        val cacheResult = todoRepository.deleteNote(noteId)
        return handleCacheResponse(cacheResult) { resultObj ->
            if(resultObj > 0){
                detailTodoUseCase.updateTodoUpdatedOn(
                    userId = userId,
                    todoId = noteId,
                    updatedOn = todoUpdatedOn
                )
                workerManager.deleteNote(userId, noteId)
                Async.Success(resultObj)
            }else{
                Async.Error(errorMsg = GENERIC_CACHE_ERROR)
            }
        }
    }

}