package io.fourth_finger.music_repository

/**
 * A container for music file data.
 * The file data must have came from the MediaStore and
 * be considered music by the MediaStore.
 *
 * @param id The id the MediaStore gave this music file.
 * @param relativePath The path to the music file.
 *                     Does not include the file name.
 * @param displayName The display name of the music file.
 */
data class MusicItem(
    val id: Long,
    val relativePath: String,
    val displayName: String
) {

    val fullPath: String = relativePath + displayName

    override fun equals(other: Any?): Boolean {
        return other is MusicItem &&
                id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}