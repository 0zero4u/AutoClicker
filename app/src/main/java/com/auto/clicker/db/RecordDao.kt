package com.auto.clicker.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecord(record: Record): Long

    @Update
    fun updateRecord(newRecord: Record)

    @Query("select * from record")
    fun loadAllRecords(): List<Record>

    @Query("select * from record where name = :name limit 1")
    fun loadRecordByName(name: String): Record

    @Delete
    fun deleteRecord(record: Record)

    @Query("delete from Record where name = :name")
    fun deleteRecordByName(name: String): Int
}
