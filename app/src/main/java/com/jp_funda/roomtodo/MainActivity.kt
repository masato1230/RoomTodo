package com.jp_funda.roomtodo

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.R
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
    val isShowDialog = remember { mutableStateOf(false) }

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { isShowDialog.value = true }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "追加ボタン")
        }
    }) {
        if (isShowDialog.value) {
            EditDialog(isShowDialog = isShowDialog, viewModel = viewModel)
        }
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
        title = { Text(text = "Todo新規作成") },
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
                        viewModel.addTodo()
                    },
                ) {
                    Text(text = "OK")
                }
            }
        }
    )
}