package com.idpwf.medicationtracker.logic

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idpwf.medicationtracker.data.MedicationDao
import com.idpwf.medicationtracker.data.MedicationDatabase
import com.idpwf.medicationtracker.data.MedicationsTakenRecord
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class DailyMedicationLogManagerTest {

    private lateinit var medicationDao: MedicationDao
    private lateinit var db: MedicationDatabase
    private lateinit var logManager: DailyMedicationLogManager

    private val todayString: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    private val yesterdayString: String = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // Step 1: Add allowMainThreadQueries() to the database builder.
        // This is a requirement for Room testing to prevent a crash during initialization.
        db = Room.inMemoryDatabaseBuilder(
            context,
            MedicationDatabase::class.java
        ).allowMainThreadQueries().build()
        medicationDao = db.medicationDao()

        // Step 2: Use the test-only constructor to inject the in-memory DAO.
        // This ensures the manager and the test are using the exact same database instance.
        logManager = DailyMedicationLogManager(medicationDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun getCurrentState_whenNoRecordExists_createsAndReturnsEmptyRecordForToday() = runBlocking {
        // Act
        val result = logManager.getCurrentState()

        // Assert
        assertEquals(todayString, result.date)
        assertEquals(emptyMap<String, Int>(), result.medicationsTaken)
    }

    @Test
    @Throws(Exception::class)
    fun getCurrentState_whenRecordForTodayExists_returnsIt() = runBlocking {
        // Arrange
        val record = MedicationsTakenRecord(todayString, mapOf("Aspirin" to 1))
        medicationDao.insert(record)

        // Act
        val result = logManager.getCurrentState()

        // Assert
        assertEquals(record, result)
    }

    @Test
    @Throws(Exception::class)
    fun getCurrentState_whenRecordForYesterdayExists_clearsDbAndReturnsEmptyRecordForToday() = runBlocking {
        // Arrange
        val oldRecord = MedicationsTakenRecord(yesterdayString, mapOf("Ibuprofen" to 2))
        medicationDao.insert(oldRecord)

        // Act
        val result = logManager.getCurrentState()

        // Assert
        assertEquals(todayString, result.date)
        assertEquals(emptyMap<String, Int>(), result.medicationsTaken)
    }

    @Test
    @Throws(Exception::class)
    fun takeMedication_whenNoRecordExists_createsNewRecordWithMedication() = runBlocking {
        // Act
        logManager.takeMedication("Aspirin")

        // Assert
        val record = medicationDao.getTodaysRecord()
        assertEquals(todayString, record?.date)
        assertEquals(mapOf("Aspirin" to 1), record?.medicationsTaken)
    }

    @Test
    @Throws(Exception::class)
    fun takeMedication_whenTodayRecordExists_incrementsExistingMedication() = runBlocking {
        // Arrange
        val initialRecord = MedicationsTakenRecord(todayString, mapOf("Aspirin" to 1))
        medicationDao.insert(initialRecord)

        // Act
        logManager.takeMedication("Aspirin")

        // Assert
        val record = medicationDao.getTodaysRecord()
        assertEquals(mapOf("Aspirin" to 2), record?.medicationsTaken)
    }

    @Test
    @Throws(Exception::class)
    fun takeMedication_whenTodayRecordExists_addsNewMedication() = runBlocking {
        // Arrange
        val initialRecord = MedicationsTakenRecord(todayString, mapOf("Ibuprofen" to 2))
        medicationDao.insert(initialRecord)

        // Act
        logManager.takeMedication("Aspirin")

        // Assert
        val record = medicationDao.getTodaysRecord()
        assertEquals(mapOf("Ibuprofen" to 2, "Aspirin" to 1), record?.medicationsTaken)
    }

    @Test
    @Throws(Exception::class)
    fun takeMedication_whenYesterdayRecordExists_createsNewRecordForToday() = runBlocking {
        // Arrange
        val oldRecord = MedicationsTakenRecord(yesterdayString, mapOf("Ibuprofen" to 2))
        medicationDao.insert(oldRecord)

        // Act
        logManager.takeMedication("Aspirin")

        // Assert
        val record = medicationDao.getTodaysRecord()
        assertEquals(todayString, record?.date)
        assertEquals(mapOf("Aspirin" to 1), record?.medicationsTaken)
    }
}
