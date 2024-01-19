package io.fourth_finger.pinky_player

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class ApplicationMainTest {

    @Test
    fun getMediaBrowser_returnsConnectedMediaBrowser() = runTest {
        val application = ApplicationProvider.getApplicationContext<ApplicationMain>()
        val browser = application.getMediaBrowser()
        Assert.assertTrue(browser.isConnected)
    }

}