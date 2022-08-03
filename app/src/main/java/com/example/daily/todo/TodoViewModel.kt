package com.example.daily.todo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.daily.todo.data.TodoDatabase
import com.example.daily.todo.data.entity.TodoData
import com.example.daily.todo.data.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(application: Application): AndroidViewModel(application) {
    private val todoDao = Room.databaseBuilder(
        application.applicationContext,
        TodoDatabase::class.java,
        "todo_database"
    ).build().TodoDao()
    private val repository = TodoRepository(todoDao)

    fun searchData(isDone: Boolean): LiveData<List<TodoData>> {
        return repository.searchData(isDone)
    }

    fun insertData(todoData: TodoData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(todoData)
        }
    }

    fun updateData(todoData: TodoData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateData(todoData)
        }
    }

    fun deleteData(todoData: TodoData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteData(todoData)
        }
    }
}