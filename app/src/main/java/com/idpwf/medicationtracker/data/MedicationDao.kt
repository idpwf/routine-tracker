package com.idpwf.medicationtracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medication_records")
    suspend fun getAll(): List<MedicationRecord>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(medicationRecord: MedicationRecord)

    @Delete
    suspend fun delete(medicationRecord: MedicationRecord): Int
}
