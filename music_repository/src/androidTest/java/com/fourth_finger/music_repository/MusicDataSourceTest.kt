package com.fourth_finger.music_repository

import android.Manifest
import android.content.ContentUris
import android.database.Cursor
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
    var mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule
        .grant(Manifest.permission.READ_EXTERNAL_STORAGE)


    private val context = InstrumentationRegistry.getInstrumentation().context

    /**
     * Test the [MusicFile]s that getMusicFromMediaStore returns
     * represent audio files that are in the MediaStore's audio store and
     * are considered music.
     *
     * It is assumed there is one music file on the device,
     * and it display name is "01 .mp3"
     */
    @Test
    fun getMusicFromMediaStore_ContentResolver_ReturnsCorrectSongs() {

        // Test the returned MusicFile objects represent the correct MediaStore rows
        val musicFiles = MusicDataSource().getMusicFromMediaStore(context.contentResolver).toList()
        val actualMusicFiles = getAllMusicFiles().toList()

        assert(actualMusicFiles.isNotEmpty())
        assert(musicFiles.size == actualMusicFiles.size)
        for((i, music) in musicFiles.withIndex()) {
            assert(music.id == actualMusicFiles[i].id)
            assert(music.displayName == actualMusicFiles[i].displayName)
        }
    }

    private fun getAllMusicFiles(): Collection<MusicFile> {
        val music: MutableCollection<MusicFile> = mutableListOf()

        // The query parameters
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.IS_MUSIC
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != ?"
        val selectionArgs = arrayOf("0")
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

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

            // Convert the database entries to MusicFiles
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                val musicFile = MusicFile(id, displayName)
                music.add(musicFile)
            }
        }
        return music
    }


}