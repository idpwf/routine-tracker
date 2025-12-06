package com.idpwf.medicationtracker.data

import android.content.Context
import kotlinx.serialization.json.Json

class LocalMedicationStorage {

    private val medicationDao: MedicationDao

    constructor(context: Context) {
        this.medicationDao = MedicationDatabase.getDatabase(context).medicationDao()
    }

    internal constructor(medicationDao: MedicationDao) {
        this.medicationDao = medicationDao
    }

    suspend fun getAllMedications(): String {
        val medications = medicationDao.getAll()
        return Json.encodeToString(medications)
    }

    suspend fun addMedication(medicationRecord: MedicationRecord) {
        medicationDao.insert(medicationRecord)
    }

    suspend fun deleteMedication(medicationRecord: MedicationRecord): Boolean {
        return medicationDao.delete(medicationRecord) > 0
    }
}
