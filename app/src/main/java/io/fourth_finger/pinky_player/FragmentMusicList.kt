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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.fourth_finger.music_repository.MusicFile
import io.fourth_finger.pinky_player.databinding.FragmentMusicListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A [Fragment] that displays a list of [MusicFile]s.
 */
@AndroidEntryPoint
class FragmentMusicList : Fragment() {

    @Inject
    lateinit var mediaBrowserProvider: MediaBrowserProvider

    private var _binding: FragmentMusicListBinding? = null
    private val binding get() = _binding!!

    private var _searchView: SearchView? = null
    private val searchView get() = _searchView!!

    private val activityMainViewModel: ActivityMainViewModel by activityViewModels()
    private val viewModel: FragmentMusicListViewModel by viewModels()

    private val menuProvider = object : MenuProvider {

        private val queryTextListener = object : SearchView.OnQueryTextListener {

            private var job: Job = Job()

            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (job.isActive) {
                    job.cancel()
                }
                if (newText != "") {
                    viewModel.newSearch(newText)
                    job = viewLifecycleOwner.lifecycleScope.launch {
                        val songs = withContext(Dispatchers.Default) { getSongsWithText(newText) }
                        (binding.recyclerView.adapter as MusicFileAdapter).updateMusicList(
                            songs
                        )
                    }
                } else {
                    job = viewLifecycleOwner.lifecycleScope.launch {
                        (binding.recyclerView.adapter as MusicFileAdapter).updateMusicList(
                            activityMainViewModel.musicFiles.value!!
                        )
                    }
                }
                return true
            }

        }

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.fragment_song_list_menu, menu)
            val searchItem = menu.findItem(R.id.action_search)
            _searchView = searchItem?.actionView as SearchView
            searchView.setOnQueryTextListener(queryTextListener)
            setSavedSearchText(searchItem, searchView)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return false
        }


    }

    private fun setSavedSearchText(searchItem: MenuItem, searchView: SearchView) {
        viewModel.getSavedSearchText()?.let {
            searchView.isIconified = true
            searchItem.expandActionView()
            searchView.setQuery(it, true)
            searchView.isFocusable = true
        }
    }

    private fun getSongsWithText(newText: String): List<MusicFile> {
        val allMusic = activityMainViewModel.musicFiles.value!!
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
        // Set up the adapter
        val adapter = MusicFileAdapter(emptyList()) {
            // Callback for when a song item is tapped
                songID ->
            mediaBrowserProvider.invokeOnConnection(Dispatchers.Main.immediate) { mediaBrowser ->
                activityMainViewModel.songClicked(requireContext(), songID, mediaBrowser)
            }
        }
        binding.recyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.recycleChildrenOnDetach = true
        binding.recyclerView.layoutManager = linearLayoutManager

        // Set up music list updates
        activityMainViewModel.musicFiles.observe(viewLifecycleOwner) { musicFiles ->
            musicFiles?.let {
                binding.recyclerView.post {
                    adapter.updateMusicList(musicFiles.toList())
                }
            }
        }
        // [FragmentTitle] must guarantee permissions are granted before launching this Fragment
        // Permission must be granted before [loadMusic] is run
        activityMainViewModel.loadMusic(requireActivity().contentResolver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().removeMenuProvider(menuProvider)
        searchView.setOnQueryTextListener(null)
        _searchView = null
        binding.recyclerView.adapter = null
        _binding = null
    }

}