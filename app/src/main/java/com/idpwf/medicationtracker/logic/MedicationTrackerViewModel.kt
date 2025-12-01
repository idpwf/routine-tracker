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

    // This is the single source of truth for the UI. It holds the combined
    // and processed list of medications to be displayed.
    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()

    init {
        // Load the initial state when the ViewModel is created.
        refreshMedications()
    }

    private fun refreshMedications() {
        viewModelScope.launch {
            Log.i("MedicationTrackerViewModel", "Starting to refresh medication state.")

            // Using async to fetch both data sources concurrently for better performance.
            val allMedsDeferred = async { Json.decodeFromString<List<MedicationRecord>>(localMedicationStorage.getAllMedications()) }
            val takenTodayDeferred = async { dailyMedicationLogManager.getCurrentState() }

            val allMeds = allMedsDeferred.await()
            val takenTodayRecord = takenTodayDeferred.await()

            // This is the combination logic, as specified in the MT-004 prompt.
            val combinedList = allMeds.map { medicationRecord ->
                val takenCount = takenTodayRecord.medicationsTaken[medicationRecord.medicationName] ?: 0
                Medication(
                    name = medicationRecord.medicationName,
                    dosage = medicationRecord.dosage,
                    takenToday = takenCount
                )
            }

            _medications.value = combinedList
            Log.i("MedicationTrackerViewModel", "Successfully refreshed medication state.")
        }
    }

    fun takeMed(name: String) {
        viewModelScope.launch {
            Log.i("MedicationTrackerViewModel", "Taking medication: $name.")
            dailyMedicationLogManager.takeMedication(name)
            refreshMedications() // Refresh the state after taking a med
            Log.i("MedicationTrackerViewModel", "Successfully took medication: $name.")
        }
    }

    fun addMedication(name: String, dosage: String) {
        viewModelScope.launch {
            Log.i("MedicationTrackerViewModel", "Adding new medication: $name, $dosage.")
            try {
                val medicationRecord = MedicationRecord(medicationName = name, dosage = dosage)
                localMedicationStorage.addMedication(medicationRecord)
                refreshMedications() // Refresh the list after adding a new medication
                Log.i("MedicationTrackerViewModel", "Successfully added new medication.")
            } catch (e: Exception) {
                Log.e("MedicationTrackerViewModel", "Failed to add medication.", e)
            }
        }
    }
}
