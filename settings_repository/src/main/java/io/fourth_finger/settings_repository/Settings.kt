package io.fourth_finger.settings_repository

import java.io.Serializable

/**
 * A class for app settings.
 *
 * @param skipMultiplier The multiplier used for all other songs when skipping a song.
 */
data class Settings(val skipMultiplier: Long): Serializable