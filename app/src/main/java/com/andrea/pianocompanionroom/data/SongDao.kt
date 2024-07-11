package com.andrea.pianocompanionroom.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) // ignore conflicting item
    suspend fun insert(song: Song)

    @Update
    suspend fun update(song: Song)

    @Delete
    suspend fun delete(song: Song)

    @Query("SELECT * from Songs WHERE id = :id")
    fun getSong(id: Int): Flow<Song>

    @Query("SELECT * from Songs WHERE name = :song")
    fun getSong(song: String): Flow<Song>

    @Query("SELECT * from Songs WHERE name LIKE :s OR artist LIKE :s ORDER BY name ASC")
    fun getFilteredSongs(s: String): Flow<List<Song>>

    @Query("SELECT * from Songs ORDER BY name ASC")
    fun getAllSongs(): Flow<List<Song>>
}