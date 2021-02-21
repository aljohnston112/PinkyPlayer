package com.fourthfinger.pinkyplayer.settings

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.fourthfinger.pinkyplayer.DummyViewModelFragmentBase
import com.fourthfinger.pinkyplayer.R
import java.util.concurrent.CountDownLatch

class DummySettingsViewModelFragment(fragmentLoaded: CountDownLatch) : DummyViewModelFragmentBase(fragmentLoaded) {

    val viewModel: SettingsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

}