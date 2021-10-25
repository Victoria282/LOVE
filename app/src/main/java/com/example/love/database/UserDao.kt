package com.example.love.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.love.model.TaskDB

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTasks(tasks: List<TaskDB>)

    @Query("Select * From tasks")
    fun getAllTasks():LiveData<List<TaskDB>>
}