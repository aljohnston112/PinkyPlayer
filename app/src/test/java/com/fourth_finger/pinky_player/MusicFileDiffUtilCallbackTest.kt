package com.fourth_finger.pinky_player

import android.net.Uri
import com.fourth_finger.music_repository.MusicFile
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MusicFileDiffUtilCallbackTest {

    private val urlStub = Uri.fromParts("", "", "")
    private val emptyList = emptyList<MusicFile>()
    private val singletonList = listOf(
        MusicFile(0, "A", urlStub)
    )

    @Test
    fun getOldListSize_EmptyList_ReturnsZero() {
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(emptyList, singletonList)
        assert(musicFileDiffUtilCallback.oldListSize == 0)
    }

    @Test
    fun getOldListSize_NonEmptyList_ReturnsCorrectSize() {
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(singletonList, emptyList)
        assert(musicFileDiffUtilCallback.oldListSize == 1)
    }

    @Test
    fun getNewListSize_EmptyList_ReturnsZero() {
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(singletonList, emptyList)
        assert(musicFileDiffUtilCallback.newListSize == 0)
    }

    @Test
    fun getNewListSize_NonEmptyList_ReturnsCorrectSize() {
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(emptyList, singletonList)
        assert(musicFileDiffUtilCallback.newListSize == 1)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun areItemsTheSame_FirstArgEmptyList_Throws(){
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(
            emptyList,
            singletonList
        )
        musicFileDiffUtilCallback.areItemsTheSame(0, 0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun areItemsTheSame_SecondArgEmptyList_Throws(){
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(
            singletonList,
            emptyList
        )
        musicFileDiffUtilCallback.areItemsTheSame(0, 0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun areItemsTheSame_FirstArgOutOfRangeIndex_Throws(){
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(
            singletonList,
            singletonList
        )
        musicFileDiffUtilCallback.areItemsTheSame(1, 0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun areItemsTheSame_SecondArgOutOfRangeIndex_Throws(){
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(
            singletonList,
            singletonList
        )
        musicFileDiffUtilCallback.areItemsTheSame(0, 1)
    }

    @Test
    fun areItemsTheSame_DifferentIDs_ReturnsFalse(){
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(
            listOf(
                MusicFile(0, "A", urlStub)
            ),
            listOf(
                MusicFile(1, "A", urlStub)
            )
        )
        assert(!musicFileDiffUtilCallback.areItemsTheSame(0, 0))
    }

    @Test
    fun areItemsTheSame_SameIDs_ReturnsTrue(){
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(
            singletonList,
            singletonList
        )
        assert(musicFileDiffUtilCallback.areItemsTheSame(0, 0))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun areContentsTheSame_FirstArgEmptyList_Throws(){
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(
            emptyList,
            singletonList
        )
        musicFileDiffUtilCallback.areContentsTheSame(0, 0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun areContentsTheSame_SecondArgEmptyList_Throws(){
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(
            singletonList,
            emptyList
        )
        musicFileDiffUtilCallback.areContentsTheSame(0, 0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun areContentsTheSame_FirstArgOutOfRangeIndex_Throws(){
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(
            singletonList,
            singletonList
        )
        musicFileDiffUtilCallback.areContentsTheSame(1, 0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun areContentsTheSame_SecondArgOutOfRangeIndex_Throws(){
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(
            singletonList,
            singletonList
        )
        musicFileDiffUtilCallback.areContentsTheSame(0, 1)
    }

    @Test
    fun areContentsTheSame_DifferentIDs_ReturnsFalse(){
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(
            listOf(
                MusicFile(0, "A", urlStub)
            ),
            listOf(
                MusicFile(1, "A", urlStub)
            )
        )
        assert(!musicFileDiffUtilCallback.areContentsTheSame(0, 0))
    }

    @Test
    fun areContentsTheSame_DifferentDisplayName_ReturnsFalse(){
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(
            listOf(
                MusicFile(0, "A", urlStub)
            ),
            listOf(
                MusicFile(0, "B", urlStub)
            )
        )
        assert(!musicFileDiffUtilCallback.areContentsTheSame(0, 0))
    }

    @Test
    fun areContentsTheSame_SameContents_ReturnsTrue(){
        val musicFileDiffUtilCallback = MusicFileDiffUtilCallback(
            singletonList,
            singletonList
        )
        assert(musicFileDiffUtilCallback.areContentsTheSame(0, 0))
    }
    
}