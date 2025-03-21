package io.fourth_finger.event_processor

import android.content.Context
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import io.fourth_finger.music_repository.MusicRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaItemCreator @Inject constructor(
    private val musicRepository: MusicRepository,
) {

    private val metaDataCreator = MetaDataCreator(musicRepository)

    /**
     * Creates and returns a [androidx.media3.common.MediaItem] for the music with the given id.
     * Metadata is included in the [androidx.media3.common.MediaItem].
     *
     * @param context
     * @param id The music file's [MediaStore.Audio.Media] id.
     * @return The constructed [androidx.media3.common.MediaItem].
     */
    fun getMediaItem(
        context: Context,
        id: Long
    ): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id.toString())
            .setUri(musicRepository.getUri(id))
            .setMediaMetadata(
                metaDataCreator.getMetaData(
                    context,
                    id
                )
            )
            .build()
    }

}