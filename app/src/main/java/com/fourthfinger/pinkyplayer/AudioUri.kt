package com.fourthfinger.pinkyplayer

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.fourthfinger.pinkyplayer.songs.Song
import com.fourthfinger.pinkyplayer.songs.SongDao
import java.io.*
import java.util.*

@Transient
const val TAG = "AudioURI"

class AudioUri(
        val displayName: String,
        val artist: String,
        val title: String,
        val id: Long,
) : Comparable<AudioUri>, Serializable {

    private val nestProbMap: NestedProbMap = NestedProbMap()

    val uri: Uri by lazy {
        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

    val duration: Int by lazy {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(null, uri)
        var time = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        mediaMetadataRetriever.release()
        if (time == null) {
            time = "00:00:00"
        }
        time.toInt()
    }

    fun shouldPlay(random: Random): Boolean {
        return nestProbMap.outcome(random)
    }

    fun good(percent: Double): Boolean {
        return nestProbMap.good(percent)
    }

    fun bad(percent: Double): Boolean {
        return nestProbMap.bad(percent)
    }

    fun resetProbabilities() {
        nestProbMap.resetProbabilities()
    }

    override operator fun compareTo(other: AudioUri): Int {
        return title.compareTo(other.title)
    }

    override fun equals(other: Any?): Boolean {
        return other is AudioUri && uri == other.uri
    }

    override fun hashCode(): Int {
        return uri.hashCode()
    }

    companion object {

        fun saveAudioUri(context: Context, audioURI: AudioUri) : Boolean {
            val file = File(context.filesDir, audioURI.id.toString())
            if (!file.exists()) {
                try {
                    context.openFileOutput(audioURI.id.toString(), Context.MODE_PRIVATE).use {
                        fos -> ObjectOutputStream(fos).use {
                        objectOutputStream -> objectOutputStream.writeObject(audioURI) } }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return false
            }
            return true;
        }

        fun getAudioUri(context: Context, songID: Long): AudioUri? {
            val file = File(context.filesDir, songID.toString())
            if (file.exists()) {
                try {
                    context.openFileInput(songID.toString()).use {
                        fileInputStream -> ObjectInputStream(fileInputStream).use {
                        objectInputStream ->
                        return objectInputStream.readObject() as AudioUri } }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            return null
        }

        fun getUri(songID: Long): Uri {
            return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songID)
        }

    }
}