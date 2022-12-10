//package com.example.to_dolistclone.core.common.worker.task
//
//import android.content.Context
//import android.util.Log
//import androidx.hilt.work.HiltWorker
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//import com.example.to_dolistclone.core.common.worker.JsonConverter
//import com.example.to_dolistclone.core.common.worker.WorkTag
//import com.example.to_dolistclone.core.common.worker.WorkTag.FIELD_WORKER_DATA
//import com.example.to_dolistclone.core.common.worker.WorkTag.TASK_ID_WORKER_DATA
//import com.example.to_dolistclone.core.common.worker.WorkTag.USER_ID_WORKER_DATA
//import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
//import dagger.assisted.Assisted
//import dagger.assisted.AssistedInject
//import javax.inject.Inject
//
//@HiltWorker
//class UpdateTaskWorker @AssistedInject constructor(
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
//        Log.e("Worker", "UpdateTaskWorker - work received")
//        val userId = workerParams.inputData.getString(USER_ID_WORKER_DATA)
//        val taskId = workerParams.inputData.getString(TASK_ID_WORKER_DATA)
//        val data = workerParams.inputData.getString(FIELD_WORKER_DATA)
//        val convertedData = data?.let { jsonConverter.fromJson(data) }
//        Log.i(
//            "Worker",
//            "UpdateTaskWorker key:${convertedData?.key} to value: ${convertedData?.value}"
//        )
//
//        return if (userId == null || taskId == null || convertedData == null) {
//            Log.e("Worker", "UpdateTaskWorker - failed - null data passed")
//            Result.failure()
//        } else if (runAttemptCount > WorkTag.MAX_RETRY_ATTEMPT) {
//            Log.i("Worker", "UpdateTaskWorker - failed - exceed retry count")
//            Result.failure()
//        } else {
//            try {
//                todoRepository.updateTaskNetwork(
//                    userId,
//                    taskId,
//                    mapOf(convertedData.key to convertedData.value!!)
//                )
//                Log.i("Worker", "UpdateTaskWorker - success")
//                Result.success()
//            } catch (e: Exception) {
//                Log.e("Worker", "UpdateTaskWorker - retrying - ${e.message}")
//                Result.retry()
//            }
//        }
//    }
//}