package com.example.love

import android.app.ActivityManager
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.love.BroadcastReceiver.Receiver
import com.example.love.Service.AlarmService
import com.example.love.databinding.ActivityTaskBinding
import com.example.love.model.TaskDB
import com.example.love.view_model.MainViewModel
import ru.unit6.course.android.retrofit.utils.Status

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

        // пробуждение экрана
        wakeUpApp()

        // запуск сервиса с музыкой и вибрацией
        startAlarmService()
        setupObservers()

        binding.buttonOffAlarm.setOnClickListener {
            userAnswer = binding.editTextTextPersonName.text.toString().trim()
            countOfAnswer--

            when (userAnswer) {
                rightAnswer -> {
                    actionRightAnswer()
                }
                "" -> {
                    Toast.makeText(this, "Введите ответ!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    actionMistakeAnswer()
                }
            }
        }
    }

    private fun actionRightAnswer() {
        val nextActivityMain = Intent(this, MainActivity::class.java)
        stopService(Intent(this, AlarmService::class.java))
        nextActivityMain.putExtra("result", "true")
        startActivity(nextActivityMain)
        Receiver(this).cancelAlarm(this)
    }

    private fun actionMistakeAnswer() {
        val nextActivityMain = Intent(this, MainActivity::class.java)
        countOfAnswer = 2
        stopService(Intent(this, AlarmService::class.java))
        nextActivityMain.putExtra("result", "false")
        startActivity(nextActivityMain)
        Receiver(this).repeatAlarm(this)
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
                Status.ERROR -> {
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