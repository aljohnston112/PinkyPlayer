package io.fourth_finger.music_list_fragment

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.fourth_finger.event_processor.EventProcessor
import io.fourth_finger.music_list_fragment.databinding.FragmentMusicListBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.toList
import kotlin.getValue

/**
 * A [androidx.fragment.app.Fragment] that displays a list of [io.fourth_finger.music_repository.MusicItem]s.
 */
@AndroidEntryPoint
class FragmentMusicList : Fragment() {

    @Inject
    lateinit var eventProcessor: EventProcessor

    private var _binding: FragmentMusicListBinding? = null
    private val binding get() = _binding!!

    private var _searchView: SearchView? = null
    private val searchView get() = _searchView!!

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
                if (newText.isNotEmpty()) {
                    viewModel.newSearch(newText)
                } else {
                    job = viewLifecycleOwner.lifecycleScope.launch {
                        (binding.recyclerView.adapter as MusicFileAdapter).updateMusicList(
                            viewModel.musicItems.value!!
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
        val adapter = MusicFileAdapter(emptyList()) { songID ->
            // Callback for when a song item is tapped
            eventProcessor.songClicked(
                requireContext(),
                songID
            )
        }
        binding.recyclerView.adapter = adapter
        viewModel.musicItems.observe(viewLifecycleOwner){ songs ->
            lifecycleScope.launch {
                (binding.recyclerView.adapter as MusicFileAdapter).updateMusicList(
                    songs
                )
            }
        }

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.recycleChildrenOnDetach = true
        binding.recyclerView.layoutManager = linearLayoutManager

        // Set up music list updates
        viewModel.musicItems.observe(viewLifecycleOwner) { musicFiles ->
            musicFiles?.let {
                binding.recyclerView.post {
                    adapter.updateMusicList(musicFiles.toList())
                }
            }
        }
        // [FragmentTitle] must guarantee permissions are granted
        // before launching this Fragment
        // Permission must be granted before [loadMusic] is run
        // activityMainViewModel.loadMusic(requireActivity().contentResolver)
        // TODO should not be needed
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