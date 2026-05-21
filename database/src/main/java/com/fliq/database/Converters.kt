package com.fliq.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(data: String): List<String> {
        if (data.isEmpty()) return emptyList()
        return data.split(",")
    }
}
