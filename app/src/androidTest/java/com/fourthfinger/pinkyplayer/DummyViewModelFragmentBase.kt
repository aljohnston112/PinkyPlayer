package com.fourthfinger.pinkyplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fourthfinger.pinkyplayer.databinding.ToastBinding
import java.util.concurrent.CountDownLatch

open class DummyViewModelFragmentBase(val fragmentLoaded: CountDownLatch) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ToastBinding.inflate(layoutInflater).root
    }

    override fun onStart() {
        super.onStart()
        fragmentLoaded.countDown()
    }

}