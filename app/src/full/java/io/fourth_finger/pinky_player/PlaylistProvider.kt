package io.fourth_finger.pinky_player

import io.fourth_finger.music_repository.MusicItem
import io.fourth_finger.music_repository.MusicRepository
import io.fourth_finger.probability_map.ProbabilityMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistProvider @Inject constructor(
    scope: CoroutineScope,
    musicRepository: MusicRepository
) {

    private lateinit var mainPlaylist: ProbabilityMap<MusicItem>

    private val musicLoadedLatch = CountDownLatch(1)

    private val musicObserver: (List<MusicItem>) -> Unit = { newMusic ->
        if (newMusic.isNotEmpty()) {
            scope.launch(Dispatchers.Default) {
                if (musicLoadedLatch.count > 0) {
                    mainPlaylist = ProbabilityMap(newMusic)
                } else {
                    // Add new songs to the playlist
                    for (newSong in newMusic) {
                        if (!mainPlaylist.contains(newSong)) {
                            mainPlaylist.addElement(newSong)
                        }
                    }

                    // Remove missing songs from the playlist
                    for (oldSong in mainPlaylist.getElements().toList()) {
                        if (!newMusic.contains(oldSong)) {
                            mainPlaylist.removeElement(oldSong)
                        }
                    }
                }
                musicLoadedLatch.countDown()
            }
        }
    }

    init {
        musicRepository.musicItems.observeForever(musicObserver)
    }

    suspend fun await(): ProbabilityMap<MusicItem> {
        withContext(Dispatchers.IO) {
            musicLoadedLatch.await()
        }
        return mainPlaylist
    }

    fun getOrNull(): ProbabilityMap<MusicItem>? {
        return mainPlaylist
    }

}