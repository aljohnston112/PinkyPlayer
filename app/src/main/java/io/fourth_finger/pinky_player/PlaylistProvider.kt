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
    private val scope: CoroutineScope,
    musicFileLiveData: LiveData<List<MusicFile>>
) {

    private var playlist: ProbabilityMap<MusicFile>? = null

    private val callbacks = mutableListOf<(ProbabilityMap<MusicFile>) -> Unit>()

    private val musicObserver: (List<MusicFile>?) -> Unit = { newMusic ->
        newMusic?.let {
            scope.launch(Dispatchers.Default) {
                if (playlist == null) {
                    playlist = ProbabilityMap(newMusic)
                    for (callback in callbacks) {
                        withContext(Dispatchers.Main) {
                            callback(playlist!!)
                        }
                    }
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
            }
        }
    }


    init {
        musicFileLiveData.observeForever(musicObserver)
    }

    fun invokeOnLoad(
        callback: (ProbabilityMap<MusicFile>) -> Unit
    ) {
        if (playlist != null) {
            scope.launch(Dispatchers.Main) {
                callback(playlist!!)
            }
        } else {
            callbacks.add(callback)
        }
    }

    fun getOrNull(): ProbabilityMap<MusicFile>? {
        return playlist
    }

}