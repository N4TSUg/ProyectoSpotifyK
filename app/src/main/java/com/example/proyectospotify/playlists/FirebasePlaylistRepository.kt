package com.example.proyectospotify.playlists

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebasePlaylistRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getUserPlaylists(userId: String): Flow<List<FirebasePlaylist>> = callbackFlow {
        val listener = db.collection("playlists")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                val playlists = snapshot?.toObjects(FirebasePlaylist::class.java) ?: emptyList()
                trySend(playlists)
            }
        awaitClose { listener.remove() }
    }

    suspend fun createPlaylist(playlist: FirebasePlaylist) {
        // Crear documento con ID automático
        val docRef = db.collection("playlists").add(playlist).await()

        // Actualizar el campo "id" dentro del documento
        db.collection("playlists").document(docRef.id)
            .update("id", docRef.id)
            .await()
    }

    suspend fun addSongToPlaylist(playlistId: String, song: FirebaseSong) {
        if (playlistId.isBlank()) {
            Log.e("Firestore", "Playlist ID vacío o nulo.")
            return
        }

        val playlistRef = db.collection("playlists").document(playlistId)

        // Obtener snapshot actual
        val snapshot = playlistRef.get().await()
        val playlist = snapshot.toObject(FirebasePlaylist::class.java)

        playlist?.let {
            // Crear nueva lista manualmente (en lugar de arrayUnion)
            val updatedSongs = it.songs.toMutableList().apply { add(song) }
            playlistRef.update("songs", updatedSongs).await()
            Log.d("Firestore", "Canción añadida correctamente y lista actualizada.")
        } ?: Log.e("Firestore", "Playlist no encontrada con ID: $playlistId")
    }


    suspend fun removeSongFromPlaylist(playlistId: String, songId: String) {
        val docRef = db.collection("playlists").document(playlistId)
        val snapshot = docRef.get().await()
        val playlist = snapshot.toObject(FirebasePlaylist::class.java)

        playlist?.let {
            val currentSongs = it.songs ?: emptyList()
            val updatedSongs = currentSongs.filterNot { s -> s.id == songId }
            docRef.update("songs", updatedSongs).await()
        }
    }

    suspend fun deletePlaylist(playlistId: String) {
        if (playlistId.isBlank()) return

        try {
            db.collection("playlists").document(playlistId).delete().await()
            Log.d("Firestore", "Playlist eliminada correctamente.")
        } catch (e: Exception) {
            Log.e("Firestore", "Error al eliminar la playlist: ${e.message}", e)
        }
    }
    }
