package com.andrea.pianocompanionroom

import android.app.Application
import com.andrea.pianocompanionroom.data.AppContainer
import com.andrea.pianocompanionroom.data.AppDataContainer

class SongsApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}