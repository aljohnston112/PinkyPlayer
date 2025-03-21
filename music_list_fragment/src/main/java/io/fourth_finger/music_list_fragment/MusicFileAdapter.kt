package io.fourth_finger.music_list_fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.fourth_finger.music_repository.MusicItem
import kotlin.properties.Delegates

/**
 * A [androidx.recyclerview.widget.RecyclerView.Adapter] for [io.fourth_finger.music_repository.MusicItem]s.
 *
 * @param music The list of [io.fourth_finger.music_repository.MusicItem]s to display.
 * @param onSongClickListener The callback to be invoked when a song is clicked.
 */
class MusicFileAdapter(
    private var music: List<MusicItem>,
    private val onSongClickListener: (Long) -> Unit
) : RecyclerView.Adapter<MusicFileAdapter.ViewHolder>() {

    /**
     * Updates the adapter with a new list of [io.fourth_finger.music_repository.MusicItem]s.
     *
     * @param music The new list of [io.fourth_finger.music_repository.MusicItem]s.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(music: List<MusicItem>) {
        this.music = music
        notifyDataSetChanged()
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.id = music[position].id
        holder.textView.text = music[position].fullPath
        holder.textView.setOnClickListener {
            onSongClickListener(holder.id)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.textView.setOnClickListener(null)
    }

    override fun getItemCount() = music.size

    /**
     * A [androidx.recyclerview.widget.RecyclerView.ViewHolder] for [io.fourth_finger.music_repository.MusicItem]s.
     *
     * @param view The item holder [android.view.View].
     */
    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        val textView: TextView = view.findViewById(R.id.textView)
        var id by Delegates.notNull<Long>()

    }

}