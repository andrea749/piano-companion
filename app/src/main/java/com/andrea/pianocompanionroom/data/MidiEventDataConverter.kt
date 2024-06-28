package com.andrea.pianocompanionroom.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 * converters for [Song.notes] to store in the db
 */
class MidiEventDataConverter{
    private val gson by lazy { Gson() }
    @TypeConverter
    fun eventListToString(list: List<List<Number>>): String {
        return list.toString()
    }

    @TypeConverter
    fun stringToEventList(string: String): List<List<Number>> {
        val type = object : TypeToken<List<List<Number>>>() {}.type
        return gson.fromJson(string, type)
    }
}


