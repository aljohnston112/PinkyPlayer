package com.fourthfinger.pinkyplayer.songs

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Insert
    suspend fun insertAll(vararg songs: Song)

    @Query("DELETE FROM songs WHERE id = :song_id")
    suspend fun deleteSong(song_id: Long)

    @Query("SELECT * FROM songs WHERE id = :song_id")
    suspend fun getSong(song_id: Long): Song

    @Query("SELECT * FROM songs")
    fun getAll(): Flow<List<Song>>

    @Query("DELETE FROM songs")
    suspend fun deleteAll()

}