package com.fourthfinger.pinkyplayer.settings

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import javax.inject.Inject

private const val MAX_PERCENT = 0.1
private const val PERCENT_CHANGE_UP = 0.5
private const val PERCENT_CHANGE_DOWN = 0.9
private const val LOWER_PROB = 0.0

class SettingsFileManager @Inject constructor() {

    private fun attemptLoadFile(
            context: Context,
            fileSave: String,
            saveFileVerificationNumber: Long): Settings? {
        var longEOF = 0L
        val file = File(context.filesDir, fileSave)
        var settings: Settings? = null
        if (file.exists()) {
            try {
                context.openFileInput(fileSave).use { fileInputStream ->
                    ObjectInputStream(fileInputStream).use { objectInputStream ->
                        settings = objectInputStream.readObject() as Settings
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
        return settings
    }

    suspend fun load(
            context: Context,
            fileNames: List<String>,
            saveFileVerificationNumber: Long
    ): Settings {
        return (withContext(Dispatchers.IO) {
            var settings: Settings? = null
            var i = 0
            while (i < fileNames.size) {
                settings = attemptLoadFile(context, fileNames[i], saveFileVerificationNumber)
                i++
                if (settings != null) {
                    break
                }
            }
            if (settings == null) {
                settings = Settings(MAX_PERCENT, PERCENT_CHANGE_UP, PERCENT_CHANGE_DOWN, LOWER_PROB)
            }
            return@withContext settings
        })
    }

    fun save(
            settings: Settings,
            context: Context,
            fileNames: List<String>,
            saveFileVerificationNumber: Long,
    ) {
        var file = File(context.filesDir, fileNames[fileNames.size - 1])
        if (file.exists()) {
            file.delete()
        }
        for (i in ((fileNames.size - 2) downTo (0))) {
            val file2 = File(context.filesDir, fileNames[i])
            file2.renameTo(file)
            file = File(context.filesDir, fileNames[i])
        }
        context.openFileOutput(fileNames[0], Context.MODE_PRIVATE).use { fileOutputStream ->
            ObjectOutputStream(fileOutputStream).use { objectOutputStream ->
                objectOutputStream.writeObject(settings)
                objectOutputStream.writeLong(saveFileVerificationNumber)
            }
        }
    }

}