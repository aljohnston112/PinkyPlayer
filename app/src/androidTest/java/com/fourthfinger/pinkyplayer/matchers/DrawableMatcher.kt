package com.fourthfinger.pinkyplayer.matchers

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import org.hamcrest.BaseMatcher
import org.hamcrest.Description


class DrawableMatcher(private val resourceId: Int) : BaseMatcher<View>() {

    // Recycle bitmaps TODO

    override fun describeTo(description: Description?) {
        description?.appendText("has drawable with id: ")
        description?.appendText(resourceId.toString())
    }

    override fun matches(item: Any?): Boolean {
        if (item is AppCompatImageView) {
            val imageView: AppCompatImageView = item
            val expectedDrawable: Drawable = item.context.getDrawable(resourceId) ?: return false
            val bitmap = imageView.drawable.toBitmap()
            return bitmap.sameAs(expectedDrawable.toBitmap(bitmap.width, bitmap.height))
        } else if(item is ImageButton){
            val imageView: ImageButton = item
            val expectedDrawable: Drawable = item.context.getDrawable(resourceId) ?: return false
            val bitmap = imageView.drawable.toBitmap()
            return bitmap.sameAs(expectedDrawable.toBitmap(bitmap.width, bitmap.height))
        }
        return false
    }

}
