package com.fourthfinger.pinkyplayer

import android.content.Context
import android.widget.Toast

class ToastUtil {

    companion object {

        fun showToast(context: Context, textID: Int) {
            val duration = Toast.LENGTH_LONG
            val toast = Toast.makeText(context, context.getText(textID), duration)
            toast.show()
        }

    }

}