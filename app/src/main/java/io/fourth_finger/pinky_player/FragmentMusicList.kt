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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A [Fragment] that displays a list of [MusicFile]s.
 */
class FragmentMusicList : Fragment() {

    private val viewModel: ActivityMainViewModel by activityViewModels(
        factoryProducer = { ActivityMainViewModel.Factory }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().addMenuProvider(
            object : MenuProvider {

                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.fragment_song_list_menu, menu)
                    val searchItem = menu.findItem(R.id.action_search)
                    val searchView = searchItem?.actionView as SearchView
                    searchView.setOnSearchClickListener {
                    }
                    searchView.setOnQueryTextListener(
                        object : SearchView.OnQueryTextListener {

                            var job: Job? = null

                            override fun onQueryTextSubmit(query: String?): Boolean {
                                return true
                            }

                            override fun onQueryTextChange(newText: String): Boolean {
                                if (job?.isActive == true) {
                                    job?.cancel()
                                }
                                job = lifecycleScope.launch {
                                    val allMusic = viewModel.musicFiles.value!!
                                    val sifted: MutableList<MusicFile> = mutableListOf()
                                    for (song in allMusic) {
                                        if ((song.relativePath + song.displayName)
                                                .lowercase()
                                                .contains(
                                                    newText.lowercase()
                                                )
                                        ) {
                                            sifted.add(song)
                                        }
                                    }
                                    val recyclerView = requireView().findViewById<RecyclerView>(
                                        R.id.recycler_view
                                    )
                                    (recyclerView.adapter as MusicFileAdapter).updateMusicList(
                                        sifted.toList()
                                    )
                                }
                                return true
                            }

                        },
                    )
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return false
                }

            },
            this,
            Lifecycle.State.RESUMED
        )
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
                viewModel.songClicked(requireContext(), songID, application.getMediaBrowser())
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