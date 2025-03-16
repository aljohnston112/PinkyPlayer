package io.fourth_finger.playlist_repository

import androidx.datastore.core.CorruptionException
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.time.Duration

class PlaylistSerializerTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun load_defaultPlaylists_returnsDefaultPlaylists() =
        runTest(timeout = Duration.parse("60s")) {
            val defaultPlaylists = PlaylistSerializer.defaultValue
            assertEquals(defaultPlaylists.playlistCount, 0)
        }

    @Test
    fun readFrom_validProto_returnsThatProto() =
        runTest(timeout = Duration.parse("60s")) {
            val validProto = PlaylistsProto.newBuilder().apply {
                addPlaylist(
                    PlaylistProto.newBuilder().apply {
                        setHash(19283)
                        setName("AZBYC")
                        addAllSongId(listOf<Long>(4, 7, 5, 6))
                    }
                )
            }.build()
            val inputStream = ByteArrayInputStream(validProto.toByteArray())

            val result = PlaylistSerializer.readFrom(inputStream)
            assertEquals(validProto, result)
        }

    @Test
    fun readFrom_corruptedProto_throwsException() =
        runTest(timeout = Duration.parse("60s")) {
            val inputStream = ByteArrayInputStream(byteArrayOf(1, 9, 2, 8, 3))

            assertThrows(
                CorruptionException::class.java
            ) {
                runBlocking {
                    PlaylistSerializer.readFrom(inputStream)
                }
            }
        }

    @Test
    fun readFrom_afterWriteTo_returnsCorrectProto() =
        runTest(timeout = Duration.parse("60s")) {
            val validProto = PlaylistsProto.newBuilder().apply {
                addPlaylist(
                    PlaylistProto.newBuilder().apply {
                        setHash(19283)
                        setName("AZBYC")
                        addAllSongId(listOf<Long>(4, 7, 5, 6))
                    }
                )
            }.build()
            val outputStream = ByteArrayOutputStream()
            PlaylistSerializer.writeTo(validProto, outputStream)

            val inputStream = ByteArrayInputStream(validProto.toByteArray())
            val result = PlaylistSerializer.readFrom(inputStream)

            assertArrayEquals(result.toByteArray(), outputStream.toByteArray())
        }


}