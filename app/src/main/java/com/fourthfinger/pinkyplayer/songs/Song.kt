package com.fourthfinger.pinkyplayer.songs

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "songs")
data class Song constructor(
        @PrimaryKey val id: Long,
        @ColumnInfo(name = "title") val title: String,
) : Serializable, Comparable<Song> {
    @Ignore var selected: Boolean = false

    override fun compareTo(o: Song): Int {
        return title.compareTo(o.title)
    }

    override fun equals(other: Any?): Boolean {
        return other is Song && id == other.id
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }

}