package com.example.love.Service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import androidx.annotation.RequiresApi
import com.example.love.other.animation.Constants.CHANNEL_ID
import com.example.love.other.animation.Constants.FOREGROUND_ID
import com.example.love.other.animation.Constants.NOTIFICATION_CHANNEL_ID
import com.example.love.other.animation.Constants.NOTIFICATION_ID
import com.example.love.R
import com.example.love.TaskActivity

class AlarmService: Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private val pattern: LongArray = longArrayOf(0, 500, 1000)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.music)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        showNotification()
        mediaPlayer?.isLooping
        mediaPlayer?.start()
        vibrator!!.vibrate(pattern, 0)
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            NOTIFICATION_ID,
            NOTIFICATION_CHANNEL_ID,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification() {
        val notificationIntent = Intent(this, TaskActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, 0)
        val notification = Notification.Builder (this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("IT Alarm")
            .setContentText("Просыпайся!")
            .setSmallIcon(R.drawable.ic_nights_stay)
            .setContentIntent(pendingIntent)
            .setChannelId(CHANNEL_ID)
            .build()
        startForeground(FOREGROUND_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        vibrator?.cancel()
        stopForeground(true)
    }
}