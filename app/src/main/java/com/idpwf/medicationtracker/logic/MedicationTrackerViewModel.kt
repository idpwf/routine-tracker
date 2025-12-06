package com.idpwf.medicationtracker.logic

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.idpwf.medicationtracker.Medication
import com.idpwf.medicationtracker.data.LocalMedicationStorage
import com.idpwf.medicationtracker.data.MedicationRecord
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MedicationTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val localMedicationStorage = LocalMedicationStorage(application)
    private val dailyMedicationLogManager = DailyMedicationLogManager(application)

    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()

    init {
        refreshMedications()
    }

    private fun refreshMedications() {
        viewModelScope.launch {

            val allMedsDeferred =
                async { Json.decodeFromString<List<MedicationRecord>>(localMedicationStorage.getAllMedications()) }
            val takenTodayDeferred = async { dailyMedicationLogManager.getCurrentState() }

            val allMeds = allMedsDeferred.await()
            val takenTodayRecord = takenTodayDeferred.await()

            val combinedList = allMeds.map { medicationRecord ->
                val takenCount =
                    takenTodayRecord.medicationsTaken[medicationRecord.medicationName] ?: 0
                Medication(
                    id = medicationRecord.id,
                    name = medicationRecord.medicationName,
                    dosage = medicationRecord.dosage,
                    takenToday = takenCount
                )
            }

            _medications.value = combinedList
        }
    }

    fun takeMed(name: String) {
        viewModelScope.launch {
            dailyMedicationLogManager.takeMedication(name)
            refreshMedications()
        }
    }

    fun addMedication(name: String, dosage: String) {
        viewModelScope.launch {
            val medicationRecord = MedicationRecord(medicationName = name, dosage = dosage)
            localMedicationStorage.addMedication(medicationRecord)
            refreshMedications()
        }
    }

    fun deleteMedication(medication: Medication) {
        viewModelScope.launch {
            val medicationRecord = MedicationRecord(
                id = medication.id,
                medicationName = medication.name,
                dosage = medication.dosage
            )
            localMedicationStorage.deleteMedication(medicationRecord)
            refreshMedications()
        }
    }
}
