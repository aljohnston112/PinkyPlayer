package com.fourthfinger.pinkyplayer

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment.findNavController

class NavUtil {

    companion object {

        fun safeNav(fragment: Fragment, currentFragmentID: Int, action: NavDirections) {
            val navController = findNavController(fragment)
            val dest = navController.currentDestination
            if (dest != null) {
                if (dest.id == currentFragmentID) {
                    navController.navigate(action)
                }
            }
        }

    }

}