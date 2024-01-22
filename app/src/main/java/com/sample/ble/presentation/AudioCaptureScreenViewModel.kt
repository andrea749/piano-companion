package com.sample.ble.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AudioCaptureScreenViewModel @Inject constructor(): ViewModel() {
    fun turnOnRecorder() {
        AudioCaptureModel.beginCapture()
    }
    // note: audio stream info needs to be passed to the view via LiveData object
}

