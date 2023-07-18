package com.fourth_finger.pinky_player

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
                if (typedValue.resourceId != 0) {
                    attr = ContextCompat.getColor(context, typedValue.resourceId)
                }
            }
            return attr
        }
    }

}