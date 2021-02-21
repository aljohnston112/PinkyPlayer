package com.fourthfinger.pinkyplayer

import android.content.ComponentName
import android.content.Intent
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.Before

open class ViewModelBaseTest(val fragment : DummyViewModelFragmentBase) {

    @Before
    fun setUp() {
        val startActivityIntent = Intent.makeMainActivity(
                ComponentName(
                        ApplicationProvider.getApplicationContext(),
                        ActivityMain::class.java
                )
        )
        ActivityScenario.launch<ActivityMain>(startActivityIntent).onActivity { activity ->
            val navHostFragment = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val fragmentTransaction: FragmentTransaction = navHostFragment.childFragmentManager.beginTransaction()
            fragmentTransaction.add(fragment, "")
            fragmentTransaction.commit()
        }
        fragment.fragmentLoaded.await()
    }

}
