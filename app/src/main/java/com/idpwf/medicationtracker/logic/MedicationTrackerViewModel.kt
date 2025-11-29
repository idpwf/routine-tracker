package com.idpwf.medicationtracker.logic

import androidx.lifecycle.ViewModel

class MedicationTrackerViewModel : ViewModel() {
    //TODO Does this work correctly if I start the `Activity` EXACTLY at midnight?
//    var resetTakenMedsNextTime = LocalDate.now().plusDays(1).atStartOfDay()

    private val _takenToday: HashMap<String, Int> = hashMapOf(
        Pair("Ibuprofen 200 mg", 0),
        Pair("Acetaminophen 500 mg", 2)
    )

    fun takenToday(): Map<String, Int> = _takenToday.toMap()
    fun takeMed(name: String, howMany: Int = 1) = {
        _takenToday[name]?.also {
            _takenToday.put(name, it + 1)
        } ?: {
            _takenToday.put(name, 1)
        }()
    }
}