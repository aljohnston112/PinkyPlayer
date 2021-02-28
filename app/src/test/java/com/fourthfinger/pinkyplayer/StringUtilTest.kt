package com.fourthfinger.pinkyplayer

import org.junit.Test
import java.util.concurrent.TimeUnit

class StringUtilTest {

    @Test
    fun testFormatMillis() {
        for (millis in 0..Long.MAX_VALUE) {
            val h = TimeUnit.MILLISECONDS.toHours(millis)
            val m = TimeUnit.MILLISECONDS.toMinutes(millis) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
            val s = TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            val f = StringUtil.formatMillis(millis)
            assert(f == String.format("%02d:%02d:%02d", h, m, s))
        }
    }

}