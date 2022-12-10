package com.example.to_dolistclone.core.common

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.ui.DetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

const val NOTIFICATION_TITLE = "notification title"
const val NOTIFICATION_BODY = "notification body"
const val TODO_ID = "todoId"
const val ALARM_REF = "alarm ref"
const val ACTION_COMPLETE_TASK = "complete task"

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var dateUtil: DateUtil

    override fun onReceive(context: Context, intent: Intent) {

        val notification = ReminderNotificationService(context)

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        val todoId = intent.getStringExtra(TODO_ID) ?: ""
        val notificationId = intent.getIntExtra(ALARM_REF, -1)
        val body = intent.getStringExtra(NOTIFICATION_TITLE) ?: ""

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notification.showNotification(
            todoId = todoId, notificationId = notificationId, body = body
        )

        if (intent.getStringExtra(ACTION_COMPLETE_TASK) == "complete task") {
            Log.i("AlarmReceiver", "action triggered")
            scope.launch {
                try {
                    val todo = todoRepository.getTodo(todoId).first()
                    if (!todo.isComplete) {
                        val currentTime = dateUtil.getCurrentDateTimeLong()
                        todoRepository.updateTodoCompletion(
                            todoId, true, currentTime, dateUtil.getCurrentDateTimeLong()
                        )
                    }
                } catch (e: Exception) {
                    Log.e("AlarmReceiver", "onReceive - errorMsg: ${e.message}")
                } finally {
                    notificationManager.cancel(notificationId)
                    scope.cancel()
                }
            }.invokeOnCompletion {
                Log.i("AlarmReceiver", "update todoCompletion job is completed, $it")
            }
        }
    }

}

const val REMINDER_CHANNEL_ID = "todo_alarm"
const val REMINDER_CHANNEL_GROUP = "com.example.to_dolistclone.REMINDER_GROUP"

class ReminderNotificationService @Inject constructor(@ActivityContext private val context: Context) {

    fun showNotification(todoId: String, notificationId: Int, body: String) {

        val flags =
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0

        // pending intent to launch app at details activity with backstack to home activity when notification body is clicked
        val detailsActivityIntent = Intent(context, DetailsActivity::class.java).apply {
            putExtra(TODO_ID, todoId)
        }

        val detailsActivityPendingIntentWithBackStack = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(detailsActivityIntent)
            getPendingIntent(notificationId, flags)
        }

        // pending intent to complete todo when notification action "complete" is clicked
        val completeTodoIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(TODO_ID, todoId)
            putExtra(ACTION_COMPLETE_TASK, "complete task")
            putExtra(ALARM_REF, notificationId)
        }

        val completeTodoPendingIntent = PendingIntent.getBroadcast(
            context, 1, completeTodoIntent, flags
        )

        val todoNotification = NotificationCompat
            .Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("ToDo App")
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(detailsActivityPendingIntentWithBackStack)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .addAction(0, "Completed", completeTodoPendingIntent)


        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.apply {
            notify(notificationId, todoNotification.build())
        }
    }

}