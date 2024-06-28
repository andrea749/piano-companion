package com.andrea.pianocompanionroom.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Song::class], version = 1, exportSchema = false)
@TypeConverters(MidiEventDataConverter::class)
abstract class SongInventoryDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

    companion object {
        @Volatile // all read/writes are to/from main memory to maintain consistency across threads
        private var Instance: SongInventoryDatabase? = null
        /**
         * for info on "fallbackToDestructiveMigration":
        *  https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
        * */
        fun getDatabase(context: Context): SongInventoryDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SongInventoryDatabase::class.java, "song_database")
                    .fallbackToDestructiveMigration() // when schema changes, destroy prev database and rebuild
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
