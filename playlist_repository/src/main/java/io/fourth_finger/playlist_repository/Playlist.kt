package io.fourth_finger.playlist_repository

class Playlist(
    val hash: Int,
    val name: String,
    val musicIds: List<Long>
)