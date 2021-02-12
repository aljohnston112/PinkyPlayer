package com.fourthfinger.pinkyplayer

import com.fourthfinger.pinkyplayer.settings.Settings
import org.junit.Test

class SettingsTest {

    @Test
    fun validateConstructor() {
        val settings = Settings(1.0, 1.0,1.0 , 1.0)
        assert(settings.maxPercent==(1.0))
        assert(settings.percentChangeUp==(1.0))
        assert(settings.percentChangeDown==(1.0))
        assert(settings.lowerProb==(1.0))
    }

}