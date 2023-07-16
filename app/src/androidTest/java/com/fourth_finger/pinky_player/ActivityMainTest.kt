package com.fourth_finger.pinky_player

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.fourth_finger.music_repository.MusicRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

@HiltAndroidTest
class ActivityMainTest {

    @get:Rule()
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityScenarioRule = activityScenarioRule<ActivityMain>()

    @Inject
    lateinit var musicRepository: MusicRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun playFromMediaId_controlsVisible() = runTest {
        val allowPermissions = getPermissionUI()
        allowPermissions.click()

        val countDownLatchPlay = CountDownLatch(1)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val music = musicRepository.loadMusicFiles(context.contentResolver)

        activityScenarioRule.scenario.onActivity {
            it.mediaController.transportControls.playFromMediaId(music[0].id.toString(), null)
            countDownLatchPlay.countDown()
        }
        countDownLatchPlay.await()
        onView(allOf(withId(R.id.controls), isCompletelyDisplayed()))
    }


    /**
     * Tests that when there is no permission granted
     * [ActivityMain] brings up the permission dialog for the correct permission.
     */
    @Test
    fun onCreate_WhenNoPermission_AsksForCorrectPermission() {

        // Permission must not be granted
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var permissionStatus = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        assert(permissionStatus != PackageManager.PERMISSION_GRANTED)

        // Accept the permission
        val allowPermissions = getPermissionUI()
        assert(allowPermissions.exists())
        allowPermissions.click()
        assert(!allowPermissions.exists())

        // Make sure the correct permission was granted
        permissionStatus = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        assert(permissionStatus == PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Gets the permission dialog.
     */
    private fun getPermissionUI(): UiObject {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        return uiDevice.findObject(
            UiSelector().clickable(true).checkable(false).text(
                // Only know that 31 is correct for sure; the rest was copied.
                when {
                    Build.VERSION.SDK_INT == 23 -> "Allow"
                    Build.VERSION.SDK_INT <= 28 -> "ALLOW"
                    Build.VERSION.SDK_INT == 29 -> "Allow only while using the app"
                    Build.VERSION.SDK_INT == 31 -> "Allow"
                    else -> "While using the app"
                }
            )
        )
    }

}