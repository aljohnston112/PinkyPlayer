package com.fourthfinger.pinkyplayer

import com.fourthfinger.pinkyplayer.settings.Settings
import org.junit.Test

class SettingsTest {

    @Test
    fun validateConstructor() {
        val maxPercent = 1.234
        val percentChangeUp = 1.5346
        val percentChangeDown = 4.345
        val lowerProb = 32.343
        val settings = Settings(maxPercent, percentChangeUp,percentChangeDown , lowerProb)
        assert(settings.maxPercent==maxPercent)
        assert(settings.percentChangeUp==percentChangeUp)
        assert(settings.percentChangeDown==percentChangeDown)
        assert(settings.lowerProb==lowerProb)
    }

}