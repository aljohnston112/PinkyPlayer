package io.fourth_finger.pinky_player

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.fourth_finger.music_repository.MusicFile
import kotlin.properties.Delegates

/**
 * A [RecyclerView.Adapter] for [MusicFile]s.
 *
 * @param music The list of [MusicFile]s to display.
 * @param onSongClickListener The callback to be invoked when a song is clicked.
 */
class MusicFileAdapter(
    private var music: List<MusicFile>,
    private val onSongClickListener: (Long) -> Unit
) : RecyclerView.Adapter<MusicFileAdapter.ViewHolder>() {

    /**
     * Updates the adapter with a new list of [MusicFile]s.
     *
     * @param music The new list of [MusicFile]s.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(music: List<MusicFile>) {
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
        holder.textView.text = buildString {
            append(music[position].relativePath)
            append(music[position].displayName)
        }
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
     * A [RecyclerView.ViewHolder] for [MusicFile]s.
     *
     * @param view The item holder [View].
     */
    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        val textView: TextView = view.findViewById(R.id.textView)
        var id by Delegates.notNull<Long>()


    }

}