package com.fourthfinger.pinkyplayer.playlists

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.fourthfinger.pinkyplayer.DummyViewModelFragmentBase
import com.fourthfinger.pinkyplayer.R
import java.util.concurrent.CountDownLatch

class DummyPlaylistsViewModelFragment: DummyViewModelFragmentBase(CountDownLatch(1)) {

    val viewModel: PlaylistsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

}