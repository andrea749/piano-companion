package com.andrea.pianocompanionroom.data

import com.andrea.pianocompanionroom.data.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Song] from a given data source.
 */
interface SongsRepository {
    /**
     * Retrieve all the songs from the the given data source.
     */
    fun getAllSongsStream(): Flow<List<Song>>

    /**
     * Retrieve all the songs from the the given data source that match a search key.
     */
    fun getFilteredSongsStream(searchKey: String): Flow<List<Song>>

    /**
     * Retrieve a song from the given data source that matches with the [id].
     */
    fun getSongStream(id: Int): Flow<Song?>

    /**
     * Retrieve all songs from the given data source that starts with the [key].
     */
    fun getSongStream(key: String): Flow<Song?>

    /**
     * Insert song in the data source
     */
    suspend fun insertSong(song: Song)

    /**
     * Delete song from the data source
     */
    suspend fun deleteSong(song: Song)

    /**
     * Update song in the data source
     */
    suspend fun updateSong(song: Song)
}