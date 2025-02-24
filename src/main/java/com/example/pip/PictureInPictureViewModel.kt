package com.example.pip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PictureInPictureViewModel : ViewModel() {

    var value by mutableIntStateOf(0)

}
