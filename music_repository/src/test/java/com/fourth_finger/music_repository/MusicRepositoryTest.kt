package com.fourth_finger.music_repository

import android.content.ContentValues
import android.os.Looper
import android.provider.MediaStore
import org.junit.Test
import org.junit.runner.RunWith

import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import java.util.UUID.randomUUID

/**
 * Tests the [MusicRepository].
 */
@RunWith(RobolectricTestRunner::class)
class MusicRepositoryTest {

    /**
     * Test the [MusicFile]s that getMusicFromMediaStore returns
     * represent audio files that are in the MediaStore's audio store and
     * are considered music.
     */
    @Test
    fun loadMusicFiles_ContentResolver_ReturnsCorrectSongs() {

        // Add rows to the ContentProvider
        val halfNumRows = 50;
        val validIdToDisplayNameMap = mutableMapOf<Long, String>()
        insertMusic(validIdToDisplayNameMap, halfNumRows, 1)
        val invalidIdToDisplayNameMap = mutableMapOf<Long, String>()
        insertMusic(invalidIdToDisplayNameMap, halfNumRows, 0)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Test the returned MusicFile objects represent the correct MediaStore rows
        val app = RuntimeEnvironment.getApplication()
        MusicRepository.getInstance().loadMusicFiles(app.contentResolver)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        MusicRepository.getInstance().musicFiles.observeForever {
            assert(it.size == halfNumRows)
            for ((i, key) in validIdToDisplayNameMap.keys.withIndex()) {
                assert(it[i].id == key)
                assert(it[i].displayName == validIdToDisplayNameMap[key])
            }
        }

    }

    /**
     * Inserts music into the ContentResolver;
     * it is specifically the MediaStore's audio database that is populated.
     */
    private fun insertMusic(
        idToDisplayNameMap: MutableMap<Long, String>,
        halfNumRows: Int,
        isMusic: Int
    ) {
        for (i in 0 until halfNumRows) {
            idToDisplayNameMap[randomUUID()!!.hashCode().toLong()] = randomUUID()!!.toString()
        }
        for (pair in idToDisplayNameMap) {
            insert(pair.key, pair.value, isMusic)
        }
    }

    /**
     * Inserts music into the ContentResolver;
     * it is specifically the MediaStore's audio database that is populated.
     *
     * @param id The id of the row in the MediaStore.
     * @param displayName The display name row in the MediaStore.
     * @param isMusic True if the MediaStore entry is to be considered music.
     */
    private fun insert(id: Long, displayName: String, isMusic: Int) {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Audio.Media._ID, id)
        contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, displayName)
        contentValues.put(MediaStore.Audio.Media.IS_MUSIC, isMusic)
        val app = RuntimeEnvironment.getApplication()
        app.contentResolver.insert(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

}
