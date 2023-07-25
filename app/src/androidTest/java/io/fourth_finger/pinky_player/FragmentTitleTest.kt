package io.fourth_finger.pinky_player

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import org.junit.Test

class FragmentTitleTest {

    @Test
    fun testNavigationToInGameScreen() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        val titleScenario = launchFragmentInContainer<FragmentTitle>()

        titleScenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(ViewMatchers.withId(R.id.button_songs)).perform(ViewActions.click())
        assert(navController.currentDestination?.id == R.id.fragmentMusicList)
    }

}