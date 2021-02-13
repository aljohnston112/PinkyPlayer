package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Song::class], version = 1)
abstract class SongDB : RoomDatabase() {

    abstract fun songDao(): SongDao

    companion object {

        @Volatile
        private var INSTANCE: SongDB? = null

        fun getDatabase(context: Context): SongDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        SongDB::class.java,
                        "song_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }

}