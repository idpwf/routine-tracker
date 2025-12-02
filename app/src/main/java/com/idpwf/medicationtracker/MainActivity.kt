package com.idpwf.medicationtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.idpwf.medicationtracker.logic.MedicationTrackerViewModel
import com.idpwf.medicationtracker.ui.theme.MedicationTrackerTheme

class Medication(val id: Int, val name: String, val dosage: String, val takenToday: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MedicationTrackerTheme {
                val viewModel: MedicationTrackerViewModel = viewModel()
                val medications by viewModel.medications.collectAsState()

                var showAddMedicationDialog by remember { mutableStateOf(false) }

                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = { showAddMedicationDialog = true }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Medication")
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        MedicationTrackerTopBar()
                        MedicationTrackerMedsList(
                            meds = medications,
                            onMedicationTaken = { medicationName -> viewModel.takeMed(medicationName) },
                            onDeleteMedication = { medication -> viewModel.deleteMedication(medication) }
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
fun AppHeaderRow(modifier: Modifier = Modifier) {
    Row(modifier.padding(16.dp)) {
        Text(
            text = "Medication Tracker",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TakenMedRow(
    modifier: Modifier = Modifier,
    medication: Medication,
    onMedicationTaken: (String) -> Unit,
    onDeleteMedication: (Medication) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Medication Info and Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Column for Name and Dosage to stack them vertically.
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = medication.dosage,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = medication.takenToday.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            // Bottom Row: Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onMedicationTaken(medication.name) },
                    shape = MaterialTheme.shapes.medium) {
                    Text("Take Now",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                IconButton(onClick = { onDeleteMedication(medication) },
                    colors = IconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = MaterialTheme.colorScheme.outline,
                        disabledContainerColor = Color.Transparent
                    )) {
                    Icon(Icons.Filled.Delete,
                        contentDescription = "Delete Medication"
                    )
                }
            }
        }
    }
}

@Composable
fun MedicationTrackerMedsList(
    meds: List<Medication>,
    modifier: Modifier = Modifier,
    onMedicationTaken: (String) -> Unit,
    onDeleteMedication: (Medication) -> Unit
) {
    Column(modifier) {
        meds.forEach { medication ->
            TakenMedRow(
                medication = medication,
                onMedicationTaken = onMedicationTaken,
                onDeleteMedication = onDeleteMedication
            )
        }
    }
}
