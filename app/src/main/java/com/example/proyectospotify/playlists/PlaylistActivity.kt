package com.example.proyectospotify.playlists

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class PlaylistActivity: ComponentActivity() {
    private val vm: PlaylistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PlaylistScreen(vm)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(vm: PlaylistViewModel) {
    val playlists by vm.playlists.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Playlists") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp)) {
            items(playlists) { p ->
                ListItem(
                    headlineContent = { Text(p.name) },
                    supportingContent = { Text("ID ${p.playlistId}") }
                )
                Divider()
            }
        }
    }

    if (showDialog) CreatePlaylistDialog(onDismiss = { showDialog = false }) { name ->
        vm.create(name)
        showDialog = false
    }
}

@Composable
fun CreatePlaylistDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva playlist") },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
        },
        confirmButton = {
            TextButton(onClick = { onCreate(name) }) { Text("Crear") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}