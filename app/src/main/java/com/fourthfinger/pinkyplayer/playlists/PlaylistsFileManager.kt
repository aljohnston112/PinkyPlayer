package com.fourthfinger.pinkyplayer.playlists

import android.content.Context
import com.fourthfinger.pinkyplayer.settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import javax.inject.Inject

class PlaylistsFileManager @Inject constructor() {

    private val lock: Any = Any()

    private fun attemptLoadPlaylist(
            context: Context,
            playlistName: String,
            saveFileVerificationNumber: Long): RandomPlaylist? {
        var randomPlaylist: RandomPlaylist? = null
            var longEOF = 0L
            val file = File(context.filesDir, playlistName)
            if (file.exists()) {
                try {
                    context.openFileInput(playlistName).use { fileInputStream ->
                        ObjectInputStream(fileInputStream).use { objectInputStream ->
                            randomPlaylist = objectInputStream.readObject() as RandomPlaylist
                            longEOF = objectInputStream.readLong()
                        }
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            if (longEOF != saveFileVerificationNumber) {
                return null
            }
        return randomPlaylist
    }

    suspend fun load(
            context: Context,
            playlistFileNames: List<String>,
            saveFileVerificationNumber: Long
    ): RandomPlaylist? {
        return (withContext(Dispatchers.IO) {
            var randomPlaylist: RandomPlaylist? = null
            synchronized(lock) {
                var i = 0
                while (i < playlistFileNames.size) {
                    randomPlaylist = attemptLoadPlaylist(context, playlistFileNames[i], saveFileVerificationNumber)
                    i++
                    if (randomPlaylist != null) {
                        break
                    }
                }
            }
            return@withContext randomPlaylist
        })
    }

    fun save(
            randomPlaylist: RandomPlaylist,
            context: Context,
            playlistFileNames: List<String>,
            saveFileVerificationNumber: Long,
    ) {
        synchronized(lock) {
            var file = File(context.filesDir, playlistFileNames[playlistFileNames.size - 1])
            if (file.exists()) {
                file.delete()
            }
            for (i in ((playlistFileNames.size - 2) downTo (0))) {
                val file2 = File(context.filesDir, playlistFileNames[i])
                file2.renameTo(file)
                file = File(context.filesDir, playlistFileNames[i])
            }
            context.openFileOutput(playlistFileNames[0], Context.MODE_PRIVATE).use { fileOutputStream ->
                ObjectOutputStream(fileOutputStream).use { objectOutputStream ->
                    objectOutputStream.writeObject(randomPlaylist)
                    objectOutputStream.writeLong(saveFileVerificationNumber)
                }
            }
        }
    }

}