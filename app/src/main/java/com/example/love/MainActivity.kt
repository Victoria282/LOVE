package com.example.love

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.love.BroadcastReceiver.BroadcastReceiver
import com.example.love.Service.AlarmService
import com.example.love.database.AppDatabase
import com.example.love.databinding.ActivityMainBinding
import com.example.love.model.TaskDB
import com.example.love.view_model.MainViewModel
import ru.unit6.course.android.retrofit.utils.Status

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_settings)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        AppDatabase.invoke(applicationContext)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setupObservers()

    }

    fun getMyData(): String? {
        return if(intent.getStringExtra("result") != null) {
            intent.getStringExtra("result").toString()
        } else {
            ""
        }
    }

    fun sendActionToBroadcast(time: Long, action:String) {
        val testIntent = Intent(this, BroadcastReceiver::class.java)
        testIntent.putExtra("alarmInfo", time)
        testIntent.action = action
        sendBroadcast(testIntent)
        stopService(Intent(this, AlarmService::class.java))
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
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}