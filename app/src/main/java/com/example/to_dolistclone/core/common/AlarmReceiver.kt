package com.example.to_dolistclone.core.common

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import com.example.to_dolistclone.feature.detail.ui.DetailsActivity

const val NOTIFICATION_TITLE = "notification title"
const val NOTIFICATION_BODY = "notification body"
const val TODO_ID = "todoId"

class AlarmReceiver(private val notification: ReminderNotificationService/*, private val todoRepository: TodoRepository*/) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val todoId = intent.getStringExtra(TODO_ID)
        if(!todoId.isNullOrEmpty()){
            Toast.makeText(context, todoId, Toast.LENGTH_SHORT).show()
        }

        val service = ReminderNotificationService(context)
        service.showNotification(
            intent.getStringExtra(NOTIFICATION_TITLE) ?: "",
            intent.getStringExtra(NOTIFICATION_BODY) ?: "",
            intent.getStringExtra(TODO_ID) ?: ""
        )
    }

}

const val REMINDER_CHANNEL_ID = "todo_alarm"
const val notificationId = 1

class ReminderNotificationService(private val context: Context) {

    fun showNotification(title: String, body: String, todoId: String) {

        val flags =
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0

//        val receiverIntent = Intent(context, AlarmReceiver::class.java).apply {
//            putExtra(TODO_ID, todoId)
//        }
//        val receiverPendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, flags)

        val intent = Intent(context, DetailsActivity::class.java)
        val pendingIntentWithBackStack = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, flags)
        }
        val builder = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification).setContentTitle(title).setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntentWithBackStack).setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(notificationId, builder.build())
    }

}