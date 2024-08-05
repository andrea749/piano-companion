package com.andrea.pianocompanionroom

import android.content.ContentResolver
import android.content.Context
import com.andrea.pianocompanionroom.data.OfflineSongsRepository
import com.andrea.pianocompanionroom.data.RemoteMusicDbRepository
import com.andrea.pianocompanionroom.data.SongInventoryDatabase
import com.andrea.pianocompanionroom.data.SongsRepository
import com.andrea.pianocompanionroom.network.MusicDbApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Provides
    fun providesContentResolver(
        @ApplicationContext context: Context
    ): ContentResolver {
        return context.contentResolver
    }

    @Singleton
    @Provides
    fun providesRemoteMusicDbRepository(): RemoteMusicDbRepository {
        val BASE_URL = "https://musicbrainz.org/ws/2/"
        val logging = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
        return RemoteMusicDbRepository(retrofit.create(MusicDbApiService::class.java))
    }
}