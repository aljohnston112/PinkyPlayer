package com.fourthfinger.pinkyplayer

import android.view.View
import org.hamcrest.Matcher

class EspressoTestMatcher {

    companion object {

        fun withDrawable(resourceId: Int): Matcher<View> {
            return DrawableMatcher(resourceId)
        }

    }

}