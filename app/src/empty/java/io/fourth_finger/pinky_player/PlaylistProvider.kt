package io.fourth_finger.pinky_player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.fourth_finger.music_repository.MusicItem
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
object MusicItemLiveDataModule {

    @Provides
    fun provideMusicItemLiveData(): LiveData<List<MusicItem>> {
        return MutableLiveData(emptyList())
    }

}

@Singleton
class PlaylistProvider @Inject constructor(
    scope: CoroutineScope,
    MusicItems: LiveData<List<MusicItem>>
) {

    private var playlist: ProbabilityMap<MusicItem>? = null

    private val musicLoadedLatch = CountDownLatch(1)

    private val musicObserver: (List<MusicItem>?) -> Unit = { newMusic ->
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
        MusicItems.observeForever(musicObserver)
    }

    suspend fun await(): ProbabilityMap<MusicItem> {
        withContext(Dispatchers.IO) {
            musicLoadedLatch.await()
        }
        return playlist!!
    }

    fun getOrNull(): ProbabilityMap<MusicItem>? {
        return playlist
    }

}