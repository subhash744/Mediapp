package com.example.data.repository

import com.example.data.model.ActivityLog
import com.example.data.model.ActivityType
import com.example.data.model.AdherenceStats
import com.example.data.model.AuthSession
import com.example.data.model.ConnectionStatus
import com.example.data.model.DoseRecord
import com.example.data.model.DoseStatus
import com.example.data.model.HistoryLog
import com.example.data.model.FamilyMember
import com.example.data.model.FoodTiming
import com.example.data.model.Frequency
import com.example.data.model.Medicine
import com.example.data.model.MedicineType
import com.example.data.model.PairingInfo
import com.example.data.model.ReminderTime
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

class LocalAuthService : AuthService {
    private val _sessionState = MutableStateFlow(
        AuthSession(
            user = UserProfile(
                id = "child-arjun-01",
                email = "arjun.sharma@example.com",
                fullName = "Arjun",
                role = UserRole.CHILD,
                age = 12,
                isEmailVerified = true
            ),
            token = "mock-jwt-token-child",
            isAuthenticated = true
        )
    )
    override val sessionState: StateFlow<AuthSession> = _sessionState.asStateFlow()

    override suspend fun signUp(name: String, email: String, password: String, role: UserRole): Result<UserProfile> {
        val newUser = UserProfile(
            id = UUID.randomUUID().toString(),
            email = email,
            fullName = name,
            role = role,
            isEmailVerified = false
        )
        _sessionState.value = AuthSession(user = newUser, token = "mock-token", isAuthenticated = true)
        return Result.success(newUser)
    }

    override suspend fun signIn(email: String, password: String, role: UserRole): Result<UserProfile> {
        val user = if (role == UserRole.CHILD) {
            UserProfile(
                id = "child-arjun-01",
                email = email.ifBlank { "arjun.sharma@example.com" },
                fullName = "Arjun",
                role = UserRole.CHILD,
                age = 12,
                isEmailVerified = true
            )
        } else {
            UserProfile(
                id = "parent-rahul-01",
                email = email.ifBlank { "rahul.sharma@example.com" },
                fullName = "Rahul",
                role = UserRole.PARENT,
                isEmailVerified = true
            )
        }
        _sessionState.value = AuthSession(user = user, token = "mock-token", isAuthenticated = true)
        return Result.success(user)
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun verifyEmail(code: String): Result<Unit> {
        val current = _sessionState.value.user ?: return Result.failure(Exception("No user"))
        _sessionState.update { it.copy(user = current.copy(isEmailVerified = true)) }
        return Result.success(Unit)
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun signOut(): Result<Unit> {
        _sessionState.value = AuthSession(user = null, token = null, isAuthenticated = false)
        return Result.success(Unit)
    }

    override suspend fun switchRole(role: UserRole) {
        val user = if (role == UserRole.CHILD) {
            UserProfile(
                id = "child-arjun-01",
                email = "arjun.sharma@example.com",
                fullName = "Arjun",
                role = UserRole.CHILD,
                age = 12,
                isEmailVerified = true
            )
        } else {
            UserProfile(
                id = "parent-rahul-01",
                email = "rahul.sharma@example.com",
                fullName = "Rahul",
                role = UserRole.PARENT,
                isEmailVerified = true
            )
        }
        _sessionState.value = AuthSession(user = user, token = "mock-token", isAuthenticated = true)
    }
}

class LocalProfileService(private val authService: AuthService) : ProfileService {
    override val currentProfile: StateFlow<UserProfile?> =
        MutableStateFlow(authService.sessionState.value.user)

    private val _profile = MutableStateFlow(
        authService.sessionState.value.user ?: UserProfile(
            id = "child-arjun-01",
            email = "arjun.sharma@example.com",
            fullName = "Arjun",
            role = UserRole.CHILD,
            age = 12,
            isEmailVerified = true
        )
    )
    override val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    override suspend fun updateProfile(name: String, age: Int?): Result<UserProfile> {
        val current = authService.sessionState.value.user ?: return Result.failure(Exception("Not logged in"))
        val updated = current.copy(fullName = name, age = age)
        _profile.value = updated
        return Result.success(updated)
    }

    override suspend fun updateProfileName(name: String): Result<UserProfile> {
        val current = _profile.value
        val updated = current.copy(fullName = name)
        _profile.value = updated
        return Result.success(updated)
    }
}

class LocalMedicineRepository : MedicineRepository {
    private val _medicines = MutableStateFlow<List<Medicine>>(initialMedicines())

    override fun getMedicinesFlow(): Flow<List<Medicine>> = _medicines.asStateFlow()

    override suspend fun getMedicineById(id: String): Medicine? {
        return _medicines.value.find { it.id == id }
    }

    override suspend fun addMedicine(medicine: Medicine): Result<Medicine> {
        _medicines.update { listOf(medicine) + it }
        return Result.success(medicine)
    }

    override suspend fun updateMedicine(medicine: Medicine): Result<Medicine> {
        _medicines.update { list ->
            list.map { if (it.id == medicine.id) medicine else it }
        }
        return Result.success(medicine)
    }

    override suspend fun deleteMedicine(id: String): Result<Unit> {
        _medicines.update { list -> list.filterNot { it.id == id } }
        return Result.success(Unit)
    }

    override suspend fun archiveMedicine(id: String, archive: Boolean): Result<Unit> {
        _medicines.update { list ->
            list.map { if (it.id == id) it.copy(isArchived = archive) else it }
        }
        return Result.success(Unit)
    }

    fun resetData() {
        _medicines.value = initialMedicines()
    }

    fun clearAll() {
        _medicines.value = emptyList()
    }

    companion object {
        fun initialMedicines(): List<Medicine> = listOf(
            Medicine(
                id = "med-1",
                name = "Amoxicillin",
                strength = "500 mg",
                dosage = "1 capsule",
                type = MedicineType.CAPSULE,
                frequency = Frequency.DAILY,
                timesPerDay = 2,
                reminderTimes = listOf(
                    ReminderTime(hour = 8, minute = 0),
                    ReminderTime(hour = 20, minute = 0)
                ),
                foodTiming = FoodTiming.AFTER_FOOD,
                notes = "Finish entire 10-day prescribed course. Drink with plenty of water.",
                totalQuantity = 20,
                currentStock = 14,
                refillThreshold = 4
            ),
            Medicine(
                id = "med-2",
                name = "Vitamin D3",
                strength = "1000 IU",
                dosage = "1 tablet",
                type = MedicineType.TABLET,
                frequency = Frequency.DAILY,
                timesPerDay = 1,
                reminderTimes = listOf(
                    ReminderTime(hour = 9, minute = 0)
                ),
                foodTiming = FoodTiming.WITH_FOOD,
                notes = "Take with breakfast or fatty meal for optimal absorption.",
                totalQuantity = 60,
                currentStock = 45,
                refillThreshold = 7
            ),
            Medicine(
                id = "med-3",
                name = "Cetirizine",
                strength = "5 mg",
                dosage = "1 chewable",
                type = MedicineType.TABLET,
                frequency = Frequency.DAILY,
                timesPerDay = 1,
                reminderTimes = listOf(
                    ReminderTime(hour = 21, minute = 0)
                ),
                foodTiming = FoodTiming.AFTER_FOOD,
                notes = "Evening dose for seasonal allergy relief. May cause mild drowsiness.",
                totalQuantity = 30,
                currentStock = 22,
                refillThreshold = 5
            ),
            Medicine(
                id = "med-4",
                name = "Albuterol Inhaler",
                strength = "90 mcg",
                dosage = "2 puffs",
                type = MedicineType.INHALER,
                frequency = Frequency.AS_NEEDED,
                timesPerDay = 1,
                reminderTimes = listOf(
                    ReminderTime(hour = 14, minute = 0)
                ),
                foodTiming = FoodTiming.NO_RESTRICTION,
                notes = "Rinse mouth with water after inhalation. Keep handy during sports.",
                totalQuantity = 200,
                currentStock = 140,
                refillThreshold = 20
            )
        )
    }
}

class LocalScheduleRepository(private val medicineRepo: MedicineRepository) : ScheduleRepository {
    override fun getReminderTimesForMedicine(medicineId: String): Flow<List<ReminderTime>> {
        return medicineRepo.getMedicinesFlow().map { list ->
            list.find { it.id == medicineId }?.reminderTimes ?: emptyList()
        }
    }

    override suspend fun updateReminderTimes(medicineId: String, times: List<ReminderTime>): Result<Unit> {
        val med = medicineRepo.getMedicineById(medicineId) ?: return Result.failure(Exception("Medicine not found"))
        medicineRepo.updateMedicine(med.copy(reminderTimes = times, timesPerDay = times.size))
        return Result.success(Unit)
    }
}

class LocalDoseRepository(
    private val medicineRepo: MedicineRepository
) : DoseRepository {
    private val _doses = MutableStateFlow<List<DoseRecord>>(generateInitialDoses())
    private val _activityLogs = MutableStateFlow<List<ActivityLog>>(generateInitialLogs())

    override fun getTodayDosesFlow(): Flow<List<DoseRecord>> {
        return _doses.map { list ->
            // Filter today's doses
            val todayStart = getTodayMidnight()
            list.filter { it.scheduledDate == todayStart }
                .sortedWith(compareBy({ it.scheduledTime.hour }, { it.scheduledTime.minute }))
        }
    }

    override fun getAllDosesHistoryFlow(): Flow<List<DoseRecord>> {
        return _doses.map { list ->
            list.sortedByDescending { it.takenAt ?: it.scheduledDate }
        }
    }

    override fun getHistoryLogsFlow(): Flow<List<HistoryLog>> {
        return _doses.map { list ->
            list.filter { it.status == DoseStatus.TAKEN || it.status == DoseStatus.SKIPPED || it.status == DoseStatus.MISSED }
                .map { record ->
                    HistoryLog(
                        id = record.id,
                        medicineId = record.medicineId,
                        medicineName = record.medicineName,
                        dosage = record.dosage,
                        scheduledTime = record.scheduledTime,
                        recordedAt = record.takenAt ?: record.scheduledDate,
                        status = record.status,
                        note = record.skippedReason ?: record.notes.takeIf { it.isNotBlank() }
                    )
                }
                .sortedByDescending { it.recordedAt }
        }
    }

    override fun getAdherenceStatsFlow(): Flow<AdherenceStats> {
        return _doses.map { list ->
            val total = list.size
            val taken = list.count { it.status == DoseStatus.TAKEN }
            val skipped = list.count { it.status == DoseStatus.SKIPPED }
            val missed = list.count { it.status == DoseStatus.MISSED }
            val rate = if (total > 0) taken.toFloat() / total.toFloat() else 1.0f
            AdherenceStats(
                totalDoses = total,
                takenDoses = taken,
                skippedDoses = skipped,
                missedDoses = missed,
                adherenceRate = rate,
                streakDays = 5
            )
        }
    }

    override fun getActivityLogsFlow(): Flow<List<ActivityLog>> = _activityLogs.asStateFlow()

    override suspend fun recordTakeDose(doseId: String): Result<Unit> {
        val now = System.currentTimeMillis()
        var updatedRecord: DoseRecord? = null
        _doses.update { list ->
            list.map { record ->
                if (record.id == doseId) {
                    val updated = record.copy(
                        status = DoseStatus.TAKEN,
                        takenAt = now
                    )
                    updatedRecord = updated
                    updated
                } else record
            }
        }
        updatedRecord?.let {
            addActivityLog(
                title = "Dose Taken: ${it.medicineName}",
                description = "Arjun took ${it.dosage} at ${it.scheduledTime.formatted}",
                type = ActivityType.DOSE_TAKEN
            )
        }
        return Result.success(Unit)
    }

    override suspend fun recordSkipDose(doseId: String, reason: String): Result<Unit> {
        var updatedRecord: DoseRecord? = null
        _doses.update { list ->
            list.map { record ->
                if (record.id == doseId) {
                    val updated = record.copy(
                        status = DoseStatus.SKIPPED,
                        skippedReason = reason
                    )
                    updatedRecord = updated
                    updated
                } else record
            }
        }
        updatedRecord?.let {
            addActivityLog(
                title = "Dose Skipped: ${it.medicineName}",
                description = "Arjun skipped (${reason.ifBlank { "Not feeling well" }})",
                type = ActivityType.DOSE_SKIPPED
            )
        }
        return Result.success(Unit)
    }

    override suspend fun undoDoseAction(doseId: String): Result<Unit> {
        _doses.update { list ->
            list.map { record ->
                if (record.id == doseId) {
                    record.copy(
                        status = DoseStatus.DUE,
                        takenAt = null,
                        skippedReason = null
                    )
                } else record
            }
        }
        return Result.success(Unit)
    }

    override suspend fun simulateDoseDueNow(): Result<Unit> {
        _doses.update { list ->
            var flipped = false
            list.map { record ->
                if (!flipped && record.status == DoseStatus.UPCOMING) {
                    flipped = true
                    record.copy(status = DoseStatus.DUE)
                } else record
            }
        }
        return Result.success(Unit)
    }

    fun syncFromNewMedicine(medicine: Medicine) {
        val today = getTodayMidnight()
        val newDoses = medicine.reminderTimes.map { time ->
            DoseRecord(
                medicineId = medicine.id,
                medicineName = medicine.name,
                medicineStrength = medicine.strength,
                dosage = medicine.dosage,
                medicineType = medicine.type,
                foodTiming = medicine.foodTiming,
                scheduledTime = time,
                scheduledDate = today,
                status = DoseStatus.UPCOMING
            )
        }
        _doses.update { it + newDoses }
        addActivityLog(
            title = "New Medicine Added",
            description = "${medicine.name} (${medicine.strength}) added with ${medicine.reminderTimes.size} daily reminders",
            type = ActivityType.MEDICINE_ADDED
        )
    }

    private fun addActivityLog(title: String, description: String, type: ActivityType) {
        val log = ActivityLog(
            title = title,
            description = description,
            timestamp = System.currentTimeMillis(),
            type = type
        )
        _activityLogs.update { listOf(log) + it.take(20) }
    }

    fun resetData() {
        _doses.value = generateInitialDoses()
        _activityLogs.value = generateInitialLogs()
    }

    fun clearAll() {
        _doses.value = emptyList()
        _activityLogs.value = emptyList()
    }

    companion object {
        fun getTodayMidnight(): Long {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        private fun getYesterdayMidnight(): Long {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        fun generateInitialDoses(): List<DoseRecord> {
            val today = getTodayMidnight()
            val yesterday = getYesterdayMidnight()

            return listOf(
                // Today's doses
                DoseRecord(
                    id = "dose-today-1",
                    medicineId = "med-1",
                    medicineName = "Amoxicillin",
                    medicineStrength = "500 mg",
                    dosage = "1 capsule",
                    medicineType = MedicineType.CAPSULE,
                    foodTiming = FoodTiming.AFTER_FOOD,
                    scheduledTime = ReminderTime(hour = 8, minute = 0),
                    scheduledDate = today,
                    status = DoseStatus.TAKEN,
                    takenAt = today + (8 * 3600 + 4 * 60) * 1000L
                ),
                DoseRecord(
                    id = "dose-today-2",
                    medicineId = "med-2",
                    medicineName = "Vitamin D3",
                    medicineStrength = "1000 IU",
                    dosage = "1 tablet",
                    medicineType = MedicineType.TABLET,
                    foodTiming = FoodTiming.WITH_FOOD,
                    scheduledTime = ReminderTime(hour = 9, minute = 0),
                    scheduledDate = today,
                    status = DoseStatus.DUE // Available for user to test TAKE / SKIP immediately!
                ),
                DoseRecord(
                    id = "dose-today-3",
                    medicineId = "med-4",
                    medicineName = "Albuterol Inhaler",
                    medicineStrength = "90 mcg",
                    dosage = "2 puffs",
                    medicineType = MedicineType.INHALER,
                    foodTiming = FoodTiming.NO_RESTRICTION,
                    scheduledTime = ReminderTime(hour = 14, minute = 0),
                    scheduledDate = today,
                    status = DoseStatus.UPCOMING
                ),
                DoseRecord(
                    id = "dose-today-4",
                    medicineId = "med-1",
                    medicineName = "Amoxicillin",
                    medicineStrength = "500 mg",
                    dosage = "1 capsule",
                    medicineType = MedicineType.CAPSULE,
                    foodTiming = FoodTiming.AFTER_FOOD,
                    scheduledTime = ReminderTime(hour = 20, minute = 0),
                    scheduledDate = today,
                    status = DoseStatus.UPCOMING
                ),
                DoseRecord(
                    id = "dose-today-5",
                    medicineId = "med-3",
                    medicineName = "Cetirizine",
                    medicineStrength = "5 mg",
                    dosage = "1 chewable",
                    medicineType = MedicineType.TABLET,
                    foodTiming = FoodTiming.AFTER_FOOD,
                    scheduledTime = ReminderTime(hour = 21, minute = 0),
                    scheduledDate = today,
                    status = DoseStatus.UPCOMING
                ),

                // Yesterday's historical doses for rich history screen
                DoseRecord(
                    id = "dose-yest-1",
                    medicineId = "med-1",
                    medicineName = "Amoxicillin",
                    medicineStrength = "500 mg",
                    dosage = "1 capsule",
                    medicineType = MedicineType.CAPSULE,
                    foodTiming = FoodTiming.AFTER_FOOD,
                    scheduledTime = ReminderTime(hour = 8, minute = 0),
                    scheduledDate = yesterday,
                    status = DoseStatus.TAKEN,
                    takenAt = yesterday + 8 * 3600 * 1000L
                ),
                DoseRecord(
                    id = "dose-yest-2",
                    medicineId = "med-2",
                    medicineName = "Vitamin D3",
                    medicineStrength = "1000 IU",
                    dosage = "1 tablet",
                    medicineType = MedicineType.TABLET,
                    foodTiming = FoodTiming.WITH_FOOD,
                    scheduledTime = ReminderTime(hour = 9, minute = 0),
                    scheduledDate = yesterday,
                    status = DoseStatus.TAKEN,
                    takenAt = yesterday + 9 * 3600 * 1000L
                ),
                DoseRecord(
                    id = "dose-yest-3",
                    medicineId = "med-1",
                    medicineName = "Amoxicillin",
                    medicineStrength = "500 mg",
                    dosage = "1 capsule",
                    medicineType = MedicineType.CAPSULE,
                    foodTiming = FoodTiming.AFTER_FOOD,
                    scheduledTime = ReminderTime(hour = 20, minute = 0),
                    scheduledDate = yesterday,
                    status = DoseStatus.TAKEN,
                    takenAt = yesterday + 20 * 3600 * 1000L
                ),
                DoseRecord(
                    id = "dose-yest-4",
                    medicineId = "med-3",
                    medicineName = "Cetirizine",
                    medicineStrength = "5 mg",
                    dosage = "1 chewable",
                    medicineType = MedicineType.TABLET,
                    foodTiming = FoodTiming.AFTER_FOOD,
                    scheduledTime = ReminderTime(hour = 21, minute = 0),
                    scheduledDate = yesterday,
                    status = DoseStatus.SKIPPED,
                    skippedReason = "No allergy symptoms observed"
                )
            )
        }

        fun generateInitialLogs(): List<ActivityLog> {
            return listOf(
                ActivityLog(
                    title = "Dose Taken: Amoxicillin",
                    description = "Arjun took 1 capsule at 8:04 AM",
                    timestamp = System.currentTimeMillis() - 3600000L * 2,
                    type = ActivityType.DOSE_TAKEN
                ),
                ActivityLog(
                    title = "Parent Monitored",
                    description = "Rahul checked Arjun's morning adherence",
                    timestamp = System.currentTimeMillis() - 3600000L * 4,
                    type = ActivityType.PARENT_CONNECTED
                ),
                ActivityLog(
                    title = "Dose Skipped: Cetirizine",
                    description = "Yesterday 9:15 PM (Reason: No allergy symptoms)",
                    timestamp = System.currentTimeMillis() - 3600000L * 12,
                    type = ActivityType.DOSE_SKIPPED
                )
            )
        }
    }
}

class LocalPairingService(
    private val familyRepo: LocalFamilyConnectionRepository
) : PairingService {
    private val _pairingState = MutableStateFlow(
        PairingInfo(
            code = "482 731",
            generatedAt = System.currentTimeMillis(),
            expiresInSeconds = 600,
            status = ConnectionStatus.WAITING_FOR_PARENT
        )
    )
    override val pairingState: StateFlow<PairingInfo> = _pairingState.asStateFlow()

    override fun generateNewPairingCode(): String {
        val rand = (100000..999999).random()
        val formatted = "${rand.toString().substring(0, 3)} ${rand.toString().substring(3, 6)}"
        _pairingState.update {
            it.copy(
                code = formatted,
                generatedAt = System.currentTimeMillis(),
                expiresInSeconds = 600,
                status = ConnectionStatus.WAITING_FOR_PARENT,
                pendingParent = null
            )
        }
        return formatted
    }

    override suspend fun simulateIncomingParentRequest(parent: FamilyMember) {
        _pairingState.update {
            it.copy(
                status = ConnectionStatus.PARENT_WANTS_TO_CONNECT,
                pendingParent = parent
            )
        }
    }

    override suspend fun acceptParentConnection(): Result<Unit> {
        val pending = _pairingState.value.pendingParent ?: FamilyMember(
            id = "parent-rahul-01",
            name = "Rahul",
            maskedEmail = "ra***@gmail.com",
            fullEmail = "rahul.sharma@example.com",
            role = UserRole.PARENT
        )
        familyRepo.setParentConnected(pending)
        _pairingState.update {
            it.copy(status = ConnectionStatus.CONNECTED)
        }
        return Result.success(Unit)
    }

    override suspend fun rejectParentConnection(): Result<Unit> {
        _pairingState.update {
            it.copy(
                status = ConnectionStatus.WAITING_FOR_PARENT,
                pendingParent = null
            )
        }
        return Result.success(Unit)
    }

    override suspend fun validateChildPairingCode(code: String): Result<FamilyMember> {
        val cleanInput = code.replace(" ", "")
        val currentCode = _pairingState.value.code.replace(" ", "")
        return if (cleanInput == currentCode || cleanInput == "482731") {
            val child = FamilyMember(
                id = "child-arjun-01",
                name = "Arjun",
                maskedEmail = "ar***@gmail.com",
                fullEmail = "arjun.sharma@example.com",
                role = UserRole.CHILD
            )
            Result.success(child)
        } else {
            Result.failure(Exception("Invalid or expired 6-digit pairing code. Please verify the code on your child's device."))
        }
    }

    override suspend fun submitParentConnectionRequest(childId: String): Result<Unit> {
        _pairingState.update {
            it.copy(status = ConnectionStatus.WAITING_FOR_CHILD_APPROVAL)
        }
        return Result.success(Unit)
    }

    override suspend fun simulateChildApproval(): Result<Unit> {
        val child = FamilyMember(
            id = "child-arjun-01",
            name = "Arjun",
            maskedEmail = "ar***@gmail.com",
            fullEmail = "arjun.sharma@example.com",
            role = UserRole.CHILD
        )
        familyRepo.setChildConnected(child)
        _pairingState.update {
            it.copy(status = ConnectionStatus.CONNECTED)
        }
        return Result.success(Unit)
    }

    override suspend fun submitCodeFromParent(code: String): Boolean {
        val clean = code.replace(" ", "")
        val currentCode = _pairingState.value.code.replace(" ", "")
        if (clean == currentCode || clean == "482731") {
            _pairingState.update {
                it.copy(status = ConnectionStatus.WAITING_FOR_CHILD_APPROVAL)
            }
            return true
        }
        return false
    }

    override suspend fun cancelPairing(): Result<Unit> {
        _pairingState.update {
            it.copy(
                status = ConnectionStatus.WAITING_FOR_PARENT,
                pendingParent = null
            )
        }
        return Result.success(Unit)
    }

    fun resetData() {
        _pairingState.value = PairingInfo(
            code = "482 731",
            generatedAt = System.currentTimeMillis(),
            expiresInSeconds = 600,
            status = ConnectionStatus.WAITING_FOR_PARENT
        )
    }
}

class LocalFamilyConnectionRepository : FamilyConnectionRepository {
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.CONNECTED)
    override val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private val _connectedParent = MutableStateFlow<FamilyMember?>(
        FamilyMember(
            id = "parent-rahul-01",
            name = "Rahul",
            maskedEmail = "ra***@gmail.com",
            fullEmail = "rahul.sharma@example.com",
            role = UserRole.PARENT
        )
    )
    override val connectedParent: StateFlow<FamilyMember?> = _connectedParent.asStateFlow()

    private val _connectedChild = MutableStateFlow<FamilyMember?>(
        FamilyMember(
            id = "child-arjun-01",
            name = "Arjun",
            maskedEmail = "ar***@gmail.com",
            fullEmail = "arjun.sharma@example.com",
            role = UserRole.CHILD
        )
    )
    override val connectedChild: StateFlow<FamilyMember?> = _connectedChild.asStateFlow()

    fun setParentConnected(parent: FamilyMember) {
        _connectedParent.value = parent
        _connectionStatus.value = ConnectionStatus.CONNECTED
    }

    fun setChildConnected(child: FamilyMember) {
        _connectedChild.value = child
        _connectionStatus.value = ConnectionStatus.CONNECTED
    }

    override suspend fun disconnectFamily(): Result<Unit> {
        _connectedParent.value = null
        _connectedChild.value = null
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
        return Result.success(Unit)
    }

    override suspend fun sendParentNudge(message: String): Result<Unit> {
        return Result.success(Unit)
    }

    fun resetData() {
        _connectionStatus.value = ConnectionStatus.CONNECTED
        _connectedParent.value = FamilyMember(
            id = "parent-rahul-01",
            name = "Rahul",
            maskedEmail = "ra***@gmail.com",
            fullEmail = "rahul.sharma@example.com",
            role = UserRole.PARENT
        )
        _connectedChild.value = FamilyMember(
            id = "child-arjun-01",
            name = "Arjun",
            maskedEmail = "ar***@gmail.com",
            fullEmail = "arjun.sharma@example.com",
            role = UserRole.CHILD
        )
    }
}

class LocalSyncService : SyncService {
    private val _isSyncing = MutableStateFlow(false)
    override val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSynced = MutableStateFlow(System.currentTimeMillis())
    override val lastSyncedTimestamp: StateFlow<Long> = _lastSynced.asStateFlow()

    override suspend fun triggerManualSync(): Result<Unit> {
        _isSyncing.value = true
        kotlinx.coroutines.delay(800)
        _lastSynced.value = System.currentTimeMillis()
        _isSyncing.value = false
        return Result.success(Unit)
    }
}
