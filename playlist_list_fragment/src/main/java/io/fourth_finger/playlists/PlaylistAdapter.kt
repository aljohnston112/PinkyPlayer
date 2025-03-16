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
) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    /**
     * Updates the adapter with a new list of [io.fourth_finger.playlist_repository.PlaylistItem]s.
     *
     * @param music The new list of [io.fourth_finger.playlist_repository.PlaylistItem]s.
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
        val attachToRoot = false
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.playlist_holder,
                parent,
                attachToRoot
            )
        )
    }

    override fun getItemCount() = playlistItems.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val playlistItem = playlistItems[position]
        holder.id = playlistItem.hash
        holder.textView.text = playlistItem.name
        holder.textView.setOnClickListener {
            onPlaylistClickListener(holder.id)
        }
    }

    /**
     * A [RecyclerView.ViewHolder] for [io.fourth_finger.playlist_repository.PlaylistItem]s.
     *
     * @param view The item holder's [View].
     */
    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
        var id by Delegates.notNull<Int>()
    }

}