package com.example.to_dolistclone.core.common.worker.note

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.to_dolistclone.core.common.worker.JsonConverter
import com.example.to_dolistclone.core.common.worker.WorkTag
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltWorker
class UpsertNoteWorker @AssistedInject constructor(
    @Assisted private val context: Context, @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var jsonConverter: JsonConverter

    override suspend fun doWork(): Result {
        val userId = workerParams.inputData.getString(WorkTag.USER_ID_WORKER_DATA)
        val noteId = workerParams.inputData.getString(WorkTag.NOTE_ID_WORKER_DATA)
        val note = noteId?.let { todoRepository.getNote(it).first() }

        return if (userId == null || note == null) {
            Log.e("Worker", "UpsertTaskWorker - failed - null data passed")
            Result.failure()
        } else if (runAttemptCount > WorkTag.MAX_RETRY_ATTEMPT) {
            Log.i("Worker", "UpsertTaskWorker - failed - exceed retry count")
            Result.failure()
        } else {
            try {
                todoRepository.insertNoteNetwork(userId, note)
                Log.i("Worker", "UpsertTaskWorker - success")
                Result.success()
            } catch (e: Exception) {
                Log.e("Worker", "UpsertTaskWorker - retrying - ${e.message}")
                Result.retry()
            }
        }
    }

}