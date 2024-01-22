package io.fourth_finger.pinky_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.pinky_player.databinding.FragmentMusicListBinding
import io.fourth_finger.pinky_player.databinding.FragmentTitleBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A [Fragment] that displays a list of [MusicFile]s.
 */
class FragmentMusicList : Fragment() {

    private var _binding: FragmentMusicListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ActivityMainViewModel by activityViewModels(
        factoryProducer = { ActivityMainViewModel.Factory }
    )

    private val menuProvider = object : MenuProvider {

        private val queryTextListener = object : SearchView.OnQueryTextListener {

            var job: Job? = null

            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (job?.isActive == true) {
                    job?.cancel()
                }
                job = viewLifecycleOwner.lifecycleScope.launch {
                    val songs = withContext(Dispatchers.IO) { getSongsWithText(newText) }
                    (binding.recyclerView.adapter as MusicFileAdapter).updateMusicList(
                        songs
                    )
                }
                return true
            }

        }

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.fragment_song_list_menu, menu)
            val searchItem = menu.findItem(R.id.action_search)
            val searchView = searchItem?.actionView as SearchView
            searchView.setOnQueryTextListener(queryTextListener)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return false
        }

    }

    private fun getSongsWithText(newText: String): List<MusicFile> {
        val allMusic = viewModel.musicFiles.value!!
        val sifted: MutableList<MusicFile> = mutableListOf()
        for (song in allMusic) {
            if (song.fullPath.lowercase().contains(newText.lowercase())) {
                sifted.add(song)
            }
        }
        return sifted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().addMenuProvider(
            menuProvider,
            this,
            Lifecycle.State.RESUMED
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    /**
     * Sets up the RecyclerView.
     */
    private fun setUpRecyclerView() {
        val application = requireActivity().application as ApplicationMain

        // Sets up the adapter
        val adapter = MusicFileAdapter(emptyList()) {
            // Callback for when a song item is tapped
                songID ->
            lifecycleScope.launch {
                viewModel.songClicked(requireContext(), songID, application.getMediaBrowser())
            }
        }
        binding.recyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.recycleChildrenOnDetach = true
        binding.recyclerView.layoutManager = linearLayoutManager

        // Set up music list updates
        viewModel.musicFiles.observe(viewLifecycleOwner) { musicFiles ->
            binding.recyclerView.post {
                adapter.updateMusicList(musicFiles.toList())
            }
        }
        // [FragmentTitle] must guarantee permissions are granted before launching this Fragment
        // Permission must be granted before [loadMusic] is run
        viewModel.loadMusic(requireActivity().contentResolver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().removeMenuProvider(menuProvider)
        binding.recyclerView.adapter = null
        _binding = null
    }

}