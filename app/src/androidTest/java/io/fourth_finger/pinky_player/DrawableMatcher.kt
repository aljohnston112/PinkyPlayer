package io.fourth_finger.pinky_player

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import org.hamcrest.BaseMatcher
import org.hamcrest.Description


class DrawableMatcher(private val resourceId: Int) : BaseMatcher<View>() {

    override fun describeTo(description: Description?) {
        description?.appendText("has drawable with id: ")
        description?.appendText(resourceId.toString())
    }

    override fun matches(item: Any): Boolean {
        val expectedDrawable: Drawable = (item as ImageView).context.getDrawable(resourceId) ?: return false
        val bitmap = item.drawable.toBitmap()
        val bitmap2 = expectedDrawable.toBitmap(bitmap.width, bitmap.height)
        val b = bitmap.sameAs(bitmap2)
        bitmap.recycle()
        bitmap2.recycle()
        return b
    }

}
