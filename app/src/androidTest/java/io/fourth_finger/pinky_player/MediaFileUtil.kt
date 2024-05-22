package io.fourth_finger.pinky_player

import android.media.MediaMetadataRetriever
import androidx.test.platform.app.InstrumentationRegistry
import io.fourth_finger.music_repository.MusicRepository

class MediaFileUtil {

    companion object {

        suspend fun getMusicIdOfTwoShortDurationSongs(
            musicRepository: MusicRepository,
            doNotConsider: List<Long> = listOf()
        ): List<Long> {
            val musicIds = mutableListOf<Long>()
            val id = getMusicIdOfShortDurationSong(musicRepository)
            musicIds.add(id)
            val id2 = getMusicIdOfShortDurationSong(musicRepository, musicIds + doNotConsider)
            musicIds.add(id2)
            return musicIds
        }

        suspend fun getMusicIdOfShortDurationSong(
            musicRepository: MusicRepository,
            doNotConsider: List<Long> = listOf()
        ): Long {
            val music = musicRepository.getCachedMusicFiles()
            var shortestMusic = music[0].id

            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(context, musicRepository.getUri(music[0].id))
            var durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            var shortestDuration = durationStr!!.toInt()

            for (m in music) {
                if(m.id !in doNotConsider) {
                    mmr.setDataSource(context, musicRepository.getUri(m.id))
                    durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

                    val duration = durationStr!!.toInt()
                    if (duration < shortestDuration) {
                        shortestDuration = duration
                        shortestMusic = m.id
                    }
                    if (duration < 10000) {
                        break
                    }
                }
            }
            return shortestMusic
        }

    }

}