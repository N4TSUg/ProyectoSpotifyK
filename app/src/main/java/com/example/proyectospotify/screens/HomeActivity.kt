package com.example.proyectospotify.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.proyectospotify.entities.JamendoAlbum
import com.example.proyectospotify.entities.JamendoTrack
import com.example.proyectospotify.search.SearchViewModel
import com.example.proyectospotify.ui.theme.ProyectoSpotifyTheme
import com.example.proyectospotify.viewmodels.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

// Playlists (Room)
import com.example.proyectospotify.playlists.PlaylistViewModel
import com.example.proyectospotify.playlists.PlaylistEntity
import com.example.proyectospotify.playlists.SongEntity

class HomeActivity : ComponentActivity() {

    private val searchVm: SearchViewModel by viewModels()
    private val playlistVm: PlaylistViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        prefs.edit().remove("isLoggedIn").apply()

        val intent = Intent(this, WelcomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs: SharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        setContent {
            ProyectoSpotifyTheme {
                val albumsState by homeViewModel.albums.collectAsState()

                var selectedIndex by remember { mutableStateOf(0) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Inicio", color = Color.White) },
                            actions = {
                                // Icono "puerta de salida"
                                IconButton(onClick = { logout() }) {
                                    Icon(
                                        imageVector = Icons.Filled.ExitToApp,
                                        contentDescription = "Cerrar sesión",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
                        )
                    },
                    bottomBar = {
                        NavigationBar(containerColor = Color.Black) {
                            NavigationBarItem(
                                selected = selectedIndex == 0,
                                onClick = { selectedIndex = 0 },
                                icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                                label = { Text("Inicio") }
                            )
                            NavigationBarItem(
                                selected = selectedIndex == 1,
                                onClick = { selectedIndex = 1 },
                                icon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                                label = { Text("Buscar") }
                            )
                            NavigationBarItem(
                                selected = selectedIndex == 2,
                                onClick = { selectedIndex = 2 },
                                icon = { Icon(Icons.Filled.LibraryMusic, contentDescription = "PlayLists") },
                                label = { Text("PlayLists") }
                            )
                        }
                    },
                    containerColor = Color.Black
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        var openedPlaylistId by remember { mutableStateOf<Long?>(null) }
                        when (selectedIndex) {
                            0 -> HomeContent(albumsState)
                            1 -> SearchTab(searchVm, playlistVm)
                            2 -> {
                                val pid = openedPlaylistId
                                if (pid == null) {
                                    // Lista de playlists
                                    PlaylistTab(
                                        playlistVm = playlistVm,
                                        onOpen = { id ->
                                            openedPlaylistId = id
                                        }   // ← al tocar una playlist
                                    )
                                } else {
                                    // Detalle de una playlist
                                    PlaylistDetailTab(
                                        playlistId = pid,
                                        playlistVm = playlistVm,
                                        onBack = {
                                            openedPlaylistId = null
                                        }       // ← volver a la lista
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumCard(album: JamendoAlbum) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Crear intent y enviar datos al detalle
                val intent = Intent(context, AlbumDetailActivity::class.java).apply {
                    putExtra("album_id", album.id)
                    putExtra("album_name", album.name)
                    putExtra("album_image", album.image)
                }
                context.startActivity(intent)
            }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(album.image)
                .crossfade(true)
                .placeholder(android.R.drawable.stat_sys_download)
                .error(android.R.drawable.ic_menu_report_image)
                .build(),
            contentDescription = album.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        Text(
            text = album.name,
            color = Color.White,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )
    }
}

@Composable
fun HomeContent(albums: List<JamendoAlbum>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // fondo seguro para depuración
            .padding(12.dp)
    ) {
        Text(
            text = "Álbumes recomendados",
            color = Color.White,
            fontSize = 22.sp,
            modifier = Modifier.padding(8.dp)
        )

        if (albums.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Green)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(albums) { album ->
                    AlbumCard(album)
                }
            }
        }
        if (albums.isEmpty()) {
            Text("No hay álbumes disponibles", modifier = Modifier.padding(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTab(
    vm: SearchViewModel,
    playlistVm: PlaylistViewModel
) {
    var query by rememberSaveable { mutableStateOf("") }
    val results by vm.results.collectAsState()
    val focus = LocalFocusManager.current

    // ESTADOS para añadir a playlist
    var showPickPlaylist by remember { mutableStateOf(false) }
    var trackToAdd by remember { mutableStateOf<JamendoTrack?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Buscar canciones") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                vm.search(query)
                focus.clearFocus()
            })
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                vm.search(query)
                focus.clearFocus()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Buscar") }

        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(results) { track ->
                ListItem(
                    headlineContent = { Text(track.name) },
                    supportingContent = { Text(track.artist_name) },
                    trailingContent = {
                        TextButton(onClick = {
                            trackToAdd = track
                            showPickPlaylist = true
                        }) { Text("Añadir") }
                    }
                )
                Divider()
            }
        }
    }

    // Diálogo para elegir playlist
    if (showPickPlaylist && trackToAdd != null) {
        SelectPlaylistDialog(
            playlistVm = playlistVm,
            onDismiss = {
                showPickPlaylist = false
                trackToAdd = null
            },
            onPick = { playlist ->
                val t = trackToAdd!!
                playlistVm.addSong(
                    playlistId = playlist.playlistId,
                    song = SongEntity(
                        songId = t.id,
                        title = t.name,
                        artist = t.artist_name,
                        imageUrl = t.image,
                        audioUrl = t.audio
                    )
                )
                showPickPlaylist = false
                trackToAdd = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPlaylistDialog(
    playlistVm: PlaylistViewModel,
    onDismiss: () -> Unit,
    onPick: (PlaylistEntity) -> Unit
) {
    val playlists by playlistVm.playlists.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Elige una playlist") },
        text = {
            if (playlists.isEmpty()) {
                Text("No tienes playlists. Crea una en la pestaña Playlists (+).")
            } else {
                LazyColumn {
                    items(playlists) { p ->
                        ListItem(
                            headlineContent = { Text(p.name) },
                            supportingContent = { Text("ID ${p.playlistId}") },
                            trailingContent = {
                                TextButton(onClick = { onPick(p) }) { Text("Usar") }
                            }
                        )
                        Divider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistTab(
    playlistVm: com.example.proyectospotify.playlists.PlaylistViewModel,
    onOpen: (Long) -> Unit
) {
    val playlists by playlistVm.playlists.collectAsState()
    var showCreate by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tus Playlists") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) { Text("+") }
        }
    ) { padding ->
        if (playlists.isEmpty()) {
            Column(Modifier.padding(padding).padding(16.dp)) {
                Text("Aún no tienes playlists.")
                Spacer(Modifier.height(8.dp))
                Text("Toca + para crear tu primera playlist.")
            }
        } else {
            LazyColumn(Modifier.padding(padding).padding(8.dp)) {
                items(playlists) { p ->
                    ListItem(
                        headlineContent = { Text(p.name) },
                        supportingContent = { Text("ID ${p.playlistId}") },
                        modifier = Modifier.fillMaxWidth().clickable { onOpen(p.playlistId) }
                    )
                    Divider()
                }
            }
        }
    }

    if (showCreate) {
        CreatePlaylistDialog(
            onDismiss = { showCreate = false },
            onCreate = { name -> playlistVm.create(name); showCreate = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailTab(
    playlistId: Long,
    playlistVm: com.example.proyectospotify.playlists.PlaylistViewModel,
    onBack: () -> Unit
) {
    val playlistWithSongs by playlistVm
        .playlistWithSongs(playlistId)
        .collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playlistWithSongs?.playlist?.name ?: "Playlist") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        val songs = playlistWithSongs?.songs ?: emptyList()

        if (songs.isEmpty()) {
            Column(Modifier.padding(padding).padding(16.dp)) {
                Text("Esta playlist no tiene canciones.")
                Spacer(Modifier.height(8.dp))
                Text("Ve a Buscar y añade algunas. 🙂")
            }
        } else {
            LazyColumn(Modifier.padding(padding)) {
                items(songs) { s ->
                    ListItem(
                        headlineContent = { Text(s.title) },
                        supportingContent = { Text(s.artist) },
                        trailingContent = {
                            TextButton(onClick = {
                                // Eliminar de la playlist (opcional)
                                playlistVm.removeSong(playlistId, s.songId)
                            }) { Text("Quitar") }
                        }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva playlist") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la playlist") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onCreate(name) }) { Text("Crear") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}