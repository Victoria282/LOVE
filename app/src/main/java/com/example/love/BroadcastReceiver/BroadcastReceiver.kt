package com.example.love.BroadcastReceiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.love.MainActivity
import com.example.love.Service.AlarmService
import com.example.love.TaskActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.love.SharedPreferences.SharedPreferences.alarmAction
import com.example.love.SharedPreferences.SharedPreferences.alarmTime
import com.example.love.SharedPreferences.SharedPreferences.timeAlarm
import com.example.love.other.animation.Constants

class BroadcastReceiver(): BroadcastReceiver() {
    private lateinit var alarmManager: AlarmManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmTime = intent.getLongExtra("alarmInfo", 0L)

        when(intent.action) {
            "set" -> setAlarm(alarmTime, context)
            "cancel" -> cancelAlarm(context)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setAlarm(alarmTime:Long, context: Context) {
        val intent = Intent(context, AlarmService::class.java)
        val pendingIntentTest = PendingIntent.getForegroundService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(alarmTime, pendingIntentTest), pendingIntentTest)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cancelAlarm(context: Context) {
        val intent = Intent(context, AlarmService::class.java)
        val pendingIntent = PendingIntent.getForegroundService(context, 0, intent, PendingIntent.FLAG_NO_CREATE)
        //alarmManager.cancel(pendingIntent)
        pendingIntent?.cancel()
    }
}