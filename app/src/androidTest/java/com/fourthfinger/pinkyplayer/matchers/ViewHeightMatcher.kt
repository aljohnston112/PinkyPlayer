package com.fourthfinger.pinkyplayer.matchers

import android.view.View
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class ViewHeightMatcher(private val height: Int): BaseMatcher<View>() {

    override fun describeTo(description: Description?) {
        description?.appendText("has height of at least: ")
        description?.appendText(height.toString())
    }

    override fun matches(item: Any?): Boolean {
        if(item is View){
            return item.height >= this.height
        }
        return false
    }


}