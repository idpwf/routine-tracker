package com.idpwf.medicationtracker.logic

import android.content.Context
import com.idpwf.medicationtracker.data.MedicationDao
import com.idpwf.medicationtracker.data.MedicationDatabase
import com.idpwf.medicationtracker.data.MedicationsTakenRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyMedicationLogManager {

    private val medicationDao: MedicationDao

    constructor(context: Context) {
        this.medicationDao = MedicationDatabase.getDatabase(context).medicationDao()
    }

    internal constructor(medicationDao: MedicationDao) {
        this.medicationDao = medicationDao
    }

    private fun getTodayDateString(): String {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    private suspend fun createEmptyRecord(): MedicationsTakenRecord {
        val today = getTodayDateString()
        val emptyRecord = MedicationsTakenRecord(date = today, medicationsTaken = emptyMap())
        medicationDao.insert(emptyRecord)
        return emptyRecord
    }

    suspend fun getCurrentState(): MedicationsTakenRecord {
        val today = getTodayDateString()
        val record = medicationDao.getTodaysRecord()

        return when {
            record == null -> createEmptyRecord()
            record.date == today -> record
            else -> {
                medicationDao.clearMedicationsTaken()
                createEmptyRecord()
            }
        }
    }

    suspend fun takeMedication(medicationName: String) {
        val currentState = getCurrentState()
        val updatedMedications = currentState.medicationsTaken.toMutableMap()
        val currentCount = updatedMedications.getOrDefault(medicationName, 0)
        updatedMedications[medicationName] = currentCount + 1
        val updatedRecord = currentState.copy(medicationsTaken = updatedMedications)
        medicationDao.update(updatedRecord)
    }
}
