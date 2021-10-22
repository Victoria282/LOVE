package com.example.love

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.love.BroadcastReceiver.BroadcastReceiver
import com.example.love.Service.AlarmService
import com.example.love.SharedPreferences.SharedPreferences.arr
import com.example.love.database.AppDatabase
import com.example.love.databinding.ActivityMainBinding
import com.example.love.other.animation.ObjectPending
import com.example.love.other.animation.PrefConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var alarmManager: AlarmManager
    private lateinit var arr: ArrayList<PendingIntent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

       // ModelPreferencesManager.with(application)
       /* PrefConfig.readListFromPref(this).also {
            if (it != null) {
                arr = it
                ObjectPending.globalList = arr
            }
            else
                arr = ArrayList<PendingIntent>()
        }*/

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_settings)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        AppDatabase.invoke(applicationContext)
    }

    fun getMyData(): String? {
        return intent.getStringExtra("result").toString()
    }

    fun sendActionToBroadcast(time: Long, action:String) {
        val testIntent = Intent(this, BroadcastReceiver::class.java)
        testIntent.putExtra("alarmInfo", time.toString())
        testIntent.action = action
        val pendingIntentTest = PendingIntent.getBroadcast(this, 0, testIntent!!, PendingIntent.FLAG_UPDATE_CURRENT)
        ObjectPending.globalList.add(pendingIntentTest)

        if(action == "cancel") {
            val neededPending = ObjectPending.globalList[ObjectPending.globalList.size-2]
            alarmManager.cancel(neededPending)
            ObjectPending.globalList.clear()
            stopService(Intent(this, AlarmService::class.java))
        }

        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(time, pendingIntentTest), pendingIntentTest)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}