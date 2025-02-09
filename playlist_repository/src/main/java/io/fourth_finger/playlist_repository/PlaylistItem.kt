package io.fourth_finger.playlist_repository

/**
 * A container for playlist file data.
 *
 * @param id The id the playlist file.
 * @param name The name of the playlist.
 */
class PlaylistItem(
    val id: Int,
    val name: String,
) {

    override fun equals(other: Any?): Boolean {
        return other is PlaylistItem &&
                id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}