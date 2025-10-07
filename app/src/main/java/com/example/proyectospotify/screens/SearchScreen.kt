package com.example.proyectospotify.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.proyectospotify.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: HomeViewModel) {
    var query by remember { mutableStateOf("") }
    val results by viewModel.searchResults.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        TextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.searchTracks(it)
            },
            placeholder = { Text("Buscar canciones o artistas", color = Color.Gray) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.DarkGray,
                unfocusedContainerColor = Color.DarkGray,
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (results.isEmpty() && query.isNotBlank()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Green)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(results) { track ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        AsyncImage(
                            model = track.image,
                            contentDescription = track.name,
                            modifier = Modifier.size(64.dp)
                        )
                        Column(Modifier.padding(start = 8.dp)) {
                            Text(track.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(track.artist_name, color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}