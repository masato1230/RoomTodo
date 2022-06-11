package com.jp_funda.roomtodo

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {
    private val todoRepository: TodoRepository

    init {
        val todoDatabase = TodoDatabase.getInstance(application)
        val todoDao = todoDatabase.todoDao()
        todoRepository = TodoRepository(todoDao)
    }

    private val _todos = MutableLiveData<List<Todo>>()
    val todos: LiveData<List<Todo>> = _todos

    // Todo全権取得し直し
    private fun refreshTodos() {
        viewModelScope.launch {
            _todos.postValue(todoRepository.getAllTodo())
        }
    }

    // 追加
    fun addTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.createTodo(todo)
            refreshTodos()
        }
    }

    // 更新
    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.updateTodo(todo)
            refreshTodos()
        }
    }

    // 削除
    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todo)
            refreshTodos()
        }
    }

    class MainViewModelFactory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(application) as T
        }
    }
}