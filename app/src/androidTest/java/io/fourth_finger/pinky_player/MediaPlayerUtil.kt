package io.fourth_finger.pinky_player

import android.media.MediaMetadataRetriever
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import io.fourth_finger.music_repository.MusicFile

class MediaPlayerUtil {

    companion object {

        fun get_music_id_of_shortest_song(music: List<MusicFile>): Long {
            val application = ApplicationProvider.getApplicationContext<MainApplication>()
            val musicRepository = application.musicRepository
            var shortestMusic = music[0].id

            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(context, musicRepository.getUri(music[0].id))
            var durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            var shortestDuration = durationStr!!.toInt()

            for (m in music) {
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
            return shortestMusic
        }

    }

}