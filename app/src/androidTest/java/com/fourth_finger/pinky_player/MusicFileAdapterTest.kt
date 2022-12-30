package com.fourth_finger.pinky_player

import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.platform.app.InstrumentationRegistry
import com.fourth_finger.music_repository.MusicFile
import org.junit.Before
import org.junit.Test
import com.fourth_finger.pinky_player.R

class MusicFileAdapterTest {

    private val emptyList = emptyList<MusicFile>()
    private val singletonList = listOf(
        MusicFile(0, "A")
    )

    private val context = InstrumentationRegistry.getInstrumentation().context
    private val layoutId = R.layout.music_file_holder
    private val viewStub = LinearLayout(context)

    @Before
    fun setUpViewStub() {
        viewStub.removeAllViews()
        val textView = TextView(context)
        textView.id = R.id.textView
        viewStub.addView(textView)
    }

    @Test
    fun updateMusicList_NewList_UpdatesCount() {
        val adapter = MusicFileAdapter(
            layoutId,
            emptyList
        )
        assert(adapter.itemCount == 0)
        adapter.updateMusicList(singletonList)
        assert(adapter.itemCount == 1)
    }

    @Test
    fun onCreateViewHolder_InflatesCorrectLayout() {
        val adapter = MusicFileAdapter(
            layoutId,
            singletonList
        )
        val viewHolder = adapter.createViewHolder(viewStub, 0)
        assert(viewHolder.textView.id == R.id.textView)
    }

    @Test
    fun onBindViewHolder_ValidPositions_BindCorrectly() {
        val adapter = MusicFileAdapter(
            layoutId,
            listOf(
                MusicFile(0, "A"),
                MusicFile(1, "B")
            )
        )
        val viewHolder = MusicFileAdapter.ViewHolder(viewStub)

        adapter.onBindViewHolder(viewHolder, 0)
        assert(viewHolder.textView.text == "A")

        adapter.onBindViewHolder(viewHolder, 1)
        assert(viewHolder.textView.text == "B")
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun onBindViewHolder_InValidPositions_Throws() {
        val adapter = MusicFileAdapter(
            layoutId,
            singletonList
        )
        val viewHolder = MusicFileAdapter.ViewHolder(viewStub)

        adapter.onBindViewHolder(viewHolder, 1)
    }

    @Test
    fun getItemCount_EmptyList_ReturnsZero() {
        val adapter = MusicFileAdapter(layoutId, emptyList)
        assert(adapter.itemCount == 0)
    }

    @Test
    fun getItemCount_ListWithOne_ReturnsOne() {
        val adapter = MusicFileAdapter(layoutId, singletonList)
        assert(adapter.itemCount == 1)
    }

}