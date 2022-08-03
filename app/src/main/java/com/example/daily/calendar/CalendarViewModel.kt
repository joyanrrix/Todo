package com.example.daily.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.daily.calendar.data.CalendarDatabase
import com.example.daily.calendar.data.entity.CalendarData
import com.example.daily.calendar.data.repository.CalendarRepository
import com.haibin.calendarview.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlin.collections.HashMap

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val calendarDao = Room.databaseBuilder(
        application.applicationContext,
        CalendarDatabase::class.java, "calendar_database"
    ).build().calendarDao()
    private val repository = CalendarRepository(calendarDao)

    val allData = repository.data

    val map: LiveData<HashMap<String, Calendar>> get() = _map
    private var _map = MutableLiveData<HashMap<String, Calendar>>()


    private fun getSchemeCalendar(year: Int, month: Int, day: Int, text: String = "scheme"): Calendar {
        return Calendar().apply {
            setYear(year+1900)
            setMonth(month+1)
            setDay(day)
            scheme = text
        }
    }

    fun updateMap(list: List<String>) {
        val temp = HashMap<String, Calendar>()
        val format = SimpleDateFormat("yyyy-MM-dd")
        for (i in 0..list.size-1) {
            val date = format.parse(list[i])
            temp[getSchemeCalendar(date.year, date.month, date.date).toString()]=
                getSchemeCalendar(date.year, date.month, date.date)
        }
        _map.value = temp
    }

    fun getDifferentDate(): LiveData<List<String>> {
        return repository.getDifferentDate()
    }

    fun insertData(calendarData: CalendarData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(calendarData)
        }
    }

    fun updateData(calendarData: CalendarData){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateData(calendarData)
        }
    }
    fun deleteData(calendarData: CalendarData){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteData(calendarData)
        }
    }

    fun searchDatabaseByDate(searchDate: String, searchDone: Boolean): LiveData<List<CalendarData>> {
        return repository.searchDatabaseByDate(searchDate, searchDone)
    }
}