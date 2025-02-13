package io.fourth_finger.settings_repository

import java.io.Serializable

/**
 * A class for app settings.
 *
 * @param probabilityDownMultiplier The multiplier used for all other songs when skipping a song.
 * @param respectsAudioFocus Whether or not the audio focus of other apps should be respected.
 */
data class Settings(
    val probabilityDownMultiplier: Int,
    val respectsAudioFocus: Boolean
): Serializable