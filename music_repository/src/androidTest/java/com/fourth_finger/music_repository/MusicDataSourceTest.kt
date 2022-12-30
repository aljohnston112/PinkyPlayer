package com.fourth_finger.music_repository

import android.Manifest
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
        val musicFiles = MusicDataSource.getMusicFromMediaStore(context.contentResolver)
        assert(musicFiles.size == 1)
        assert(musicFiles[0].displayName == "01 .mp3")
    }


}