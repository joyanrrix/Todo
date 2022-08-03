package com.example.daily.calendar.data.repository

import androidx.lifecycle.LiveData
import com.example.daily.calendar.data.CalendarDao
import com.example.daily.calendar.data.entity.CalendarData

class CalendarRepository(private val calendarDao: CalendarDao) {
    val data: LiveData<List<CalendarData>> = calendarDao.getAllData()

    suspend fun insertData(calendarData: CalendarData) {
        calendarDao.insertData(calendarData)
    }

    suspend fun updateData(calendarData: CalendarData) {
        calendarDao.updateData(calendarData)
    }

    suspend fun deleteData(calendarData: CalendarData){
        calendarDao.deleteData(calendarData)
    }

    fun searchDatabaseByDate(searchDate: String, searchDone: Boolean): LiveData<List<CalendarData>> {
        return calendarDao.searchDatabaseByDate(searchDate, searchDone)
    }

    fun getDifferentDate(): LiveData<List<String>> {
        return calendarDao.getDifferentDate()
    }
}