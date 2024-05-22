package io.fourth_finger.pinky_player

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaMetadata
import io.fourth_finger.music_repository.MusicRepository
import javax.inject.Inject

/**
 * Wrapper for a [MediaMetadata.Builder].
 *
 * @param musicRepository
 */
class MetaDataCreator @Inject constructor(
    private val musicRepository: MusicRepository
) {

    private val metaDataBuilder = MediaMetadata.Builder()

    /**
     * Creates a [MediaMetadata] with the title and artwork
     * associated with a music file and returns it.
     *
     * @param context
     * @param mediaId The id of the music file to get the metadata from.
     * @return The [MediaMetadata] of the music file with the given mediaId.
     */
    fun getMetaData(
        context: Context,
        mediaId: Long
    ): MediaMetadata {

        // Music title
        val musicFile = musicRepository.getMusicFile(mediaId)
        if (musicFile != null) {
            metaDataBuilder.setTitle(musicFile.relativePath + musicFile.displayName)
        }

        // Bitmap
        musicRepository.getUri(mediaId)?.let { uri ->
            metaDataBuilder.setArtworkUri(uri)
            uri
        } ?: {
            val resources = context.resources
            val resourceId = R.drawable.ic_baseline_music_note_24
            val drawableUri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resourceId))
                .appendPath(resources.getResourceTypeName(resourceId))
                .appendPath(resources.getResourceEntryName(resourceId))
                .build()
            metaDataBuilder.setArtworkUri(drawableUri)
        }

        return metaDataBuilder.build()
    }

}