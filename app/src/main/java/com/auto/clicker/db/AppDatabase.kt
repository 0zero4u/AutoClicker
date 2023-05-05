package com.auto.clicker.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.auto.clicker.MyApplication

@Database(version = 1, entities = [Record::class])
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recordDao(): RecordDao

    companion object {
        val INSTANCE: AppDatabase = Room.databaseBuilder(MyApplication.application, AppDatabase::class.java, "app_database")
            .build()
    }
}
