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
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.fourth_finger.music_repository.MusicRepository
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import kotlin.time.Duration

@HiltAndroidTest
class ActivityMainTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityScenarioRule = activityScenarioRule<ActivityMain>()

    private val application: HiltTestApplication = ApplicationProvider.getApplicationContext()

    private val sessionToken = SessionToken(
        application,
        ComponentName(application, ServiceMediaLibrary::class.java)
    )

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun mediaControllerSetMediaItem_followedByPlay_makesPlayButtonVisible() =
        // TODO this hangs now; run all tests when fixed
        runTest(timeout = Duration.parse("60s")) {
            val allowPermissions = getPermissionUIAllowButton()
            allowPermissions.click()

            // Setup media controller listener
            val countDownLatchPlay = CountDownLatch(1)
            val mediaController = buildMediaController()
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

            // Play a song and wait for it to start
            val mediaItem = getFirstMusicUri()
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
    fun mediaControllerSetMediaItem_followedByPlay_thenByClickingPause_makesPlayButtonVisible() = runTest(timeout = Duration.parse("60s")) {
        val allowPermissions = getPermissionUIAllowButton()
        allowPermissions.click()

        // Setup media controller listener
        val countDownLatchPlay = CountDownLatch(1)
        val mediaController = buildMediaController()
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

        // Play a song and wait for it to start
        val mediaItem = getFirstMusicUri()
        UiThreadStatement.runOnUiThread {
            mediaController.setMediaItem(mediaItem)
            mediaController.play()
        }
        countDownLatchPlay.await()

        // Pause the music
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
        var permissionStatus = ContextCompat.checkSelfPermission(
            application,
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
            application,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        assert(permissionStatus == PackageManager.PERMISSION_GRANTED)
    }

    @Test
    fun whenPermissionDenied_userToastShows() {
        // TODO this only works for API 31

        val denyPermissions = getPermissionUIDenyButton()
        denyPermissions.click()

        // Make sure permission has not been granted
        val permissionStatus = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        assert(permissionStatus == PackageManager.PERMISSION_DENIED)

        onView(withText(R.string.permission_needed))
            .check(matches(isCompletelyDisplayed()))
    }

    private suspend fun buildMediaController(): MediaController {
        return MediaController.Builder(application, sessionToken)
            .buildAsync()
            .await()
    }

    private suspend fun getFirstMusicUri(): MediaItem {
        val musicRepository = MusicRepository()
        val music = musicRepository.loadMusicFiles(application.contentResolver)
        return MediaItem.Builder()
            .setUri(musicRepository.getUri(music[0].id))
            .build()
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