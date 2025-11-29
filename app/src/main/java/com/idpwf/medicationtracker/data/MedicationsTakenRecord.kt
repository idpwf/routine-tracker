package com.idpwf.medicationtracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "medications_taken")
data class MedicationsTakenRecord(
    @PrimaryKey
    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "medications_taken")
    val medicationsTaken: Map<String, Int>
)
