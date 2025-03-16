package io.fourth_finger.playlist_repository

/**
 * A UI data container for playlist file data.
 *
 * @param hash The hash the playlist file.
 * @param name The name of the playlist.
 */
class PlaylistItem(
    val hash: Int,
    val name: String,
) {

    override fun equals(other: Any?): Boolean {
        return other is PlaylistItem &&
                hash == other.hash
    }

    override fun hashCode(): Int {
        return hash.hashCode()
    }

}