package com.fourthfinger.pinkyplayer.playlists

import androidx.lifecycle.lifecycleScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.LiveDataTestUtil
import com.fourthfinger.pinkyplayer.ViewModelBaseTest
import com.fourthfinger.pinkyplayer.settings.SettingsViewModel
import com.fourthfinger.pinkyplayer.songs.AudioUri
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MediaViewModelTest: ViewModelBaseTest(DummyMediaViewModelFragment()) {

    @Test
    fun viewModel() {
        fragment as DummyMediaViewModelFragment
        val mediaViewModel: MediaViewModel = fragment.mediaViewModel
        val viewLifecycleOwner = fragment.viewLifecycleOwner
        val lifecycleScope = fragment.lifecycleScope

        val displayName = "a"
        val artist = "b"
        val title = "c"
        val id = 4L
        val audioUri = AudioUri(displayName, artist, title, id)

        mediaViewModel.set
        LiveDataTestUtil.checkLiveDataUpdate(lifecycleScope, viewLifecycleOwner, mediaViewModel.currentAudioUri, audioUri)

    }


}