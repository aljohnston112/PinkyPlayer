package io.fourth_finger.pinky_player

import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.platform.app.InstrumentationRegistry
import io.fourth_finger.music_repository.MusicFile
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch

class MusicFileAdapterTest {

    private val emptyList = emptyList<MusicFile>()
    private val singletonList = listOf(
        MusicFile(0, "/B", "A")
    )

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val viewStub = LinearLayout(context)

    @Before
    fun setUpViewStub() {
        viewStub.removeAllViews()
        val textView = TextView(context)
        textView.id = R.id.textView
        viewStub.addView(textView)
    }

    /**
     * Tests that providing a new list of music to the adapter
     * updates the backing list.
     */
    @Test
    fun updateMusicList_NewList_UpdatesItemCountAndViewHolders() {
        val adapter = MusicFileAdapter(emptyList){ /** onClick **/ }
        assert(adapter.itemCount == 0)
        adapter.updateMusicList(singletonList)
        assert(adapter.itemCount == 1)

        val viewHolder = adapter.createViewHolder(viewStub, 0)
        adapter.onBindViewHolder(viewHolder, 0)
        assert(viewHolder.textView.text == singletonList[0].relativePath + singletonList[0].displayName)
    }

    /**
     * Tests that the ViewHolder created by the adapter
     * contains the correct view for data binding.
     */
    @Test
    fun onCreateViewHolder_CorrectLayout() {
        val adapter = MusicFileAdapter(singletonList){ /** onClick **/ }
        val viewHolder = adapter.createViewHolder(viewStub, 0)
        assert(viewHolder.textView.id == R.id.textView)
    }

    /**
     * Tests that the adapter binds the data
     * to the layout correctly.
     */
    @Test
    fun onBindViewHolder_ValidPositions_BindCorrectly() {
        val adapter = MusicFileAdapter(
            listOf(
                MusicFile(0, "B/","A"),
                MusicFile(1, "B/","B")
            )
        ){ /** onClick **/ }
        val viewHolder = MusicFileAdapter.ViewHolder(viewStub)

        adapter.onBindViewHolder(viewHolder, 0)
        assert(viewHolder.textView.text == "B/A")

        adapter.onBindViewHolder(viewHolder, 1)
        assert(viewHolder.textView.text == "B/B")
    }

    /**
     * Tests that the adapter centers text
     * in the layout correctly.
     */
    @Test
    fun onBindViewHolder_ValidPositions_CentersText() {
        val adapter = MusicFileAdapter(singletonList){ /** onClick **/ }
        val viewHolder = MusicFileAdapter.ViewHolder(viewStub)

        adapter.onBindViewHolder(viewHolder, 0)
        assert(viewHolder.textView.gravity.and(Gravity.CENTER) == Gravity.CENTER)
    }

    /**
     * Tests that onBindViewHolder will not
     * take an index greater than the size of the list.
     */
    @Test(expected = IndexOutOfBoundsException::class)
    fun onBindViewHolder_InValidPosition_Throws() {
        val adapter = MusicFileAdapter(singletonList){ /** onClick **/ }
        val viewHolder = MusicFileAdapter.ViewHolder(viewStub)

        adapter.onBindViewHolder(viewHolder, 1)
    }

    /**
     * Tests that onBindViewHolder will not
     * take an index less than 0.
     */
    @Test(expected = IndexOutOfBoundsException::class)
    fun onBindViewHolder_NegativePosition_Throws() {
        val adapter = MusicFileAdapter(singletonList){ /** onClick **/ }
        val viewHolder = MusicFileAdapter.ViewHolder(viewStub)

        adapter.onBindViewHolder(viewHolder, -1)
    }

    /**
     * Tests that getItemCount works with an empty list.
     */
    @Test
    fun getItemCount_EmptyList_ReturnsZero() {
        val adapter = MusicFileAdapter(emptyList){}
        assert(adapter.itemCount == 0)
    }

    /**
     * Tests that getItemCount works with an non-empty list.
     */
    @Test
    fun getItemCount_ListWithOne_ReturnsOne() {
        val adapter = MusicFileAdapter(singletonList){}
        assert(adapter.itemCount == 1)
    }

    @Test
    fun textViewClicked_CallsOnClickListenerWithCorrectId(){
        val countDownLatch = CountDownLatch(1)
        val adapter = MusicFileAdapter(
            singletonList
        ){
            assert(it == singletonList[0].id)
            countDownLatch.countDown()
        }
        val viewHolder = adapter.createViewHolder(viewStub, 0)
        adapter.bindViewHolder(viewHolder, 0)
        viewHolder.textView.performClick()
        countDownLatch.await()
    }

}