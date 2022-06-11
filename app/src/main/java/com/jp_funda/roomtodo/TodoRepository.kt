package com.jp_funda.roomtodo

class TodoRepository(private val todoDao: TodoDao) {
    /** Create. */
    suspend fun createTodo(todo: Todo) {
        todoDao.insertTodo(todo)
    }

    /** Read. */
    suspend fun getAllTodo(): List<Todo> {
        return todoDao.getAllTodos()
    }

    /** Update. */
    suspend fun updateTodo(todo: Todo) {
        todoDao.updateTodo(todo)
    }

    /** Delete. */
    suspend fun deleteTodo(todo: Todo) {
        todoDao.deleteTodo(todo)
    }
}