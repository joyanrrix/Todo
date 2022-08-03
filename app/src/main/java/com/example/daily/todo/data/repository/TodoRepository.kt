package com.example.daily.todo.data.repository

import androidx.lifecycle.LiveData
import com.example.daily.todo.data.TodoDao
import com.example.daily.todo.data.entity.TodoData

class TodoRepository(private val todoDao: TodoDao) {
    fun searchData(isDone: Boolean): LiveData<List<TodoData>> {
        return todoDao.searchData(isDone)
    }

    suspend fun insertData(todoData: TodoData) {
        todoDao.insertData(todoData)
    }

    suspend fun updateData(todoData: TodoData) {
        todoDao.updateData(todoData)
    }

    suspend fun deleteData(todoData: TodoData) {
        todoDao.deleteData(todoData)
    }

}