package com.example.love.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.love.database.AppDatabase
import com.example.love.model.TaskDB
import com.example.love.repository.DatabaseRepository

class DatabaseViewModel(application: Application): AndroidViewModel(application) {
    val readAllData: LiveData<List<TaskDB>>
    private val databaseRepository: DatabaseRepository

    init {
        val userDao = AppDatabase.invoke(application).userDao()
        databaseRepository = DatabaseRepository(userDao)
        readAllData = databaseRepository.readAllData
    }
}