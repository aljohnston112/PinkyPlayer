package com.fourthfinger.pinkyplayer.settings

import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.*
import com.fourthfinger.pinkyplayer.songs.SongDBBaseTest.Companion.loadingCallback
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SettingsViewModelTest : ViewModelBaseTest(DummySettingsViewModelFragment()) {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun viewModel() {
        fragment as DummySettingsViewModelFragment
        val viewModelSettings = fragment.viewModel
        val viewModelScope = viewModelSettings.viewModelScope
        val viewLifecycleOwner = fragment.viewLifecycleOwner
        viewModelSettings.loadSettings(loadingCallback)
        val countDownLatchSettingLoaded = CountDownLatch(1)
        viewModelScope.launch {
            loadingCallback.settingsLoaded.observe(viewLifecycleOwner) {
                if (it == true) {
                    assert(viewModelSettings.settings.value != null)
                    countDownLatchSettingLoaded.countDown()
                }
            }
        }
        countDownLatchSettingLoaded.await()
        val settings = Settings(0.0, 1.0, 2.0, 3.0)
        viewModelSettings.save(settings)
        LiveDataTestUtil.checkLiveDataUpdate(
                viewModelScope, viewLifecycleOwner,
                viewModelSettings.settings, settings)
        val settings2 = Settings(0.01, 0.5, 0.9, 0.01)
        viewModelSettings.save(settings2)
        LiveDataTestUtil.checkLiveDataUpdate(
                viewModelScope, viewLifecycleOwner,
                viewModelSettings.settings, settings2)
    }

}