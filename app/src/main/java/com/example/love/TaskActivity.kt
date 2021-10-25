package com.example.love

import android.app.ActivityManager
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.love.Service.AlarmService
import com.example.love.databinding.ActivityTaskBinding
import android.view.Gravity
import com.example.love.BroadcastReceiver.BroadcastReceiver
import com.example.love.view_model.DatabaseViewModel

class TaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding
    private lateinit var DBviewModel: DatabaseViewModel

    private val randIndexTask = (1..6).random() - 1

    private var rightAnswer: String = ""
    private var userAnswer: String = ""

    private var countOfAnswer: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        DBviewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)

        setContentView(binding.root)

        wakeUpApp()
        startAlarmService()
        getTaskFromDB()

        binding.buttonOffAlarm.setOnClickListener {
            userAnswer = binding.editTextTextPersonName.text.toString().trim()
            when (userAnswer) {
                rightAnswer -> {
                    finishTaskActivity("true")
                }
                "" -> {
                    showMessage("Введите ответ!")
                }
                else -> {
                    if(countOfAnswer != 0) {
                        countOfAnswer--
                        showMessage("Не верно, попробуйте ещё..")
                    }
                    else {
                        countOfAnswer = 1
                        val testIntent = Intent(this, BroadcastReceiver::class.java)
                        testIntent.putExtra("alarmInfo", System.currentTimeMillis() + 1000 * 3)
                        testIntent.action = "set"
                        sendBroadcast(testIntent)
                        finishTaskActivity("false")
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
    private fun showMessage(str: String) {
        val toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
        toast.setText(str)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    private fun finishTaskActivity(msg: String) {
        stopService(Intent(this, AlarmService::class.java))
        val nextActivityMain = Intent(this, MainActivity::class.java)
        nextActivityMain.putExtra("result", msg)
        if(msg == "false") {
            countOfAnswer = 1
        }
        startActivity(nextActivityMain)
    }

    private fun wakeUpApp() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        window.setBackgroundDrawable(ColorDrawable(0))
        volumeControlStream = AudioManager.STREAM_VOICE_CALL

        window.apply{
            decorView.keepScreenOn = true
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = 0
            navigationBarColor = 0

            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    private fun startAlarmService() {
        if(!isMyServiceRunning(AlarmService::class.java)) {
            startService(Intent(this, AlarmService::class.java))
        }
    }

    private fun isMyServiceRunning(myService: Class<AlarmService>):Boolean {
        val manager : ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for(service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE))
            if(myService.name.equals(service.service.className)) {
                return true
            }
        return false
    }

    private fun getTaskFromDB() {
        DBviewModel.readAllData.observe(this)  {
            binding.textView.text = it[randIndexTask].task
            rightAnswer = it[randIndexTask].answer
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, AlarmService::class.java))
    }
}