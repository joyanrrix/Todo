package com.example.daily.calendar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.daily.calendar.data.entity.CalendarData

@Database(entities = [CalendarData::class], version = 1)
abstract class CalendarDatabase: RoomDatabase() {
    abstract fun calendarDao(): CalendarDao
}