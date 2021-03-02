package com.fourthfinger.pinkyplayer.playlists

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.fourthfinger.pinkyplayer.DummyViewModelFragmentBase
import com.fourthfinger.pinkyplayer.R
import java.util.concurrent.CountDownLatch

class DummyMediaViewModelFragment: DummyViewModelFragmentBase(CountDownLatch(1)) {

    val mediaViewModel: MediaViewModel by hiltNavGraphViewModels(R.id.nav_graph)

}