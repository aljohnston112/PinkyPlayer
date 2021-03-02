package com.fourthfinger.pinkyplayer.playlists

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.fourthfinger.pinkyplayer.DummyViewModelFragmentBase
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.settings.SettingsViewModel
import java.util.concurrent.CountDownLatch

class DummyPlaylistsViewModelFragment: DummyViewModelFragmentBase(CountDownLatch(1)) {

    val viewModelPlaylists: PlaylistsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    val viewModelSettings: SettingsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

}