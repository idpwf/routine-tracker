package com.idpwf.medicationtracker.data
import kotlinx.serialization.encodeToString

import android.content.Context
import kotlinx.serialization.json.Json

class LocalMedicationStorage(context: Context) {

    private val medicationDao = MedicationDatabase.getDatabase(context).medicationDao()

    suspend fun getAllMedications(): String {
        val medications = medicationDao.getAll()
        return Json.encodeToString(medications)
    }

    suspend fun addMedication(medicationRecord: MedicationRecord) {
        medicationDao.insert(medicationRecord)
    }

    suspend fun deleteMedication(medicationRecord: MedicationRecord): Boolean {
        val deletedRows = medicationDao.delete(medicationRecord)
        return deletedRows > 0
    }
}