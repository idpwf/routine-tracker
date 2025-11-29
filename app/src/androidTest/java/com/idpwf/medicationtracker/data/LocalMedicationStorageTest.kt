package com.idpwf.medicationtracker.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalMedicationStorageTest {

    private lateinit var db: MedicationDatabase
    private lateinit var medicationDao: MedicationDao
    private lateinit var storage: LocalMedicationStorage

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context,
            MedicationDatabase::class.java
        ).allowMainThreadQueries().build()
        medicationDao = db.medicationDao()
        storage = LocalMedicationStorage(medicationDao)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun getAllMedications_whenDbIsEmpty_returnsEmptyJsonArray() = runBlocking {
        // Act
        val result = storage.getAllMedications()

        // Assert
        assertThat(result).isEqualTo("[]")
    }

//    @Test
//    fun getAllMedications_whenDbHasRecords_returnsCorrectJson() = runBlocking {
//        // Arrange
//        val record1 = MedicationRecord(1, "Aspirin", "100 mg")
//        val record2 = MedicationRecord(2, "Ibuprofen", "200 mg")
//        medicationDao.insert(record1)
//        medicationDao.insert(record2)
//
//        // Act
//        val result = storage.getAllMedications()
//        val decodedResult = Json.decodeFromString<List<MedicationRecord>>(result)
//
//        // Assert
//        assertThat(decodedResult).containsExactly(record1, record2)
//    }

    @Test
    fun addMedication_addsRecordSuccessfully() = runBlocking {
        // Arrange
        val record = MedicationRecord(medicationName = "Lisinopril", dosage = "10 mg")

        // Act
        storage.addMedication(record)

        // Assert
        val allRecords = medicationDao.getAll()
        assertThat(allRecords).hasSize(1)
        assertThat(allRecords[0].medicationName).isEqualTo("Lisinopril")
    }

    @Test(expected = SQLiteConstraintException::class)
    fun addMedication_whenAddingDuplicate_throwsException() = runBlocking {
        // Arrange
        val record = MedicationRecord(medicationName = "Aspirin", dosage = "100 mg")
        storage.addMedication(record)

        // Act: Attempt to add the same record again
        storage.addMedication(record.copy(id = 2)) // Copy with a different ID but same content
    }

    @Test
    fun deleteMedication_whenRecordExists_deletesItAndReturnsTrue() = runBlocking {
        // Arrange
        val record = MedicationRecord(medicationName = "Metformin", dosage = "500 mg")
        storage.addMedication(record)
        val addedRecord = medicationDao.getAll().first()
        assertThat(medicationDao.getAll()).isNotEmpty()

        // Act
        val result = storage.deleteMedication(addedRecord)

        // Assert
        assertThat(result).isTrue()
        assertThat(medicationDao.getAll()).isEmpty()
    }

    @Test
    fun deleteMedication_whenRecordDoesNotExist_returnsFalse() = runBlocking {
        // Arrange
        val nonExistentRecord = MedicationRecord(id = 99, medicationName = "Non-Existent", dosage = "N/A")

        // Act
        val result = storage.deleteMedication(nonExistentRecord)

        // Assert
        assertThat(result).isFalse()
    }
}
