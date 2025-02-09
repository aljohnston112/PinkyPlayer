package io.fourth_finger.pinky_player.hilt

import android.content.ContentResolver
import android.net.Uri
import io.fourth_finger.music_repository.MusicDataSource
import io.fourth_finger.music_repository.MusicDataSourceImpl
import io.fourth_finger.music_repository.MusicItem
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.pinky_player.MediaFileUtil.Companion.getMusicIdOfTwoShortDurationSongs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


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
                    getMusicIdOfTwoShortDurationSongs(musicRepository).map {
                        musicRepository.getMusicItem(it)!!
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
