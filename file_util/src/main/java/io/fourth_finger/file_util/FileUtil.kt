package io.fourth_finger.file_util

import android.content.Context
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

private const val N_BACKUPS = 2

class FileUtil {

    companion object {

        private val fileLock = Any()

        /**
         * Returns a [List] of strings appending a number 0 through [N_BACKUPS] to [fileName].
         * Ex: A file name of "a" wil result in "a0", "a1", ..., "a[N_BACKUPS - 1]", "a[N_BACKUPS]"
         *
         * @param fileName The base name of the files.
         * @return A list of files with the base name and a number appended.
         */
        private fun getFileNames(fileName: String): List<String> {
            val list = mutableListOf<String>()
            for (i in 0..N_BACKUPS) {
                list.add(fileName + i.toString())
            }
            return list
        }

        /**
         * Rotates the backup files.
         * The last file in the list is deleted, and then
         * every file left is renamed to the file after it in the given list.
         *
         * @param context
         * @param fileNames The names of all the backup files.
         */
        private fun rotateBackupFiles(
            context: Context,
            fileNames: List<String>
        ) {
            val directory = context.filesDir

            // Delete the oldest file
            var file = File(
                directory,
                fileNames[fileNames.size - 1]
            )
            if (file.exists()) {
                file.delete()
            }

            // Rotate via renaming
            for (i in ((fileNames.size - 2) downTo (0))) {
                val file2 = File(
                    directory,
                    fileNames[i]
                )
                file2.renameTo(file)
                file = File(
                    directory,
                    fileNames[i]
                )
            }
        }

        /**
         * Tries to save an object to a file.
         *
         * @param toSave The object to save.
         * @param context
         * @param fileName The base file name.
         *                 The file saved may not have that name, but it can be used with [load].
         * @param saveFileVerificationNumber Used to verify the file during [load].
         */
        fun <T : Serializable> save(
            toSave: T,
            context: Context,
            fileName: String,
            saveFileVerificationNumber: Long
        ) {
            val fileNames = getFileNames(fileName)
            synchronized(fileLock) {
                rotateBackupFiles(
                    context,
                    fileNames
                )
                context.openFileOutput(
                    fileNames[0],
                    Context.MODE_PRIVATE
                ).use { fileOutputStream ->
                    ObjectOutputStream(fileOutputStream).use { objectOutputStream ->
                        objectOutputStream.writeObject(toSave)
                        objectOutputStream.writeLong(saveFileVerificationNumber)
                    }
                }
            }
        }

        /**
         * Tries to save a list of objects to a file.
         *
         * @param objectList The list of objects to save.
         * @param context
         * @param fileName The base file name.
         *                 The file saved may not have that name, but it can be used with [load].
         * @param saveFileVerificationNumber Used to verify the file during [load].
         */
        fun <T : Serializable> saveList(
            objectList: List<T>,
            context: Context,
            fileName: String,
            saveFileVerificationNumber: Long,
        ) {
            val fileNames = getFileNames(fileName)
            synchronized(fileLock) {
                rotateBackupFiles(
                    context,
                    fileNames
                )
                context.openFileOutput(
                    fileNames[0],
                    Context.MODE_PRIVATE
                ).use { fileOutputStream ->
                    ObjectOutputStream(fileOutputStream).use { objectOutputStream ->
                        objectOutputStream.writeInt(objectList.size)
                        for (toSave in objectList) {
                            objectOutputStream.writeObject(toSave)
                        }
                        objectOutputStream.writeLong(saveFileVerificationNumber)
                    }
                }
            }
        }

        /**
         * Tries to load an object from a file.
         *
         * @param context
         * @param fileName The actual file name.
         * @param saveFileVerificationNumber Used to verify the loaded file.
         *
         * @return The loaded object, or null if loading failed.
         */
        private fun <T : Serializable> attemptLoad(
            context: Context,
            fileName: String,
            saveFileVerificationNumber: Long
        ): T? {
            var longEOF = saveFileVerificationNumber + 1
            var potentialLoadedObject: T? = null
            val file = File(
                context.filesDir,
                fileName
            )
            if (file.exists()) {
                try {
                    context.openFileInput(fileName).use { fileInputStream ->
                        ObjectInputStream(fileInputStream).use { objectInputStream ->
                            @Suppress("UNCHECKED_CAST")
                            potentialLoadedObject = objectInputStream.readObject() as T
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
                potentialLoadedObject = null
            }
            return potentialLoadedObject
        }

        /**
         * Tries to load an object from a file.
         *
         * @param context
         * @param fileName The base file name.
         *                 This must match the name of the fileName passed to [save];
         *                 the file name of the saved file may not match.
         *
         * @param saveFileVerificationNumber Used to verify the loaded file.
         *
         * @return The loaded object, or null if loading failed.
         */
        fun <T : Serializable> load(
            context: Context,
            fileName: String,
            saveFileVerificationNumber: Long
        ): T? {
            val fileNames = getFileNames(fileName)
            var potentialLoadedObject: T? = null
            var i = 0
            var done = false
            synchronized(fileLock) {
                while (!done && i < fileNames.size) {
                    potentialLoadedObject = attemptLoad(
                        context,
                        fileNames[i],
                        saveFileVerificationNumber
                    )
                    if (potentialLoadedObject != null) {
                        done = true
                    }
                    i++
                }
            }
            return potentialLoadedObject
        }

        /**
         * Tries to load a list of objects from a file.
         *
         * @param context
         * @param fileName The base file name.
         *                 The file saved may not have that name, but it can be used here
         * @param saveFileVerificationNumber Used to verify the loaded file.
         *
         * @return The loaded list of objects, or null if loading failed.
         */
        fun <T : Serializable> loadList(
            context: Context,
            fileName: String,
            saveFileVerificationNumber: Long
        ): List<T>? {
            val fileNames = getFileNames(fileName)
            var potentialLoadedList: List<T>? = null
            var i = 0
            var done = false
            synchronized(fileLock) {
                while (!done && i < fileNames.size) {
                    potentialLoadedList =
                        attemptLoadList(context, fileNames[i], saveFileVerificationNumber)
                    if (potentialLoadedList != null) {
                        done = true
                    }
                    i++
                }
            }
            return potentialLoadedList
        }

        /**
         * Tries to load a list of objects from a file.
         *
         * @param context
         * @param fileName The base file name.
         * @param saveFileVerificationNumber Used to verify the loaded file.
         *
         * @return The loaded list of objects, or null if loading failed.
         */
        private fun <T : Serializable> attemptLoadList(
            context: Context,
            fileName: String,
            saveFileVerificationNumber: Long
        ): List<T>? {
            var longEOF = 0L
            var potentialLoadedList: MutableList<T>? = mutableListOf()
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                try {
                    context.openFileInput(fileName).use { fileInputStream ->
                        ObjectInputStream(fileInputStream).use { objectInputStream ->
                            val listSize = objectInputStream.readInt()
                            for (i in 0 until listSize) {
                                @Suppress("UNCHECKED_CAST")
                                potentialLoadedList?.add(objectInputStream.readObject() as T)
                            }
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
                potentialLoadedList = null
            }
            return potentialLoadedList
        }

        /**
         * Deletes a file and all of its backups
         *
         * @param context
         * @param fileName The base name of the files to delete.
         */
        fun delete(
            context: Context,
            fileName: String,
        ) {
            val fileNames = getFileNames(fileName)
            synchronized(fileLock) {
                for (fileName in fileNames) {
                    val file = File(
                        context.filesDir,
                        fileName
                    )
                    if (file.exists()) {
                        file.delete()
                    }
                }
            }
        }

    }

}