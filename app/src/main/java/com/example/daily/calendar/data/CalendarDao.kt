package com.example.daily.calendar.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.daily.calendar.data.entity.CalendarData

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendar_table")
    fun getAllData():LiveData<List<CalendarData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(calendarData: CalendarData)

    @Update
    suspend fun updateData(calendarData: CalendarData)

    @Delete
    suspend fun deleteData(calendarData: CalendarData)

    @Query("SELECT * FROM calendar_table WHERE done = :searchDone AND date = :searchDate ORDER BY time ASC")
    fun searchDatabaseByDate(searchDate: String, searchDone: Boolean): LiveData<List<CalendarData>>

    @Query("SELECT DISTINCT date FROM calendar_table")
    fun getDifferentDate() : LiveData<List<String>>
}