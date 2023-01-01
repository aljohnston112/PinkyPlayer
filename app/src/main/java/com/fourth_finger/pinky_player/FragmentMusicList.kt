package com.fourth_finger.pinky_player

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fourth_finger.music_repository.MusicFile

/**
 * A [Fragment] that displays a list of [MusicFile]s.
 */
class FragmentMusicList : Fragment() {

    private val viewModel: FragmentMusicListViewModel by viewModels{
        FragmentMusicListViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_music_list,
            container,
            false
        )
    }

    /**
     * Sets up the RecyclerView and updates to it.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the RecyclerView
        val adapter = MusicFileAdapter(R.layout.music_file_holder, emptyList())
        val rv = view.findViewById<RecyclerView>(R.id.recycler_view)
        rv.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv.layoutManager = linearLayoutManager

        // Set up updates
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            adapter.updateMusicList(uiState.musicFiles)
        }

    }
}
