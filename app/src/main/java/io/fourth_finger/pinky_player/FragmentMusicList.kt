package io.fourth_finger.pinky_player

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.fourth_finger.music_repository.MusicFile

/**
 * A [Fragment] that displays a list of [MusicFile]s.
 */
class FragmentMusicList : Fragment() {

    private val viewModel: ActivityMainViewModel by activityViewModels(
        factoryProducer = { ActivityMainViewModel.Factory }
    )

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView(view)
    }

    /**
     * Sets up the RecyclerView.
     */
    private fun setUpRecyclerView(view: View) {
        val adapter = MusicFileAdapter(emptyList()) { id ->
            val controls =
                MediaControllerCompat.getMediaController(requireActivity()).transportControls
            viewModel.songClicked(id, controls)
        }
        val rv = view.findViewById<RecyclerView>(R.id.recycler_view)
        rv.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv.layoutManager = linearLayoutManager

        // Set up updates
        viewModel.fetchMusicFiles(requireContext().contentResolver)
        viewModel.musicFiles.observe(viewLifecycleOwner) { musicFiles ->
            adapter.updateMusicList(musicFiles.toList())
        }
    }

}