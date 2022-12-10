//package com.example.to_dolistclone.core.common.worker.todo
//
//import android.content.Context
//import android.util.Log
//import androidx.hilt.work.HiltWorker
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//import com.example.to_dolistclone.core.common.worker.JsonConverter
//import com.example.to_dolistclone.core.common.worker.WorkTag
//import com.example.to_dolistclone.core.common.worker.WorkTag.FIELD_WORKER_DATA
//import com.example.to_dolistclone.core.common.worker.WorkTag.TODO_ID_WORKER_DATA
//import com.example.to_dolistclone.core.common.worker.WorkTag.USER_ID_WORKER_DATA
//import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
//import dagger.assisted.Assisted
//import dagger.assisted.AssistedInject
//import javax.inject.Inject
//
//@HiltWorker
//class UpdateTodoWorker @AssistedInject constructor(
//    @Assisted private val context: Context, @Assisted private val workerParams: WorkerParameters
//) : CoroutineWorker(context, workerParams) {
//
//    @Inject
//    lateinit var todoRepository: TodoRepository
//
//    @Inject
//    lateinit var jsonConverter: JsonConverter
//
//    override suspend fun doWork(): Result {
//        Log.e("Worker", "UpdateTodoWorker - work received")
//        val userId = workerParams.inputData.getString(USER_ID_WORKER_DATA)
//        val todoId = workerParams.inputData.getString(TODO_ID_WORKER_DATA)
//        val data = workerParams.inputData.getString(FIELD_WORKER_DATA)
//        val convertedData = data?.let { jsonConverter.fromJson(data) }
//        Log.i(
//            "Worker",
//            "UpdateTodoWorker key:${convertedData?.key} to value: ${convertedData?.value}"
//        )
//
//        return if (userId == null || todoId == null || convertedData == null) {
//            Log.e("Worker", "UpdateTodoWorker - failed - null data passed")
//            Result.failure()
//        } else if (runAttemptCount > WorkTag.MAX_RETRY_ATTEMPT) {
//            Log.i("Worker", "UpdateTodoWorker - failed - exceed retry count")
//            Result.failure()
//        } else {
//            try {
//                todoRepository.updateTodoNetwork(
//                    userId,
//                    todoId,
//                    mapOf(convertedData.key to convertedData.value)
//                )
//                Log.i("Worker", "UpdateTodoWorker - success")
//                Result.success()
//            } catch (e: Exception) {
//                Log.e("Worker", "UpdateTodoWorker - retrying - ${e.message}")
//                Result.retry()
//            }
//        }
//    }
//}