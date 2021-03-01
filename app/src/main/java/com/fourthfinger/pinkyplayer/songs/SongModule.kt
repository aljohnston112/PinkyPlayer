package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SongModule {

    @Provides
    @Singleton
    fun provideSongDB(@ApplicationContext context: Context): SongDB {
        return Room.databaseBuilder(
                context.applicationContext,
                SongDB::class.java,
                "song_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSongDao(songDB: SongDB): SongDao {
        return songDB.songDao()
    }

}