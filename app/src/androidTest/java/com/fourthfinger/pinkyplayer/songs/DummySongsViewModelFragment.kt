package com.fourthfinger.pinkyplayer.songs

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.fourthfinger.pinkyplayer.DummyViewModelFragmentBase
import com.fourthfinger.pinkyplayer.R
import java.util.concurrent.CountDownLatch

class DummySongsViewModelFragment(fragmentLoaded: CountDownLatch) : DummyViewModelFragmentBase(fragmentLoaded) {

    val viewModel: SongsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

}