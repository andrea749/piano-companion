package com.andrea.pianocompanionroom.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineSongsRepository @Inject constructor(private val songDao: SongDao) : SongsRepository {
    override fun getAllSongsStream(): Flow<List<Song>> = songDao.getAllSongs()
    override fun getFilteredSongsStream(searchKey: String): Flow<List<Song>> = songDao.getFilteredSongs(searchKey)

    override fun getSongStream(id: Int): Flow<Song?> = songDao.getSong(id)

    override fun getSongStream(key: String): Flow<Song?> = songDao.getSong(key)

    override suspend fun insertSong(song: Song) = songDao.insert(song)

    override suspend fun deleteSong(song: Song) = songDao.delete(song)

    override suspend fun updateSong(song: Song) = songDao.update(song)
}
