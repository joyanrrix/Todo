package com.example.daily.calendar.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_table")
data class CalendarData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var date: String,
    var time: String,
    var done: Boolean
)