package io.fourth_finger.pinky_player

import android.content.Context
import android.util.TypedValue
import androidx.core.content.ContextCompat

class ThemeHelper {

    companion object {
        fun getAttr(context: Context, resID: Int): Int? {
            val typedValue = TypedValue()
            val theme = context.theme

            var attr: Int? = null
            if (theme.resolveAttribute(resID, typedValue, true)) {
                if (typedValue.type == TypedValue.TYPE_REFERENCE) {
                    attr = ContextCompat.getColor(context, typedValue.data)
                } else if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT &&
                    typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT){
                    attr = typedValue.data
                }
            }
            return attr
        }
    }

}