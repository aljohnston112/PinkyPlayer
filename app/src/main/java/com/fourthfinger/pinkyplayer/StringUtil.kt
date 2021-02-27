package com.fourthfinger.pinkyplayer

import android.content.Context
import android.os.Build
import java.util.*
import java.util.concurrent.TimeUnit


class StringUtil {

    companion object {

        fun formatMillis(locale: Locale, millis: Int): String {
            return String.format(locale,
                    "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis.toLong()),
                    TimeUnit.MILLISECONDS.toMinutes(millis.toLong()) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis.toLong())),
                    TimeUnit.MILLISECONDS.toSeconds(millis.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis.toLong())))
        }

        fun getLocale(context: Context): Locale? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales[0]
            } else {
                context.resources.configuration.locale
            }
        }

    }

}