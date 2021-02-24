package com.fourthfinger.pinkyplayer.songs

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SongDao {

    @Insert
    suspend fun insertAll(vararg songs: Song)

    @Query("SELECT * FROM songs WHERE id = :song_id")
    suspend fun getSong(song_id: Long): Song?

    @Query("SELECT * FROM songs")
    fun getAll(): LiveData<List<Song>>

    @Query("DELETE FROM songs WHERE id = :song_id")
    suspend fun deleteSong(song_id: Long)

    @Query("DELETE FROM songs")
    suspend fun deleteAll()

}