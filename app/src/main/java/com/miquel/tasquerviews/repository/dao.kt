package com.miquel.tasquerviews.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao{
    @Query("SELECT * FROM user WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?
    @Query("UPDATE user SET songPosition = :position WHERE userId = (SELECT userId FROM user ORDER BY userId DESC LIMIT 1)")
    suspend fun updateSongPositionToLastUser(position: Int)
    @Query("UPDATE user SET songPosition = :position WHERE email = :email")
    suspend fun updateSongPositionToUserEmail(email: String, position: Int)
    @Insert
    suspend fun addUser(user: User)
    @Update
    suspend fun updateUser(user: User)
    @Delete
    suspend fun deleteUser(user: User)
}

@Dao
interface TaskDao{
    @Query("SELECT COUNT(*) FROM task_item WHERE email = :email")
    suspend fun getAmountTaskOfUserEmail(email: String): Int
    @Query("SELECT * FROM task_item WHERE userId = :userId")
    suspend fun getTasksByUserId(userId: String): List<TaskItem>
    @Query("SELECT * FROM task_item WHERE email = :email")
    suspend fun getTaskByUserEmail(email: String): List<TaskItem>
    @Insert
    suspend fun addTask(task: TaskItem): Long
    @Update
    suspend fun updateTask(task: TaskItem)
    @Delete
    suspend fun deleteTask(task: TaskItem)


}