package io.fourth_finger.music_repository

import android.Manifest
import android.provider.MediaStore
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test


/**
 * Tests the [MusicDataSource].
 */
class MusicDataSourceTest {

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val context = InstrumentationRegistry.getInstrumentation().context

    /**
     * Test the [MusicFile]s that getMusicFromMediaStore returns.
     * They must represent audio files that are in the MediaStore's audio store and
     * are considered music.
     * This test will fail if there is no music on the device being tested or
     * the query to the [MediaStore] fails.
     */
    @Test
    fun getMusicFromMediaStore_ContentResolver_ReturnsCorrectSongs() {

        // Assert there are music files
        val actualMusicFiles = getAllMusicFiles().toList()
        assert(actualMusicFiles.isNotEmpty())

        // Assert the data source gets all of them
        val musicDataSource = MusicDataSource()
        val musicFiles = musicDataSource.getMusicFromMediaStore(
            context.contentResolver
        )!!
        assert(musicFiles.size == actualMusicFiles.size)
        for(musicFile in actualMusicFiles) {
            assert(musicFile in musicFiles)
        }

    }

    /**
     * Gets a list of [MusicFile]s that represent files
     * that the [MediaStore] considers music.
     *
     * @return A [List] of [MusicFile]s that represent files
     *         that the [MediaStore] considers music.
     */
    private fun getAllMusicFiles(): List<MusicFile> {
        val music: MutableList<MusicFile> = mutableListOf()

        // The query parameters
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.RELATIVE_PATH,
            )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != ?"
        val selectionArgs = arrayOf("0")
        val sortOrder = "${MediaStore.Audio.Media.RELATIVE_PATH} ASC"

        // The query and conversion to MusicFiles
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.DISPLAY_NAME
            )
            val relativePathColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.RELATIVE_PATH
            )

            // Convert the database entries to MusicFiles
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn)
                val relativePath = cursor.getString(relativePathColumn)

                val musicFile = MusicFile(id, relativePath, displayName)
                music.add(musicFile)
            }
        }
        return music
    }

}