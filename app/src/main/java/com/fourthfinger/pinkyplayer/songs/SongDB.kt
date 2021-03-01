package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Song::class], version = 1)
abstract class SongDB : RoomDatabase() {

    abstract fun songDao(): SongDao

}