package io.fourth_finger.pinky_player

import androidx.media3.session.MediaBrowser
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun invokeOnConnection_invokesLambdaAfterConnection() = runTest {
        val countDownLatch = CountDownLatch(1)
        val lambda = { _: MediaBrowser -> countDownLatch.countDown() }
        UiThreadStatement.runOnUiThread {
            mediaBrowserProvider.invokeOnConnection(UnconfinedTestDispatcher(), lambda)
        }
        countDownLatch.await()
    }

}