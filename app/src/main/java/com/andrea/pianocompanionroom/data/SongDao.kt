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
    fun getItem(id: Int): Flow<Song>

    @Query("SELECT * from Songs ORDER BY name ASC")
    fun getAllItems(): Flow<List<Song>>
}