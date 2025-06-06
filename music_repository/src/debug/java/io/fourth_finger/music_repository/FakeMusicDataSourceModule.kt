package io.fourth_finger.music_repository

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext


fun provideFakeMusicDataSourceWithTwoShortestSongs(): MusicDataSource {
    return object : MusicDataSource() {

        private val musicRepository = MusicRepository(MusicDataSourceImpl())
        private val shortestMusic = mutableListOf<MusicItem>()
        private val mutex = Mutex()

        override suspend fun loadMusicFiles(
            contentResolver: ContentResolver,
            refresh: Boolean,
            dispatcher: CoroutineDispatcher
        ): List<MusicItem> {
            return mutex.withLock {
                musicRepository.loadMusicFiles(
                    contentResolver,
                    refresh,
                    dispatcher
                )
                shortestMusic.clear()
                shortestMusic.addAll(
                    withContext(Dispatchers.Default) {
                        MediaFileUtil.Companion.getMusicIdOfTwoShortDurationSongs(musicRepository)
                            .map {
                                musicRepository.getMusicItem(it)!!
                            }
                    }
                )
                shortestMusic.toList()
            }
        }

        override suspend fun getCachedMusicItems(): List<MusicItem> {
            return shortestMusic
        }

        override fun getUri(id: Long): Uri? {
            return musicRepository.getUri(id)
        }

        override fun getMusicItem(id: Long): MusicItem? {
            return musicRepository.getMusicItem(id)
        }

    }

}

fun provideFakeMusicDataSourceWithNoSongs(): MusicDataSource {
    return object : MusicDataSource() {

        private val music = mutableListOf<MusicItem>()

        override suspend fun loadMusicFiles(
            contentResolver: ContentResolver,
            refresh: Boolean,
            dispatcher: CoroutineDispatcher
        ): List<MusicItem> {
            return music
        }

        override suspend fun getCachedMusicItems(): List<MusicItem> {
            return music
        }

        override fun getUri(id: Long): Uri? {
            return null
        }

        override fun getMusicItem(id: Long): MusicItem? {
            return null
        }

    }

}
