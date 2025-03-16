package io.fourth_finger.playlist_repository

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

internal object PlaylistSerializer: Serializer<PlaylistsProto> {

    override val defaultValue: PlaylistsProto = PlaylistsProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): PlaylistsProto {
        try {
            return PlaylistsProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException(
                "Cannot read proto.",
                exception
            )
        }

    }

    override suspend fun writeTo(
        t: PlaylistsProto,
        output: OutputStream
    ) = t.writeTo(output)

}

internal val Context.playlistDataStore: DataStore<PlaylistsProto> by dataStore(
    fileName = "playlist.pb",
    serializer = PlaylistSerializer
)
