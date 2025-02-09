package io.fourth_finger.playlists

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.fourth_finger.playlist_repository.PlaylistItem
import kotlin.properties.Delegates

class PlaylistAdapter(
    private var playlistItems: List<PlaylistItem>,
    private val onPlaylistClickListener: (Int) -> Unit
): RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    /**
     * Updates the adapter with a new list of [PlaylistItem]s.
     *
     * @param music The new list of [PlaylistItem]s.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updatePlaylistList(music: List<PlaylistItem>) {
        this.playlistItems = music
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.playlist_holder,
                parent,
                false
            )
        )    }

    override fun getItemCount() = playlistItems.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.id = playlistItems[position].id
        holder.textView.text = playlistItems[position].name
        holder.textView.setOnClickListener {
            onPlaylistClickListener(holder.id)
        }
    }

    /**
     * A [RecyclerView.ViewHolder] for [PlaylistItem]s.
     *
     * @param view The item holder [View].
     */
    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        val textView: TextView = view.findViewById(R.id.textView)
        var id by Delegates.notNull<Int>()

    }

}