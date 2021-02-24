package com.fourthfinger.pinkyplayer

import android.content.Context
import java.io.*

private const val N_BACKUPS = 2

private val lock: Any = Any()

class FileUtil {

    companion object {

        /**
         * Returns a [List] of strings appending a number 0 through [N_BACKUPS] to [fileName]
         */
        private fun getFileNames(fileName: String): List<String> {
            val list = mutableListOf<String>()
            for(i in 0..N_BACKUPS){
                list.add(fileName+i.toString())
            }
            return list
        }

        /**
         * Tries to save a file with the given [fileName].
         * The file saved may not have that name, but can be used with [load].
         * [saveFileVerificationNumber] is used for verifying the file during [load].
         */
        fun <T> save(
                t: T,
                context: Context,
                fileName: String,
                saveFileVerificationNumber: Long,
        ) {
            val fileNames = getFileNames(fileName)
            synchronized(lock) {
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
                        objectOutputStream.writeObject(t)
                        objectOutputStream.writeLong(saveFileVerificationNumber)
                    }
                }
            }
        }

        /**
         * Tries to load [fileSave].
         * [saveFileVerificationNumber] is the number passed to [save]
         * and is used to verify the file.
        */
        private fun <T> attemptLoadFile(
                context: Context,
                fileSave: String,
                saveFileVerificationNumber: Long
        ): T? {
            var longEOF = 0L
            var t: T? = null
            synchronized(lock) {
                val file = File(context.filesDir, fileSave)
                if (file.exists()) {
                    try {
                        context.openFileInput(fileSave).use { fileInputStream ->
                            ObjectInputStream(fileInputStream).use { objectInputStream ->
                                @Suppress("UNCHECKED_CAST")
                                t = objectInputStream.readObject() as T
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
            }
            if (longEOF != saveFileVerificationNumber) {
                return null
            }
            return t
        }

        /**
         * Tries to load [fileName].
         * [saveFileVerificationNumber] is the number passed to [save]
         * and is used to verify the file.
         */
        fun <T> load(
                context: Context,
                fileName: String,
                saveFileVerificationNumber: Long
        ): T? {
            val fileNames = getFileNames(fileName)
            var t: T? = null
                var i = 0
                while (i < fileNames.size) {
                    t = attemptLoadFile(context, fileNames[i], saveFileVerificationNumber)
                    i++
                    if (t != null) {
                        break
                    }
                }
                return t
            }

    }

}