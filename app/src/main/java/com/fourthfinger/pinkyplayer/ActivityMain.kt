package com.fourthfinger.pinkyplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.fourthfinger.pinkyplayer.databinding.ActivityMainBinding
import com.fourthfinger.pinkyplayer.playlists.DialogFragmentAddToPlaylist
import com.fourthfinger.pinkyplayer.songs.Song
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityMain : AppCompatActivity(), DialogFragmentAddToPlaylist.DialogFragmentAddToPlaylistListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDialogPositiveClick(dialog: String, song: Song) {
        TODO("Not yet implemented")
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        TODO("Not yet implemented")
    }

}