package com.fourthfinger.pinkyplayer

import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import org.hamcrest.BaseMatcher
import org.hamcrest.Description


class DrawableMatcher(private val resourceId: Int) : BaseMatcher<View>() {

    override fun describeTo(description: Description?) {
        description?.appendText("has drawable with id: ")
        description?.appendText(resourceId.toString())
    }

    override fun matches(item: Any?): Boolean {
        if (item is AppCompatImageView) {
            val imageView: AppCompatImageView = item
            val expectedDrawable: Drawable = item.context.getDrawable(resourceId) ?: return false
            val drawable = imageView.drawable
            return drawable == expectedDrawable
        }
        return false
    }

}
