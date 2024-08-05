package com.andrea.pianocompanionroom.data

import com.andrea.pianocompanionroom.data.model.ArtistResults
import com.andrea.pianocompanionroom.data.model.RecordingResults
import com.andrea.pianocompanionroom.data.model.Releases
import com.andrea.pianocompanionroom.network.MusicDbApiService
import javax.inject.Inject


/**
 * Repository that interfaces with the music database.
 */
interface MusicDbRepository {
    suspend fun getArtistInfo(artistName: String): ArtistResults

    suspend fun queryForRecording(queryString: String): RecordingResults

    suspend fun getRecordingInfo(recordingMbid: String): Releases

}

class RemoteMusicDbRepository @Inject constructor(private val apiService: MusicDbApiService): MusicDbRepository {
    override suspend fun getArtistInfo(artistName: String): ArtistResults {
        return apiService.getArtistInfo(artistName)
    }

    // return a list of Recordings that match params in the queryString
    override suspend fun queryForRecording(queryString: String): RecordingResults {
        return apiService.queryForRecording(queryString)
    }

    // get the recording info associated with the MBID, including a list of releases associated
    // with the recording
    override suspend fun getRecordingInfo(recordingMbid: String): Releases {
        return apiService.getRecordingInfo(recordingMbid, "releases")
    }
}