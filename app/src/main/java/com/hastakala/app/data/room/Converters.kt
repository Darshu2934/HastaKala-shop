package com.hastakala.app.data.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringMap(value: String): Map<String, Int> {
        val mapType = object : TypeToken<Map<String, Int>>() {}.type
        return gson.fromJson(value, mapType) ?: emptyMap()
    }

    @TypeConverter
    fun toStringMap(map: Map<String, Int>): String {
        return gson.toJson(map)
    }
}
