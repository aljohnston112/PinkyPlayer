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
data class MusicFile(
    val id: Long,
    val relativePath: String,
    val displayName: String
) {

    val fullPath: String = relativePath + displayName

    override fun equals(other: Any?): Boolean {
        return other is MusicFile &&
                id == other.id &&
                relativePath == other.relativePath &&
                displayName == other.displayName
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + relativePath.hashCode()
        result = 31 * result + displayName.hashCode()
        return result
    }

}