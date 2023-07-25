package io.fourth_finger.pinky_player

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import kotlin.time.Duration

class ActivityMainTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<ActivityMain>()


    @Test
    fun whenPermissionDenied_UserToastShows(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val denyPermissions = getPermissionUIDenyButton()
        denyPermissions.click()

        val permissionStatus = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        assert(permissionStatus == PackageManager.PERMISSION_DENIED)

        onView(withText(R.string.permission_needed))
            .check(matches(isCompletelyDisplayed()));
    }

    @Test
    fun playFromMediaId_controlsVisible() = runTest(timeout = Duration.parse("60s")) {
        val allowPermissions = getPermissionUIAllowButton()
        allowPermissions.click()

        val countDownLatchPlay = CountDownLatch(1)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val music = application.musicRepository.loadMusicFiles(context.contentResolver)!!

        activityScenarioRule.scenario.onActivity {
            it.mediaController.transportControls.playFromMediaId(music[0].id.toString(), null)
            countDownLatchPlay.countDown()
        }
        countDownLatchPlay.await()
        onView(allOf(withId(R.id.controls), isCompletelyDisplayed()))
        onView(allOf(withId(R.id.button_play_pause), DrawableMatcher(R.drawable.ic_baseline_pause_24), isCompletelyDisplayed()))
    }

    @Test
    fun clickingPause_playButtonDisplays() = runTest(timeout = Duration.parse("60s")) {
        val allowPermissions = getPermissionUIAllowButton()
        allowPermissions.click()

        val countDownLatchPlay = CountDownLatch(1)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val application = ApplicationProvider.getApplicationContext<MainApplication>()
        val music = application.musicRepository.loadMusicFiles(context.contentResolver)!!

        activityScenarioRule.scenario.onActivity {
            it.mediaController.transportControls.playFromMediaId(music[0].id.toString(), null)
            countDownLatchPlay.countDown()
        }
        countDownLatchPlay.await()
        onView(allOf(withId(R.id.controls))).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.button_play_pause)).perform(click())
        onView(
            allOf(withId(R.id.button_play_pause), DrawableMatcher(R.drawable.ic_baseline_play_arrow_24))
        ).check(matches(isCompletelyDisplayed()))
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
        val allowPermissions = getPermissionUIAllowButton()
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
     * Gets the permission dialog button for allowing permissions.
     */
    private fun getPermissionUIAllowButton(): UiObject {
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

    /**
     * Gets the permission dialog button for allowing permissions.
     */
    private fun getPermissionUIDenyButton(): UiObject {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        return uiDevice.findObject(
            UiSelector().clickable(true).checkable(false).text(
                // Only know that 31 is correct for sure; the rest was a guess.
                when {
                    Build.VERSION.SDK_INT == 23 -> "Don't allow"
                    Build.VERSION.SDK_INT <= 28 -> "DON'T ALLOW"
                    Build.VERSION.SDK_INT == 29 -> "Don't allow"
                    Build.VERSION.SDK_INT == 31 -> "Don\u2019t allow"
                    else -> "Don't allow"
                }
            )
        )
    }

}