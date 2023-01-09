package com.fourth_finger.pinky_player

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.GravityInt
import androidx.core.widget.TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
import androidx.core.widget.TextViewCompat.AutoSizeTextType
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fourth_finger.music_repository.MusicFile
import kotlin.properties.Delegates

/**
 * The [RecyclerView.Adapter] for [MusicFile]s.
 *
 * @param dataSet The list of [MusicFile]s to display.
 */
class MusicFileAdapter(
    private var dataSet: List<MusicFile>,
) : RecyclerView.Adapter<MusicFileAdapter.ViewHolder>() {

    private var onSongClickListener: ((Long) -> Unit) = {}

    /**
     * Updates the adapter with a new list of [MusicFile]s.
     *
     * @param music The new list of [MusicFile]s.
     */
    fun updateMusicList(music: List<MusicFile>) {
        val diffUtilCallback = MusicFileDiffUtilCallback(dataSet, music)
        val diff = DiffUtil.calculateDiff(diffUtilCallback)
        dataSet = music
        diff.dispatchUpdatesTo(this)
    }

    /**
     * Sets a callback for when a song is clicked.
     *
     * @param function The callback to be invoked when a song is clicked.
     */
    fun setOnSongClickListener(function: (Long) -> Unit) {
        onSongClickListener = function
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.music_file_holder,
                parent,
                false
            ),
            onSongClickListener
            )
    }

    /**
     * Sets the [ViewHolder]'s text to the display name.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataSet[position].displayName
        holder.id = dataSet[position].id
    }

    /**
     * Gets the number of items in this [MusicFileAdapter].
     */
    override fun getItemCount() = dataSet.size

    /**
     * The [RecyclerView.ViewHolder] for [MusicFile]s.
     *
     * @param view The item holder [View].
     * @param onSongClickedListener The callback to be invoked when a song is clicked.
     */
    class ViewHolder(
        view: View,
        private val onSongClickedListener: (Long) -> Unit = { }
    ) : RecyclerView.ViewHolder(view) {

        var id by Delegates.notNull<Long>()
        val textView: TextView = view.findViewById(R.id.textView)

        init {
            textView.setOnClickListener {
                onSongClickedListener(id)
            }
        }

    }

}