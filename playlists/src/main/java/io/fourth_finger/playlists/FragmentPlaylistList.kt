package io.fourth_finger.playlists

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
import io.fourth_finger.playlists.databinding.FragmentPlaylistListBinding
import kotlinx.coroutines.launch

/**
 * A [Fragment] that displays a list of [PlaylistItem]s.
 */
@AndroidEntryPoint
class FragmentPlaylistList : Fragment() {

    private var _binding: FragmentPlaylistListBinding? = null
    private val binding get() = _binding!!

    private var _searchView: SearchView? = null
    private val searchView get() = _searchView!!

    private val viewModel: FragmentPlaylistViewModel by viewModels()

    private val menuProvider = object : MenuProvider {

        private val queryTextListener = object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText != "") {
                    viewModel.newSearch(newText)
                }
                return true
            }

        }

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.fragment_playlist_list_menu, menu)
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
        _binding = FragmentPlaylistListBinding.inflate(inflater, container, false)
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
        val adapter = PlaylistAdapter(emptyList()) { playlistID ->
            // Callback for when a playlist item is tapped
            viewModel.playlistItemClicked(
                requireContext(),
                playlistID
            )
        }
        binding.recyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.recycleChildrenOnDetach = true
        binding.recyclerView.layoutManager = linearLayoutManager

        // Set up playlist list updates

        lifecycleScope.launch {
            viewModel.playlistItems.collect { playlistItems ->
                playlistItems.let {
                    binding.recyclerView.post {
                        adapter.updatePlaylistList(playlistItems)
                    }
                }
            }
        }
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