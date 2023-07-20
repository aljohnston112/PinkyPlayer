package com.fourth_finger.pinky_player

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HiltTestActivity: AppCompatActivity(){

    val viewModel: ActivityMainViewModel by viewModels()

}
