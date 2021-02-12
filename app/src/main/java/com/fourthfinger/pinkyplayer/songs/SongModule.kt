package com.fourthfinger.pinkyplayer.songs

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.migration.DisableInstallInCheck
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SongModule {

    @Provides
    @Singleton
    fun provideSongDB(@ApplicationContext context: Context): SongDatabase {
        return SongDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideSongDao(songDatabase: SongDatabase): SongDao {
        return songDatabase.songDAO()
    }

}