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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.proyectospotify.entities.JamendoAlbum
import com.example.proyectospotify.ui.theme.ProyectoSpotifyTheme
import com.example.proyectospotify.viewmodels.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : ComponentActivity() {

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
                                icon = { Icon(Icons.Filled.LibraryMusic, contentDescription = "Biblioteca") },
                                label = { Text("Tu Biblioteca") }
                            )
                        }
                    },
                    containerColor = Color.Black
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (selectedIndex) {
                            0 -> HomeContent(albumsState)
//                            1 -> PlaceholderScreen("Buscar")
//                            2 -> PlaceholderScreen("Tu Biblioteca")
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
            .clickable { /* Navegación a detalle del álbum */ }
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
    }
}