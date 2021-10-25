package com.example.love.repository

import androidx.lifecycle.LiveData
import com.example.love.database.UserDao
import com.example.love.model.TaskDB

class DatabaseRepository(private val userDao: UserDao) {
    val readAllData: LiveData<List<TaskDB>> = userDao.getAllTasks()
}