package com.fourthfinger.pinkyplayer.songs

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg songs: Song)

    @Deprecated("Different LiveData object is returned every time and can lead to Observer leaks")
    @Query("SELECT * FROM songs")
    fun getAll(): LiveData<List<Song>>

    @Query("SELECT * FROM songs WHERE id = :song_id")
    suspend fun getSong(song_id: Long): Song?

    @Query("DELETE FROM songs WHERE id = :song_id")
    suspend fun deleteSong(song_id: Long)

    @Query("DELETE FROM songs")
    suspend fun deleteAll()

}