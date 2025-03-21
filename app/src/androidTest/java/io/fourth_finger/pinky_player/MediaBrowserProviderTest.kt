package io.fourth_finger.pinky_player

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.fourth_finger.shared_resources.MediaBrowserProvider
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
    fun await_returnsConnectedMediaBrowser() = runTest {
        Assert.assertTrue(mediaBrowserProvider.await().isConnected)
    }

}