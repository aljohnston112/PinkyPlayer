package com.fourthfinger.pinkyplayer.songs

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import java.io.*
import java.util.*
import kotlin.random.Random

class AudioUri(
        val displayName: String,
        val artist: String,
        val title: String,
        val id: Long,
) : Comparable<AudioUri>, Serializable {

    private val nestProbMap: NestedProbMap = NestedProbMap()

    fun uri(): Uri = getUri(id)

    private var duration: Long = -1L

    fun getDuration(context: Context): Long {
        if(duration == -1L) {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, uri())
            var time = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            mediaMetadataRetriever.release()
            if (time == null) {
                time = "-1"
            }
            duration = time.toLong()
        }
        return duration
    }

    fun shouldPlay(random: Random): Boolean {
        return nestProbMap.outcome(random, Calendar.getInstance())
    }

    fun good(percent: Double): Boolean {
        return nestProbMap.good(percent, Calendar.getInstance())
    }

    fun bad(percent: Double): Boolean {
        return nestProbMap.bad(percent, Calendar.getInstance())
    }

    fun resetProbabilities() {
        nestProbMap.resetProbabilities()
    }

    override operator fun compareTo(other: AudioUri): Int {
        return uri().compareTo(other.uri())
    }

    override fun equals(other: Any?): Boolean {
        return other is AudioUri && uri() == other.uri()
    }

    override fun hashCode(): Int {
        return uri().hashCode()
    }

    companion object {

        fun audioUriExists(context: Context, songID: Long): Boolean {
            val file = File(context.filesDir, songID.toString())
            return file.exists()
        }

        fun saveAudioUri(context: Context, audioURI: AudioUri) : Boolean {
            val file = File(context.filesDir, audioURI.id.toString())
            if (!file.exists()) {
                try {
                    context.openFileOutput(audioURI.id.toString(), Context.MODE_PRIVATE).use {
                        fos -> ObjectOutputStream(fos).use {
                        objectOutputStream -> objectOutputStream.writeObject(audioURI) } }
                    return true
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return false
        }

        fun deleteAudioUri(context: Context, songID : Long) : Boolean {
            val file = File(context.filesDir, songID.toString())
            if (file.exists()) {
                return file.delete()
            }
            return false
        }

        fun getAudioUri(context: Context, songID: Long): AudioUri? {
            val file = File(context.filesDir, songID.toString())
            if (file.exists()) {
                try {
                    context.openFileInput(songID.toString()).use {
                        fileInputStream -> ObjectInputStream(fileInputStream).use {
                        objectInputStream ->
                        return objectInputStream.readObject() as AudioUri
                    }
                    }
                }catch (e: InvalidClassException){
                    e.printStackTrace()
                }
                catch (e: FileNotFoundException) {
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