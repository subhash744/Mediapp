package com.example.data.model

import java.util.UUID

enum class DoseStatus(val displayName: String) {
    UPCOMING("Upcoming"),
    DUE("Due Now"),
    TAKEN("Taken"),
    SKIPPED("Skipped"),
    MISSED("Missed")
}

data class DoseRecord(
    val id: String = UUID.randomUUID().toString(),
    val medicineId: String,
    val medicineName: String,
    val medicineStrength: String = "",
    val dosage: String = "1 tablet",
    val medicineType: MedicineType = MedicineType.TABLET,
    val foodTiming: FoodTiming = FoodTiming.AFTER_FOOD,
    val scheduledTime: ReminderTime,
    val scheduledDate: Long, // Midnight timestamp for the day
    val status: DoseStatus = DoseStatus.UPCOMING,
    val takenAt: Long? = null,
    val skippedReason: String? = null,
    val notes: String = ""
)

data class HistoryLog(
    val id: String = UUID.randomUUID().toString(),
    val medicineId: String,
    val medicineName: String,
    val dosage: String,
    val scheduledTime: ReminderTime,
    val recordedAt: Long,
    val status: DoseStatus,
    val note: String? = null
)

data class AdherenceStats(
    val totalDoses: Int,
    val takenDoses: Int,
    val skippedDoses: Int,
    val missedDoses: Int,
    val adherenceRate: Float, // 0.0 to 1.0
    val streakDays: Int = 3
)

data class ActivityLog(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: ActivityType = ActivityType.DOSE_TAKEN
)

enum class ActivityType {
    DOSE_TAKEN,
    DOSE_SKIPPED,
    DOSE_MISSED,
    MEDICINE_ADDED,
    PARENT_CONNECTED,
    PARENT_NUDGE
}
