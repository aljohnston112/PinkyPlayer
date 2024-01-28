package io.fourth_finger.pinky_player

import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import org.hamcrest.BaseMatcher
import org.hamcrest.Description


/**
 * Matches [ImageView]s that have a drawable with the given resource id.
 *
 * @param resourceId The resource id of the drawable.
 */
class DrawableMatcher(private val resourceId: Int) : BaseMatcher<View>() {

    override fun describeTo(description: Description) {
        description.appendText("has drawable with id: ")
        description.appendText(resourceId.toString())
    }

    override fun matches(item: Any): Boolean {
        var result = item is ImageView
        if(result) {
            val expectedDrawable = (item as ImageView).context.getDrawable(resourceId)
            if(expectedDrawable != null){
                val bitmap = item.drawable.toBitmap()
                val bitmap2 = expectedDrawable.toBitmap(bitmap.width, bitmap.height)
                result = bitmap.sameAs(bitmap2)
                bitmap.recycle()
                bitmap2.recycle()
            } else {
                result = false
            }
        }
        return result
    }

}