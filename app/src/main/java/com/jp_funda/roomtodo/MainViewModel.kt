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

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    fun setTitle(value: String) {
        _title.value = value
    }

    fun setDescription(value: String) {
        _description.value = value
    }

    fun clearTitleAndDescription() {
        _title.value = ""
        _description.value = ""
    }

    // Todo全権取得し直し
    fun refreshTodos() {
        viewModelScope.launch {
            _todos.postValue(todoRepository.getAllTodo())
        }
    }

    // 追加
    fun addTodo() {
        viewModelScope.launch {
            val todo = Todo(title = _title.value ?: "", description = _description.value ?: "")
            todoRepository.createTodo(todo)
            clearTitleAndDescription()
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