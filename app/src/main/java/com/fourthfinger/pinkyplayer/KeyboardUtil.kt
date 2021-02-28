package com.fourthfinger.pinkyplayer

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService

class KeyboardUtil {

    companion object {

        fun hideKeyboard(view: View) {
            getSystemService(
                    view.context,
                    InputMethodManager::class.java
            )?.hideSoftInputFromWindow(view.windowToken, 0)
        }

    }

}