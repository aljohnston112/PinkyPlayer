package io.fourth_finger.pinky_player

import android.media.MediaMetadataRetriever
import androidx.test.platform.app.InstrumentationRegistry
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope

class MediaFileUtil {

    companion object {

        suspend fun getMusicIdOfTwoShortDurationSongs(
            musicRepository: MusicRepository,
            doNotConsider: List<Long> = listOf()
        ): List<Long> {
            val musicIds = mutableListOf<Long>()
            val id = getMusicIdOfSongWithDurationUnder(musicRepository)
            musicIds.add(id)
            val id2 = getMusicIdOfSongWithDurationUnder(musicRepository, musicIds + doNotConsider)
            musicIds.add(id2)
            return musicIds
        }

        suspend fun getMusicIdOfSongWithDurationUnder(
            musicRepository: MusicRepository,
            doNotConsider: List<Long> = listOf(),
            durationMS: Int = 100
        ): Long = coroutineScope {
            val music = musicRepository.getCachedMusicItems()
            val context = InstrumentationRegistry.getInstrumentation().targetContext

            val deferredResults = music.mapNotNull { musicFile ->
                if (musicFile.id !in doNotConsider) {
                    async {
                        val mmr = MediaMetadataRetriever()
                        return@async try {
                            mmr.setDataSource(context, musicRepository.getUri(musicFile.id))
                            val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            val duration = durationStr?.toInt()
                            if (duration != null && duration < durationMS) {
                                musicFile.id
                            } else null
                        } finally {
                            mmr.release()
                        }
                    }
                } else{
                    null
                }
            }

            try {
                deferredResults.firstNotNullOfOrNull { it.await() }?.also {
                    deferredResults.forEach { it.cancel() }
                } ?: music.first().id
            } finally {
                coroutineContext.cancelChildren()
            }
        }

    }

}