package io.fourth_finger.music_repository

import android.Manifest
import android.provider.MediaStore
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test

/**
 * Tests the [MusicRepository].
 */
class MusicRepositoryTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    private val context = InstrumentationRegistry.getInstrumentation().context

    private val contentResolver = context.contentResolver

    /**
     * Tests that the [MusicFile]s returned by [MusicRepository.loadMusicFiles] match those
     * loaded by the [MediaStore].
     * This test will fail if there are no music files on the test device.
     */
    @Test
    fun loadMusicFiles_ContentResolver_ReturnsCorrectSongs() = runTest {
        val musicFiles = MusicRepository().loadMusicFiles(contentResolver)
        val actualMusicFiles = getAllMusicFiles()

        // Assert there are music files
        assert(musicFiles.isNotEmpty())

        // Assert the music repository loads all of them
        assert(musicFiles.size == actualMusicFiles.size)
        for (musicFile in actualMusicFiles) {
            assert(musicFile in musicFiles)
        }

    }

    /**
     * Tests that the [MusicRepository] caches the results of [MusicRepository.loadMusicFiles] and
     * they can be retrieved via [MusicRepository.getCachedMusicFiles].
     */
    @Test
    fun loadMusicFiles_ContentResolver_CachesCorrectSongs() = runTest {
        val actualMusicFiles = MusicRepository().loadMusicFiles(contentResolver)

        val musicRepository = MusicRepository()
        assertThrows(NoSuchElementException::class.java){
            runBlocking {
                musicRepository.getCachedMusicFiles()
            }
        }
        musicRepository.loadMusicFiles(contentResolver)
        val musicFiles = musicRepository.getCachedMusicFiles()

        // Assert there are music files
        assert(musicFiles.isNotEmpty())

        // Assert the music repository cached all of them
        assert(musicFiles.size == actualMusicFiles.size)
        for (musicFile in actualMusicFiles) {
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
