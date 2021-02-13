package com.fourthfinger.pinkyplayer.songs

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
        @PrimaryKey val id : Long,
        @ColumnInfo(name = "title") val title :String,
){
    @Ignore var selected: Boolean = false
}