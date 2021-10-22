package com.example.love.BroadcastReceiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.core.content.ContextCompat
import com.example.love.MainActivity
import com.example.love.Service.AlarmService
import com.example.love.TaskActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class BroadcastReceiver(): BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            "set" -> setAlarm(context)
        }
    }
    private fun setAlarm(context: Context) {
        val intent = Intent(context, AlarmService::class.java)
        context.startService(intent)
    }
}