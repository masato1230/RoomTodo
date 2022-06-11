package com.jp_funda.roomtodo

import androidx.room.*

@Dao
interface TodoDao {
    // Create
    @Insert
    fun insertTodo(todo: Todo)

    // Read
    @Query("SELECT * FROM Todo")
    fun getAllTodos(): List<Todo>

    // Update
    @Update
    fun updateTodo(todo: Todo)

    // Delete
    @Delete
    fun deleteTodo(todo: Todo)
}