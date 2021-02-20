package com.fourthfinger.pinkyplayer.settings

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourthfinger.pinkyplayer.ActivityMain
import com.fourthfinger.pinkyplayer.R
import com.fourthfinger.pinkyplayer.databinding.FragmentTitleBinding
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SettingViewModelTest : Fragment() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val viewModelSettings: SettingsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private val countDownLatchFragmentLoaded: CountDownLatch = CountDownLatch(2)

    @Test
    fun viewModel() {
        val countDownLatchSettingLoaded: CountDownLatch = CountDownLatch(1)
        val countDownLatchSettingSaved: CountDownLatch = CountDownLatch(1)
        val countDownLatchSettingSaved2: CountDownLatch = CountDownLatch(1)
        val startActivityIntent = Intent.makeMainActivity(
                ComponentName(
                        ApplicationProvider.getApplicationContext(),
                        ActivityMain::class.java
                )
        )
        ActivityScenario.launch<ActivityMain>(startActivityIntent).onActivity { activity ->
            val navHostFragment = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val fragmentTransaction: FragmentTransaction = navHostFragment.childFragmentManager.beginTransaction()
            fragmentTransaction.add(this, "")
            fragmentTransaction.commit()
        }
        countDownLatchFragmentLoaded.await()
        viewModelSettings.loadSettings()
        lifecycleScope.launch {
            viewModelSettings.isLoaded.observe(viewLifecycleOwner) {
                if (it == true) {
                    assert(viewModelSettings.settings.value != null)
                    countDownLatchSettingLoaded.countDown()
                }
            }
        }
        countDownLatchSettingLoaded.await()
        val settings = Settings(0.0, 1.0, 2.0, 3.0)
        viewModelSettings.save(settings)
        var done = false
        lifecycleScope.launch {
            viewModelSettings.settings.observe(viewLifecycleOwner) {
                if (!done) {
                    if(it == settings){
                        countDownLatchSettingSaved.countDown()
                    }
                    done = true
                } else {
                    assert(it == settings)
                    countDownLatchSettingSaved.countDown()
                }
                viewModelSettings.settings.removeObservers(viewLifecycleOwner)
            }
        }
        countDownLatchSettingSaved.await()
        val settings2 = Settings(0.01, 0.5, 0.9, 0.01)
        viewModelSettings.save(settings2)
        done = false
        lifecycleScope.launch {
            viewModelSettings.settings.observe(viewLifecycleOwner) {
                if (!done) {
                    if(it == settings2){
                        countDownLatchSettingSaved2.countDown()
                    }
                    done = true
                } else {
                    assert(it == settings2)
                    countDownLatchSettingSaved2.countDown()
                }
            }
            viewModelSettings.settings.removeObservers(viewLifecycleOwner)
        }
        countDownLatchSettingSaved2.await()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        countDownLatchFragmentLoaded.countDown()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentTitleBinding.inflate(layoutInflater).root
    }

    override fun onStart() {
        super.onStart()
        countDownLatchFragmentLoaded.countDown()
    }

}