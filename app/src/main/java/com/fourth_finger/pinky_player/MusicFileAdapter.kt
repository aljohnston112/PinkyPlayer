package com.fourth_finger.pinky_player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
     */
    class ViewHolder(
        view: View,
        private val callback: (Long) -> Unit = { }
    ) : RecyclerView.ViewHolder(view) {

        var id by Delegates.notNull<Long>()

        val textView: TextView = view.findViewById(R.id.textView)

        init {
            textView.setOnClickListener {
                callback(id)
            }
        }

    }

}