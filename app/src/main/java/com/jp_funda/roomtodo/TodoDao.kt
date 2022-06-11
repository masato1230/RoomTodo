package com.jp_funda.roomtodo

import androidx.room.*

@Dao
interface TodoDao {
    // Create
    @Insert
    suspend fun insertTodo(todo: Todo)

    // Read
    @Query("SELECT * FROM Todo")
    suspend fun getAllTodos(): List<Todo>

    // Update
    @Update
    suspend fun updateTodo(todo: Todo)

    // Delete
    @Delete
    suspend fun deleteTodo(todo: Todo)
}