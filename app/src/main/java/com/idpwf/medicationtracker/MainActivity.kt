package com.idpwf.medicationtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.idpwf.medicationtracker.logic.MedicationTrackerViewModel
import com.idpwf.medicationtracker.ui.theme.MedicationTrackerTheme

// This data class represents the fully combined data needed for display in the UI.
class Medication(val name: String, val dosage: String, val takenToday: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MedicationTrackerTheme {
                val viewModel: MedicationTrackerViewModel = viewModel()
                // The UI observes the list of medications. When this list changes in the
                // ViewModel, this composable will automatically re-render.
                val medications by viewModel.medications.collectAsState()

                var showAddMedicationDialog by remember { mutableStateOf(false) }

                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = { showAddMedicationDialog = true }) {
                            Text(text = "➕", fontSize = 24.sp)
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        MedicationTrackerTopBar()
                        MedicationTrackerMedsList(
                            meds = medications,
                            onMedicationTaken = { medicationName -> viewModel.takeMed(medicationName) }
                        )
                    }
                }

                if (showAddMedicationDialog) {
                    AddMedicationDialog(
                        onDismiss = { showAddMedicationDialog = false },
                        onConfirm = {
                            name, dosage -> viewModel.addMedication(name, dosage)
                            showAddMedicationDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AddMedicationDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Medication") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medication Name") }
                )
                Spacer(modifier = Modifier.padding(8.dp))
                TextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage (e.g., 100 mg)") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, dosage) }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MedicationTrackerTopBar(modifier: Modifier = Modifier) {
    Column(modifier) {
        AppHeaderRow(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun TakenMedTodayCounter(modifier: Modifier = Modifier, count: Int) {
    Text(
        text = count.toString(),
        modifier = modifier,
        fontSize = 30.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
fun TakenMedRow(
    modifier: Modifier = Modifier, 
    medication: Medication, 
    onMedicationTaken: (String) -> Unit
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = { onMedicationTaken(medication.name) },
            modifier = Modifier.weight(4f)
        ) {
            Text(text = "${medication.name} ${medication.dosage}")
        }

        TakenMedTodayCounter(
            modifier = Modifier.weight(1f),
            count = medication.takenToday
        )

        Button(
            { },
            modifier = Modifier.weight(1f)
        ) {
            Text("❌")
        }
    }
}

@Composable
fun AppHeaderRow(modifier: Modifier = Modifier) {
    Row(modifier) {
        Spacer(Modifier.weight(1f))
        Text(text = "Medication Tracker", textAlign = TextAlign.Center)
        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun TakenMedLabelRow(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth()) {
        Text(
            text = "Medication Name and Dose",
            modifier = Modifier.weight(4f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Today",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Delete",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MedicationTrackerMedsList(
    meds: List<Medication>,
    modifier: Modifier = Modifier,
    onMedicationTaken: (String) -> Unit
) {
    Column(modifier) {
        TakenMedLabelRow(modifier = Modifier.fillMaxWidth())
        meds.forEach { medication ->
            TakenMedRow(
                modifier = Modifier.fillMaxWidth(),
                medication = medication,
                onMedicationTaken = onMedicationTaken
            )
        }
    }
}
