package io.fourth_finger.pinky_player

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

class ActivityMainTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<ActivityMain>()

    @Test
    fun mediaControllerSetMediaItem_followedByPlay_makesActivityPlayButtonVisible() = runTest {
        val allowPermissions = getPermissionUIAllowButton()
        allowPermissions.click()

        val application = ApplicationProvider.getApplicationContext<ApplicationMain>()
        val sessionToken = SessionToken(
            application,
            ComponentName(application, ServiceMediaLibrary::class.java)
        )
        val mediaController = MediaController.Builder(application, sessionToken)
            .buildAsync()
            .await()

        val music = application.musicRepository.loadMusicFiles(application.contentResolver)!!
        val mediaItem = MediaItem.Builder()
            .setUri(application.musicRepository.getUri(music[0].id))
            .build()

        val countDownLatchPlay = CountDownLatch(1)
        // The listener is needed for the UI to update; play() is not enough
        mediaController.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        countDownLatchPlay.countDown()
                    }
                }
            }
        )

        UiThreadStatement.runOnUiThread {
            mediaController.setMediaItem(mediaItem)
            mediaController.play()
        }
        countDownLatchPlay.await()

        onView(withId(R.id.controls))
            .check(
                matches(
                    allOf(
                        isCompletelyDisplayed(),
                        withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
                    )
                )
            )

        onView(withId(R.id.button_play_pause))
            .check(
                matches(
                    allOf(
                        DrawableMatcher(R.drawable.ic_baseline_pause_24),
                        isCompletelyDisplayed()
                    )
                )
            )
    }

    @Test
    fun clickingPause_playButtonDisplays() = runTest {
        val allowPermissions = getPermissionUIAllowButton()
        allowPermissions.click()

        val application = ApplicationProvider.getApplicationContext<ApplicationMain>()
        val sessionToken = SessionToken(
            application,
            ComponentName(application, ServiceMediaLibrary::class.java)
        )
        val mediaController = MediaController.Builder(application, sessionToken)
            .buildAsync()
            .await()

        val music = application.musicRepository.loadMusicFiles(application.contentResolver)!!
        val mediaItem = MediaItem.Builder()
            .setUri(application.musicRepository.getUri(music[0].id))
            .build()

        val countDownLatchPlay = CountDownLatch(1)
        mediaController.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        countDownLatchPlay.countDown()
                    }
                }
            }
        )
        UiThreadStatement.runOnUiThread {
            mediaController.setMediaItem(mediaItem)
            mediaController.play()
        }
        countDownLatchPlay.await()

        onView(withId(R.id.controls))
            .check(
                matches(
                    allOf(
                        isCompletelyDisplayed(),
                        withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
                    )
                )
            )

        onView(withId(R.id.button_play_pause))
            .perform(click())

        onView(withId(R.id.button_play_pause))
            .check(
                matches(
                    allOf(
                        isCompletelyDisplayed(),
                        DrawableMatcher(R.drawable.ic_baseline_play_arrow_24)
                    )
                )
            )
    }

    @Test
    fun whenNoPermission_AsksForCorrectPermission() {
        // TODO this only works for API 31

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

    @Test
    fun whenPermissionDenied_userToastShows() {
        // TODO this only works for API 31

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val denyPermissions = getPermissionUIDenyButton()
        denyPermissions.click()

        // Make sure permission has not been granted
        val permissionStatus = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        assert(permissionStatus == PackageManager.PERMISSION_DENIED)

        onView(withText(R.string.permission_needed))
            .check(matches(isCompletelyDisplayed()))
    }

    /**
     * Gets the permission dialog button for allowing permissions.
     */
    private fun getPermissionUIAllowButton(): UiObject {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        return uiDevice.findObject(
            UiSelector().clickable(true).checkable(false).text(
                // TODO
                // Only know that 31 is correct; the rest was copied from a random SO post.
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
                // TODO
                // Only know that 31 is correct; the rest was copied from a random SO post.
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