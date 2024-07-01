package com.andrea.pianocompanionroom.data
import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val songsRepository: SongsRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val songsRepository: SongsRepository by lazy {
        OfflineSongsRepository(SongInventoryDatabase.getDatabase(context).songDao())
    }
}