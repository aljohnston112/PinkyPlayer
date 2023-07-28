package io.fourth_finger.pinky_player

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import io.fourth_finger.music_repository.MusicRepository

class MetaDataHelper(private val musicRepository: MusicRepository) {

    private val metaDataBuilder = MediaMetadataCompat.Builder()

    fun updateMetaData(context: Context, mediaId: String, mediaSession: MediaSessionCompat?) {
        // Music title
        val musicFile = musicRepository.getMusicFile(mediaId.toLong())
        if (musicFile != null) {
            metaDataBuilder.putString(
                MediaMetadataCompat.METADATA_KEY_TITLE,
                musicFile.relativePath + musicFile.displayName
            )
        }

        // Bitmap
        val inputStream = musicRepository.getUri(mediaId.toLong())?.let { uri ->
            context.contentResolver.openInputStream(uri)
        }
        metaDataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,
            BitmapFactory.decodeStream(inputStream)
        )
        inputStream?.close()

        mediaSession?.setMetadata(metaDataBuilder.build())
    }


}