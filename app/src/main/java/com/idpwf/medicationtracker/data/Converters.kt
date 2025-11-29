package com.idpwf.medicationtracker.data

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromMap(value: Map<String, Int>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toMap(value: String): Map<String, Int> {
        return Json.decodeFromString(value)
    }
}
