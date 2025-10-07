package com.example.proyectospotify.playlists

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(p: PlaylistEntity): Long

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE playlistId = :id LIMIT 1")
    fun getPlaylist(id: Long): Flow<PlaylistEntity?>

    @Transaction
    @Query("SELECT * FROM playlists WHERE playlistId = :id")
    fun getPlaylistWithSongs(id: Long): Flow<PlaylistWithSongs?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSong(song: SongEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(crossRef: PlaylistSongCrossRef)

    @Query("DELETE FROM playlist_song_crossref WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String)

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)
}