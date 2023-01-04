package com.fourth_finger.pinky_player

import android.Manifest
import android.content.pm.PackageManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch


class ActivityMainTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<ActivityMain>()

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

        // Accept the permission to make sure the correct one is granted
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val allowPermissions = uiDevice.findObject(
            UiSelector().clickable(true).checkable(false).index(GRANT_BUTTON_INDEX)
        )
        assert(allowPermissions.exists())
        allowPermissions.click()
        assert(!allowPermissions.exists())
        permissionStatus = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        assert(permissionStatus == PackageManager.PERMISSION_GRANTED)
    }

    companion object {
        const val GRANT_BUTTON_INDEX = 1
    }

}