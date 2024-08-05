package com.andrea.pianocompanionroom.network

import com.andrea.pianocompanionroom.data.model.ArtistResults
import com.andrea.pianocompanionroom.data.model.RecordingResults
import com.andrea.pianocompanionroom.data.model.Releases
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface MusicDbApiService {
    @Headers(
        "Accept: application/json",
        "User-Agent: Piano Companion/0.0.0 ( http://github.com/andea749 )")
    @GET("artist/")
    suspend fun getArtistInfo(@Query("query") artistName: String): ArtistResults

    @Headers(
        "Accept: application/json",
        "User-Agent: Piano Companion/0.0.0 ( http://github.com/andea749 )")
    @GET("recording/")
    suspend fun queryForRecording(@Query("query") completeQueryString: String): RecordingResults

    @Headers(
        "Accept: application/json",
        "User-Agent: Piano Companion/0.0.0 ( http://github.com/andea749 )")
    @GET("recording/{songId}")
    suspend fun getRecordingInfo(
        @Path("songId") songId: String,
        @Query("inc") includeParam: String
    ): Releases
}
