package com.andrea.pianocompanionroom.data

import kotlinx.coroutines.flow.Flow

class OfflineSongsRepository(private val songDao: SongDao) : SongsRepository {
    override fun getAllSongsStream(): Flow<List<Song>> = songDao.getAllSongs()

    override fun getSongStream(id: Int): Flow<Song?> = songDao.getSong(id)

    override suspend fun insertSong(song: Song) = songDao.insert(song)

    override suspend fun deleteSong(song: Song) = songDao.delete(song)

    override suspend fun updateSong(song: Song) = songDao.update(song)
}
