package com.example.daily.todo.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.daily.todo.data.entity.TodoData

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_table WHERE done = :isDone ORDER BY scheme ASC")
    fun searchData(isDone: Boolean): LiveData<List<TodoData>>

    @Insert
    suspend fun insertData(todoData: TodoData)

    @Delete
    suspend fun deleteData(todoData: TodoData)

    @Update
    suspend fun updateData(todoData: TodoData)
}
