package com.idpwf.medicationtracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medication_records")
    suspend fun getAll(): List<MedicationRecord>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(medicationRecord: MedicationRecord)

    @Delete
    suspend fun delete(medicationRecord: MedicationRecord): Int

    @Query("SELECT * FROM medications_taken LIMIT 1")
    suspend fun getTodaysRecord(): MedicationsTakenRecord?

    @Insert
    suspend fun insert(medicationsTakenRecord: MedicationsTakenRecord)

    @Update
    suspend fun update(medicationsTakenRecord: MedicationsTakenRecord)

    @Query("DELETE FROM medications_taken")
    suspend fun clearMedicationsTaken()
}
