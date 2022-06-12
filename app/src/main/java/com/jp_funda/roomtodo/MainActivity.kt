package com.jp_funda.roomtodo

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jp_funda.roomtodo.ui.theme.RoomTodoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoomTodoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val owner = LocalViewModelStoreOwner.current
                    owner?.let {
                        val viewModel = viewModel<MainViewModel>(
                            it,
                            "MainViewModel",
                            MainViewModel.MainViewModelFactory(LocalContext.current.applicationContext as Application)
                        )
                        MainContent(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent(viewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        viewModel.refreshTodos()
    }

    val isShowDialog = remember { mutableStateOf(false) }

    Scaffold(
        backgroundColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(onClick = { isShowDialog.value = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "追加ボタン")
            }
        }) {
        if (isShowDialog.value) {
            EditDialog(isShowDialog = isShowDialog, viewModel = viewModel)
        }

        TodoList(viewModel, isShowDialog)
    }
}

@Composable
fun EditDialog(isShowDialog: MutableState<Boolean>, viewModel: MainViewModel) {
    val title = viewModel.title.observeAsState()
    val description = viewModel.description.observeAsState()

    AlertDialog(
        onDismissRequest = {
            isShowDialog.value = false
            viewModel.clearTitleAndDescription()
        },
        title = { Text(text = if (viewModel.isUpdating) "Todo更新" else "Todo新規作成") },
        text = {
            Column {
                Text(text = "タイトル")
                TextField(
                    value = title.value ?: "",
                    onValueChange = { viewModel.setTitle(it) })
                Text(text = "詳細")
                TextField(
                    value = description.value ?: "",
                    onValueChange = { viewModel.setDescription(it) })
            }
        },
        buttons = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier.width(120.dp),
                    onClick = {
                        isShowDialog.value = false
                        viewModel.clearTitleAndDescription()
                    },
                ) {
                    Text(text = "キャンセル")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    modifier = Modifier.width(120.dp),
                    onClick = {
                        isShowDialog.value = false
                        if (viewModel.isUpdating) {
                            viewModel.updateTodo()
                        } else {
                            viewModel.addTodo()
                        }
                    },
                ) {
                    Text(text = "OK")
                }
            }
        }
    )
}

@Composable
fun TodoList(viewModel: MainViewModel, isShowDialog: MutableState<Boolean>) {
    val observedTodos = viewModel.todos.observeAsState()

    observedTodos.value?.let { todos ->
        LazyColumn {
            items(todos) { todo ->
                TodoRow(todo, viewModel, isShowDialog)
            }
        }
    }
}

@Composable
fun TodoRow(todo: Todo, viewModel: MainViewModel, isShowDialog: MutableState<Boolean>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                viewModel.setUpdatingTodo(todo)
                isShowDialog.value = true
            },
        elevation = 5.dp,
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = todo.title)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { viewModel.deleteTodo(todo) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "削除ボタン")
            }
        }
    }
}