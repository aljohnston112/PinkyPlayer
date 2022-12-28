package com.fourth_finger.pinky_player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fourth_finger.music_repository.MusicFile

/**
 * The [RecyclerView.Adapter] for [MusicFile]s.
 */
class MusicFileAdapter(
    private var dataSet: List<MusicFile>
) : RecyclerView.Adapter<MusicFileAdapter.ViewHolder>() {

    /**
     * Updates the adapter with a new [List<MusicFile>].
     *
     * @param music The new list of [MusicFile]s.
     */
    fun updateMusicList(music: List<MusicFile>){
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
            )
        )
    }

    /**
     * Sets the [ViewHolder]'s text to the display name.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataSet[position].displayName
    }

    /**
     * Gets the number of items in this [MusicFileAdapter].
     */
    override fun getItemCount() = dataSet.size

    /**
     * The [RecyclerView.ViewHolder] for [MusicFile]s.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
    }

}