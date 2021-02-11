package com.fourthfinger.pinkyplayer

import com.fourthfinger.pinkyplayer.settings.Settings
import org.junit.Test

class SettingsTest {

    @Test
    fun validateConstructor() {
        val settings = Settings(1.0, 1.0,1.0 , 1.0)
        assert(settings.maxPercent.equals(1.0))
        assert(settings.percentChangeUp.equals(1.0))
        assert(settings.percentChangeDown.equals(1.0))
        assert(settings.lowerProb.equals(1.0))
    }

}