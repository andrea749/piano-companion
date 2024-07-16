package com.andrea.pianocompanionroom

import android.app.Application
import com.andrea.pianocompanionroom.ble.BLE
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SongsApplication : Application() {
    lateinit var ble: BLE

    override fun onCreate() {
        super.onCreate()
        ble = BLE(this.applicationContext)
    }
}
