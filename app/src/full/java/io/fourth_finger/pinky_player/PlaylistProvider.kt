package io.fourth_finger.pinky_player

import androidx.lifecycle.LiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.probability_map.ProbabilityMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicFileLiveDataModule {

    @Provides
    fun provideMusicFileLiveData(
        musicRepository: MusicRepository
    ): LiveData<List<MusicFile>> {
        return musicRepository.musicFiles
    }

}

@Singleton
class PlaylistProvider @Inject constructor(
    scope: CoroutineScope,
    musicFiles: LiveData<List<MusicFile>>
) {

    private var playlist: ProbabilityMap<MusicFile>? = null

    private val musicLoadedLatch = CountDownLatch(1)

    private val musicObserver: (List<MusicFile>?) -> Unit = { newMusic ->
        newMusic?.let {
            if(newMusic.isNotEmpty()) {
                scope.launch(Dispatchers.Default) {
                    if (playlist == null) {
                        playlist = ProbabilityMap(newMusic)
                    } else {
                        playlist?.let {
                            // Add new songs to the playlist
                            for (newSong in newMusic) {
                                if (!it.contains(newSong)) {
                                    it.addElement(newSong)
                                }
                            }

                            // Remove missing songs from the playlist
                            for (oldSong in it.getElements().toList()) {
                                if (!newMusic.contains(oldSong)) {
                                    it.removeElement(oldSong)
                                }
                            }
                        }
                    }
                    musicLoadedLatch.countDown()
                }
            }
        }
    }


    init {
        musicFiles.observeForever(musicObserver)
    }

    suspend fun await(): ProbabilityMap<MusicFile> {
        withContext(Dispatchers.IO) {
            musicLoadedLatch.await()
        }
        return playlist!!
    }

    fun getOrNull(): ProbabilityMap<MusicFile>? {
        return playlist
    }

}