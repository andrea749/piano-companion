package com.andrea.pianocompanionroom

import android.content.Context
import com.andrea.pianocompanionroom.data.OfflineSongsRepository
import com.andrea.pianocompanionroom.data.SongInventoryDatabase
import com.andrea.pianocompanionroom.data.SongsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providesSongsRepository(
        @ApplicationContext context: Context
    ): SongsRepository {
        return OfflineSongsRepository(SongInventoryDatabase.getDatabase(context).songDao())
    }
}