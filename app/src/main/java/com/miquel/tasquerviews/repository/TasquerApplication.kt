package com.miquel.tasquerviews.repository

import android.app.Application
import androidx.room.Room

class TasquerApplication :  Application() {
    companion object{
        lateinit var database: TasksDatabase
    }
    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            TasksDatabase::class.java,
            "tasks_database")
            .fallbackToDestructiveMigration()
            .build()
    }
}