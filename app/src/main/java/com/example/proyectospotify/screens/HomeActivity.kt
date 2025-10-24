package com.example.proyectospotify.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.proyectospotify.entities.JamendoAlbum
import com.example.proyectospotify.entities.JamendoTrack
import com.example.proyectospotify.playlists.FirebasePlaylist
import com.example.proyectospotify.playlists.PlaylistDetailActivity
import com.example.proyectospotify.playlists.PlaylistViewModel
import com.example.proyectospotify.search.SearchViewModel
import com.example.proyectospotify.ui.theme.ProyectoSpotifyTheme
import com.example.proyectospotify.viewmodels.HomeViewModel
import com.example.proyectospotify.viewmodels.PlayerViewModel
import com.google.firebase.auth.FirebaseAuth


class HomeActivity : ComponentActivity() {


    private val homeViewModel: HomeViewModel by viewModels()
    private val searchVm: SearchViewModel by viewModels()
    private val playlistVm: PlaylistViewModel by viewModels()

    private val playerViewModel: PlayerViewModel by lazy {
        PlayerViewModel.getInstance(application)
    }

    private fun logout() {
        // 🔹 Reinicia completamente el reproductor antes de cerrar sesión
        playerViewModel.resetPlayer()

        // 🔹 Cierra sesión en Firebase
        FirebaseAuth.getInstance().signOut()

        // 🔹 Limpia preferencias guardadas
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        prefs.edit().remove("isLoggedIn").apply()

        // 🔹 Redirige al login
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoSpotifyTheme {
                val currentTrack by playerViewModel.currentTrack.collectAsState()
                val isPlaying by playerViewModel.isPlaying.collectAsState()
                val currentPosition by playerViewModel.currentPosition.collectAsState()

                val currentAlbumId by playerViewModel.currentAlbumId.collectAsState()
                val currentAlbumName by playerViewModel.currentAlbumName.collectAsState()
                val currentAlbumImage by playerViewModel.currentAlbumImage.collectAsState()

                val albumsState by homeViewModel.albums.collectAsState()
                var selectedIndex by remember { mutableStateOf(0) }
                val accentColor = Color(0xFFE53935)

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    "RedSound",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            actions = {
                                IconButton(onClick = { logout() }) {
                                    Icon(
                                        Icons.Filled.ExitToApp,
                                        "Cerrar sesión",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
                        )
                    },
                    containerColor = Color.Black
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(Color.Black)
                    ) {
                        // Contenido principal con espacio para el minireproductor y la barra
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f)) {
                                when (selectedIndex) {
                                    0 -> HomeContent(albumsState, accentColor)
                                    1 -> SearchTab(searchVm, playlistVm, playerViewModel)
                                    2 -> {
                                        val context = LocalContext.current
                                        PlaylistTab(
                                            playlistVm = playlistVm,
                                            onOpenPlaylist = { playlistId ->
                                                val intent = Intent(
                                                    context,
                                                    PlaylistDetailActivity::class.java
                                                ).apply {
                                                    putExtra("playlist_id", playlistId)
                                                }
                                                context.startActivity(intent)
                                            }
                                        )
                                    }
                                }
                            }

                            // MiniPlayer con padding inferior para no chocar con la barra
                            currentTrack?.let { track ->
                                val context = LocalContext.current
                                MiniPlayer(
                                    track = track,
                                    isPlaying = isPlaying,
                                    currentPosition = currentPosition,
                                    duration = track.duration,
                                    onPlayPause = { playerViewModel.togglePlayPause() },
                                    onNext = { playerViewModel.playNext() },
                                    onPrevious = { playerViewModel.playPrevious() },
                                    onSeek = { newPosition -> playerViewModel.seekTo(newPosition) },
                                    onMiniPlayerClick = {
                                        currentAlbumId?.let { albumId ->
                                            val intent = Intent(
                                                context,
                                                AlbumDetailActivity::class.java
                                            ).apply {
                                                putExtra("album_id", albumId)
                                                putExtra("album_name", currentAlbumName)
                                                putExtra("album_image", currentAlbumImage)
                                            }
                                            context.startActivity(intent)
                                        }
                                    }
                                )
                            }

                            // Barra de navegación
                            NavigationBar(containerColor = Color.Black) {
                                val navItems = listOf("Inicio", "Buscar", "Playlists")
                                val navIcons = listOf(
                                    Icons.Filled.Home,
                                    Icons.Filled.Search,
                                    Icons.Filled.LibraryMusic
                                )

                                navItems.forEachIndexed { index, title ->
                                    NavigationBarItem(
                                        selected = selectedIndex == index,
                                        onClick = { selectedIndex = index },
                                        icon = {
                                            Icon(
                                                navIcons[index],
                                                contentDescription = title
                                            )
                                        },
                                        label = { Text(title) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = accentColor,
                                            selectedTextColor = accentColor,
                                            unselectedIconColor = Color.Gray,
                                            unselectedTextColor = Color.Gray,
                                            indicatorColor = Color.Transparent
                                        )
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistTab(
    playlistVm: PlaylistViewModel,
    onOpenPlaylist: (String) -> Unit
) {
    val playlists by playlistVm.playlists.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var playlistToDelete by remember { mutableStateOf<FirebasePlaylist?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tus Playlists", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Color(0xFFE53935)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear Playlist", tint = Color.White)
            }
        },
        containerColor = Color.Black
    ) { padding ->
        if (playlists.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Aún no tienes playlists.\nToca '+' para crear la primera.",
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(8.dp)
            ) {
                items(playlists) { playlist ->
                    ListItem(
                        headlineContent = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    playlist.name,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        },

                        trailingContent = {
                            IconButton(onClick = { playlistToDelete = playlist }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar Playlist",
                                    tint = Color.Red
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenPlaylist(playlist.id) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                    Divider(color = Color.Gray.copy(alpha = 0.3f))
                }
            }
        }
    }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                playlistVm.create(name)
                showCreateDialog = false
            }
        )
    }

    // Diálogo de confirmación de eliminación
    playlistToDelete?.let { playlist ->
        AlertDialog(
            onDismissRequest = { playlistToDelete = null },
            title = { Text("Eliminar Playlist") },
            text = { Text("¿Estás seguro de que deseas eliminar la playlist \"${playlist.name}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    playlistVm.deletePlaylist(playlist.id)
                    playlistToDelete = null
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { playlistToDelete = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    val accentColor = Color(0xFFE53935)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Playlist", color = Color.White) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la playlist", color = Color.Gray) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = accentColor,
                    focusedIndicatorColor = accentColor,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Crear", color = accentColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF212121)
    )
}

@Composable
fun HomeContent(albums: List<JamendoAlbum>, accentColor: Color) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(accentColor.copy(alpha = 0.2f), Color.Black),
        endY = 800f
    )
    Column(
        modifier = Modifier.fillMaxSize().background(gradientBrush).padding(horizontal = 8.dp)
    ) {
        Text(
            text = "Álbumes Recomendados",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
        )
        if (albums.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accentColor)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(albums) { album -> AlbumCard(album) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTab(
    vm: SearchViewModel,
    playlistVm: PlaylistViewModel,
    playerViewModel: PlayerViewModel
) {
    var query by rememberSaveable { mutableStateOf("") }
    val results by vm.results.collectAsState()
    val focus = LocalFocusManager.current
    val localPlaylists by playlistVm.playlists.collectAsState()
    var showPickPlaylistDialog by remember { mutableStateOf(false) }
    var trackToAdd by remember { mutableStateOf<JamendoTrack?>(null) }
    val currentPlayingTrack by playerViewModel.currentTrack.collectAsState()
    val accentColor = Color(0xFFE53935)

    Column(Modifier.fillMaxSize().background(Color.Black).padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Buscar canciones", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = accentColor,
                focusedIndicatorColor = accentColor,
                unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            ),
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Color.Gray) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                vm.search(query)
                focus.clearFocus()
            })
        )
        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(results) { track ->
                val isPlaying = currentPlayingTrack?.id == track.id
                ListItem(
                    modifier = Modifier.clickable {
                        playerViewModel.playTrack(
                            track = track,
                            playlist = results,
                            albumId = "search_results",
                            albumImage = track.image,
                            albumName = track.artist_name
                        )
                    },
                    headlineContent = {
                        Text(
                            track.name,
                            color = if (isPlaying) accentColor else Color.White,
                            fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    supportingContent = { Text(track.artist_name, color = Color.Gray) },
                    trailingContent = {
                        IconButton(onClick = {
                            trackToAdd = track
                            showPickPlaylistDialog = true
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Añadir a playlist", tint = Color.Gray)
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                Divider(color = Color.Gray.copy(alpha = 0.3f))
            }
        }
    }

    if (showPickPlaylistDialog && trackToAdd != null) {
        PickPlaylistDialog(
            playlists = localPlaylists,
            onDismiss = {
                showPickPlaylistDialog = false
                trackToAdd = null
            },
            onPlaylistSelected = { selectedPlaylist ->
                trackToAdd?.let { track ->
                    playlistVm.addSong(
                        selectedPlaylist.id,
                        com.example.proyectospotify.playlists.FirebaseSong(
                            id = track.id,
                            name = track.name,
                            artist = track.artist_name,
                            image = track.image,
                            audio = track.audio ?: "",
                            duration = track.duration
                        )
                    )
                }
                showPickPlaylistDialog = false
                trackToAdd = null
            }
        )
    }
}

@Composable
fun PickPlaylistDialog(
    playlists: List<FirebasePlaylist>,
    onDismiss: () -> Unit,
    onPlaylistSelected: (FirebasePlaylist) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir a...", color = Color.White) },
        text = {
            if (playlists.isEmpty()) {
                Text("No tienes playlists. Crea una desde la pestaña 'Playlists'.", color = Color.Gray)
            } else {
                LazyColumn {
                    items(playlists) { playlist ->
                        ListItem(
                            headlineContent = { Text(playlist.name, color = Color.White) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPlaylistSelected(playlist) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFFE53935))
            }
        },
        containerColor = Color(0xFF212121)
    )
}

@Composable
fun AlbumCard(album: JamendoAlbum) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth().clickable {
            val intent = Intent(context, AlbumDetailActivity::class.java).apply {
                putExtra("album_id", album.id)
                putExtra("album_name", album.name)
                putExtra("album_image", album.image)
            }
            context.startActivity(intent)
        }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context).data(album.image).crossfade(true).build(),
            contentDescription = album.name,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            contentScale = ContentScale.Crop
        )
        Text(
            text = album.name,
            color = Color.White,
            maxLines = 1,
            modifier = Modifier.padding(top = 8.dp, start = 4.dp, end = 4.dp)
        )
    }
}


@Composable
fun MiniPlayer(
    track: JamendoTrack,
    isPlaying: Boolean,
    currentPosition: Int,
    duration: Int,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Int) -> Unit,
    onMiniPlayerClick: () -> Unit
) {
    val accentColor = Color(0xFFE53935)
    val trackDurationMs = duration * 1000
    var isUserDragging by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(currentPosition) {
        if (!isUserDragging) {
            sliderValue = if (trackDurationMs > 0) {
                currentPosition.toFloat() / trackDurationMs
            } else {
                0f
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C1C))
            .clickable { onMiniPlayerClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(track.image).crossfade(true).build(),
                contentDescription = track.name,
                modifier = Modifier.size(48.dp)
            )
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(track.name, fontWeight = FontWeight.Bold, maxLines = 1, color = Color.White)
                Text(track.artist_name, fontSize = 12.sp, maxLines = 1, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPrevious) {
                    Icon(imageVector = Icons.Filled.SkipPrevious, contentDescription = "Anterior", tint = Color.White)
                }
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Play/Pausa",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(imageVector = Icons.Filled.SkipNext, contentDescription = "Siguiente", tint = Color.White)
                }
            }
        }

        Slider(
            value = sliderValue,
            onValueChange = { newValue ->
                isUserDragging = true
                sliderValue = newValue
            },
            onValueChangeFinished = {
                val newPosition = (sliderValue * trackDurationMs).toInt()
                onSeek(newPosition)
                isUserDragging = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(horizontal = 4.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color.Transparent,
                activeTrackColor = accentColor,
                inactiveTrackColor = Color.Gray.copy(alpha = 0.5f)
            )
        )
    }
}
