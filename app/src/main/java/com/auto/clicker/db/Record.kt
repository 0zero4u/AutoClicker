package com.auto.clicker.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(indices = [Index(value = ["name"], unique = true)])
data class Record(
    var name: String,
    val pointConfigs: List<PointConfig>
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

data class PointConfig(
    var x: Int,
    var y: Int,
    var delayMs: Long
)

class Converters {

    @TypeConverter
    fun fromPointConfigList(value: List<PointConfig>): String {
        val gson = Gson()
        val type = object : TypeToken<List<PointConfig>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toPointConfigList(value: String): List<PointConfig> {
        val gson = Gson()
        val type = object : TypeToken<List<PointConfig>>() {}.type
        return gson.fromJson(value, type)
    }
}