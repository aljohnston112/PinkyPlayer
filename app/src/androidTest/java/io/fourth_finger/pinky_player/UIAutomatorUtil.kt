package io.fourth_finger.pinky_player

import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector

class UIAutomatorUtil {

    companion object {

        /**
         * Gets the permission dialog button for allowing permissions.
         */
        fun getPermissionUIAllowButton(): UiObject {
            val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            return uiDevice.findObject(
                UiSelector().clickable(true).checkable(false).text(
                    // TODO
                    // Only know that 31 is correct; the rest was copied from a random SO post.
                    when {
                        Build.VERSION.SDK_INT == 23 -> "Allow"
                        Build.VERSION.SDK_INT <= 28 -> "ALLOW"
                        Build.VERSION.SDK_INT == 29 -> "Allow only while using the app"
                        Build.VERSION.SDK_INT >= 31 -> "Allow"
                        else -> "While using the app"
                    }
                )
            )
        }

        /**
         * Gets the permission dialog button for allowing permissions.
         */
        fun getPermissionUIDenyButton(): UiObject {
            val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            return uiDevice.findObject(
                UiSelector().clickable(true).checkable(false).text(
                    // TODO
                    // Only know that 31 is correct; the rest was copied from a random SO post.
                    when {
                        Build.VERSION.SDK_INT == 23 -> "Don't allow"
                        Build.VERSION.SDK_INT <= 28 -> "DON'T ALLOW"
                        Build.VERSION.SDK_INT == 29 -> "Don't allow"
                        Build.VERSION.SDK_INT >= 31 -> "Don\u2019t allow"
                        else -> "Don't allow"
                    }
                )
            )
        }

    }

}