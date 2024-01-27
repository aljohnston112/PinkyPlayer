package io.fourth_finger.pinky_player

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class MediaBrowserProviderTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var mediaBrowserProvider: MediaBrowserProvider

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun getMediaBrowser_returnsConnectedMediaBrowser() = runTest {
        Assert.assertTrue(mediaBrowserProvider.get().isConnected)
    }

}