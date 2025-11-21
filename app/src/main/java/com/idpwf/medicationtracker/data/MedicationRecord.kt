package com.idpwf.medicationtracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medication_records",
    indices = [Index(value = ["medication_name", "dosage"], unique = true)]
)
data class MedicationRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "medication_name")
    val medicationName: String,

    @ColumnInfo(name = "dosage")
    val dosage: String
)
