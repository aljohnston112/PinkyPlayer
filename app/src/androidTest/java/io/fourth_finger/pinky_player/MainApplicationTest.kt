package io.fourth_finger.pinky_player

import android.app.ActivityManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Test

class MainApplicationTest {

    @Test
    fun onCreate_startsServiceMediaBrowser(){
        val app = ApplicationProvider.getApplicationContext<MainApplication>()
        app.onCreate()
        val activityManager = app.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for(service in activityManager.getRunningServices(1)){
            assert(service.service.className == ServiceMediaBrowser::class.java.name)
            assert(service.started)
        }
    }

}