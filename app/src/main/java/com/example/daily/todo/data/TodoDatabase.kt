package com.example.daily.todo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.daily.todo.data.entity.TodoData

@Database(entities = [TodoData::class], version = 1)
abstract class TodoDatabase: RoomDatabase() {
    abstract fun TodoDao(): TodoDao
}