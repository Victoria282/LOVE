package com.example.love

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
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
import com.example.love.model.TaskDB
import com.example.love.view_model.MainViewModel
import ru.unit6.course.android.retrofit.utils.Status
import android.view.Gravity
import com.example.love.SharedPreferences.SharedPreferences.app
import com.example.love.other.animation.Constants
import com.example.love.other.animation.ObjectPending
import com.example.love.other.animation.PrefConfig
import com.google.gson.annotations.JsonAdapter

class TaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding
    private lateinit var viewModel: MainViewModel

    private val randIndexTask = (1..4).random() - 1

    private var rightAnswer: String = ""
    private var userAnswer: String = ""

    private var countOfAnswer: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setContentView(binding.root)

        wakeUpApp()
        startAlarmService()
        setupObservers()

        binding.buttonOffAlarm.setOnClickListener {
            userAnswer = binding.editTextTextPersonName.text.toString().trim()
            when (userAnswer) {
                rightAnswer -> {
                    ObjectPending.globalList.clear()
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
                        countOfAnswer = 2
                        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val intent = Intent(this, com.example.love.BroadcastReceiver.BroadcastReceiver::class.java)
                        intent.action = "set"
                        intent.putExtra("alarmInfo", System.currentTimeMillis())
                        val pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        ObjectPending.globalList.add(pi)
                        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(System.currentTimeMillis() + 1000 * 60 * 3, pi), pi)
                        finishTaskActivity("false")
                    }
                }
            }
        }
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
            countOfAnswer = 2
        }
        startActivity(nextActivityMain)
    }

    private fun wakeUpApp() {
        // ПРОБУЖДЕНИЕ ЭКРАНА
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

    private fun setupObservers() {
        viewModel.getAllTasks().observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    resource.data?.let { task ->
                        viewModel.setAllTasksToDatabase(
                            tasks = task.map { task ->
                                TaskDB(
                                    id = task.id,
                                    task = task.task,
                                    answer = task.answer
                                )
                            }
                        )
                    }
                }
                Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    binding.textView.text = "Что-то пошло не так.."
                }
            }
        }
        getTaskFromDB()
    }

    private fun getTaskFromDB() {
        viewModel.localTasks.observe(this)  {
            binding.textView.text = it[randIndexTask].task
            rightAnswer = it[randIndexTask].answer
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, AlarmService::class.java))
    }

}