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
 * A [RecyclerView.Adapter] for [MusicFile]s.
 *
 * @param dataSet The list of [MusicFile]s to display.
 * @param onSongClickListener The callback to be invoked when a song is clicked.
 */
class MusicFileAdapter(
    private var dataSet: List<MusicFile>,
    private val onSongClickListener: (Long) -> Unit,
) : RecyclerView.Adapter<MusicFileAdapter.ViewHolder>() {

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataSet[position].displayName
        holder.id = dataSet[position].id
    }

    override fun getItemCount() = dataSet.size

    /**
     * A [RecyclerView.ViewHolder] for [MusicFile]s.
     *
     * @param view The item holder [View].
     * @param onSongClickedListener The callback to be invoked when a song is clicked.
     */
    class ViewHolder(
        view: View,
        private val onSongClickedListener: (Long) -> Unit = { }
    ) : RecyclerView.ViewHolder(view) {

        val textView: TextView = view.findViewById(R.id.textView)
        var id by Delegates.notNull<Long>()

        init {
            textView.setOnClickListener {
                onSongClickedListener(id)
            }
        }

    }

}