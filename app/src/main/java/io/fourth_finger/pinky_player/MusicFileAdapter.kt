package io.fourth_finger.pinky_player

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
            ),
            onSongClickListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = buildString {
            append(music[position].relativePath)
            append(music[position].displayName)
        }
        holder.id = music[position].id
    }

    override fun getItemCount() = music.size

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