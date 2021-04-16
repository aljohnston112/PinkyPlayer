package com.fourthfinger.pinkyplayer

import org.junit.Test
import java.util.concurrent.TimeUnit

class StringUtilTest {

    @Test
    fun testFormatMillis() {
        val millises = listOf(0L, Long.MAX_VALUE)
        for (millis in millises) {
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