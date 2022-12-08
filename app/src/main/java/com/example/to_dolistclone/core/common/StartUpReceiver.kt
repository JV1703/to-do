package com.example.to_dolistclone.core.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.to_dolistclone.core.repository.abstraction.TodoRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StartUpReceiver : BroadcastReceiver() {

    @Inject
    lateinit var dateUtil: DateUtil

    @Inject
    lateinit var todoRepository: TodoRepository

    private lateinit var alarmManager: AlarmManager

    override fun onReceive(context: Context, intent: Intent) {

        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

            val currentTime = dateUtil.getCurrentDateTimeLong()

            val flags =
                PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            scope.launch {
                val todos = todoRepository.getTodos().first()
                todos.filter { it.reminder != null && !it.isComplete && it.reminder > currentTime && it.alarmRef != null }
                    .forEach {
                        val i = Intent(context, AlarmReceiver::class.java)
                        i.putExtra(TODO_ID, it.todoId)
                        i.putExtra(ALARM_REF, it.alarmRef)
                        i.putExtra(NOTIFICATION_TITLE, it.title)
                        val pendingIntent =
                            PendingIntent.getBroadcast(context, it.alarmRef!!, i, flags)
                        alarmManager.set(AlarmManager.RTC, it.reminder!!, pendingIntent)
                        Log.i(
                            "startup",
                            "title: ${it.title}, reminderOn: ${
                                dateUtil.toString(
                                    it.reminder,
                                    "EEE, MM dd",
                                    java.util.Locale.getDefault()
                                )
                            }"
                        )
                    }
            }
        }
    }
}