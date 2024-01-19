package io.fourth_finger.pinky_player

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.fourth_finger.music_repository.MusicFile
import kotlinx.coroutines.launch

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
        val application = requireActivity().application as ApplicationMain

        // Sets up the adapter
        val adapter = MusicFileAdapter(emptyList()) {
            // Callback for when a song item is tapped
            songID ->
            lifecycleScope.launch {
                viewModel.songClicked(songID, application.getMediaBrowser())
            }
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager

        // Set up music list updates
        viewModel.musicFiles.observe(viewLifecycleOwner) { musicFiles ->
            adapter.updateMusicList(musicFiles.toList())
        }
        // [FragmentTitle] must guarantee permissions are granted before launching this Fragment
        // Permission must be granted before [loadMusic] is run
        viewModel.loadMusic(requireActivity().contentResolver)
    }

}