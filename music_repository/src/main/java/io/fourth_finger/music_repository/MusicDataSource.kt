package io.fourth_finger.music_repository

import android.content.ContentResolver
import android.net.Uri
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object MusicDataSourceModule {

    @Provides
    fun provideMusicDataSource(): MusicDataSource {
        return MusicDataSourceImpl()
    }

}

abstract class MusicDataSource {

    abstract suspend fun loadMusicFiles(
        contentResolver: ContentResolver,
        refresh: Boolean = false,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): List<MusicItem>

    abstract suspend fun getCachedMusicItems(): List<MusicItem>
    abstract fun getUri(id: Long): Uri?
    abstract fun getMusicItem(id: Long): MusicItem?

}