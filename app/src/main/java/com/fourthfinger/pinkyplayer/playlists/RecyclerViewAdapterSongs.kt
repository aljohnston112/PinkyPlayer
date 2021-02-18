package com.fourthfinger.pinkyplayer.playlists

import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.songs.Song

interface ListenerCallbackSongs {
    fun onClickViewHolder(song: Song?)
    fun onMenuItemClickAddToPlaylist(song: Song?): Boolean
    fun onMenuItemClickAddToQueue(song: Song?): Boolean
}

class RecyclerViewAdapterSongs(
        private var listenerCallbackSongs: ListenerCallbackSongs,
) : RecyclerView.Adapter<RecyclerViewAdapterSongs.ViewHolder>() {

    private var songs: List<Song> = emptyList()

    fun updateList(songs: List<Song>) {
        this.songs = songs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_song, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.song = songs[position]
        holder.textViewSongName.text = songs[position].title

        // So the context menu opens
        holder.handle.setOnClickListener { holder.handle.performLongClick() }

        holder.songView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listenerCallbackSongs.onClickViewHolder(holder.song)
            }
        }

        holder.handle.setOnCreateContextMenuListener{
            menu: ContextMenu, _: View?, _: ContextMenuInfo? ->
            val menuItemAddToPlaylist: MenuItem = menu.add(R.string.add_to_playlist)
            menuItemAddToPlaylist.setOnMenuItemClickListener {
                listenerCallbackSongs.onMenuItemClickAddToPlaylist(holder.song)
            }
            val menuItemAddToQueue: MenuItem = menu.add(R.string.add_to_queue)
            menuItemAddToQueue.setOnMenuItemClickListener {
                listenerCallbackSongs.onMenuItemClickAddToQueue(holder.song)
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.handle.setOnCreateContextMenuListener(null)
        holder.handle.setOnClickListener(null)
        holder.songView.setOnClickListener(null)
        holder.song = null
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    class ViewHolder(val songView: View) : RecyclerView.ViewHolder(songView) {
        val textViewSongName: TextView = songView.findViewById(R.id.text_view_songs_name)
        val handle: ImageView = songView.findViewById(R.id.song_handle)
        var song: Song? = null

        override fun toString(): String {
            return song!!.title
        }

    }

}