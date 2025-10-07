package com.example.proyectospotify.playlists

import android.content.Context
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(context: Context) {
    private val dao = AppDatabase.get(context).playlistDao()

    fun playlists(): Flow<List<PlaylistEntity>> = dao.getPlaylists()
    fun playlist(id: Long): Flow<PlaylistEntity?> = dao.getPlaylist(id)
    fun playlistWithSongs(id: Long): Flow<PlaylistWithSongs?> = dao.getPlaylistWithSongs(id)

    suspend fun createPlaylist(name: String): Long {
        return dao.insertPlaylist(PlaylistEntity(name = name.trim()))
    }

    suspend fun addSongToPlaylist(playlistId: Long, song: SongEntity) {
        dao.upsertSong(song)
        dao.addSongToPlaylist(PlaylistSongCrossRef(playlistId, song.songId))
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String) {
        dao.removeSongFromPlaylist(playlistId, songId)
    }

    suspend fun deletePlaylist(playlistId: Long) {
        dao.deletePlaylist(playlistId)
    }
}