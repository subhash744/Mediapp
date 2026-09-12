package com.example.data.model

import java.util.UUID

enum class MedicineType(val displayName: String) {
    TABLET("Tablet"),
    CAPSULE("Capsule"),
    SYRUP("Syrup / Liquid"),
    INHALER("Inhaler"),
    DROPS("Drops"),
    INJECTION("Injection"),
    CREAM("Topical / Cream")
}

enum class Frequency(val displayName: String) {
    DAILY("Every Day"),
    SPECIFIC_DAYS("Specific Days"),
    AS_NEEDED("As Needed (PRN)")
}

enum class FoodTiming(val displayName: String) {
    BEFORE_FOOD("Before Food"),
    WITH_FOOD("With Food"),
    AFTER_FOOD("After Food"),
    NO_RESTRICTION("No Food Restriction")
}

data class ReminderTime(
    val id: String = UUID.randomUUID().toString(),
    val hour: Int,
    val minute: Int
) {
    val formatted: String
        get() {
            val period = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            return String.format("%d:%02d %s", displayHour, minute, period)
        }

    val militaryTime: String
        get() = String.format("%02d:%02d", hour, minute)
}

data class Medicine(
    val id: String = UUID.randomUUID().toString(),
    val childId: String = "child-arjun-01",
    val name: String,
    val strength: String = "",
    val dosage: String = "1 tablet",
    val type: MedicineType = MedicineType.TABLET,
    val frequency: Frequency = Frequency.DAILY,
    val timesPerDay: Int = 1,
    val reminderTimes: List<ReminderTime> = listOf(ReminderTime(hour = 8, minute = 0)),
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val isOngoing: Boolean = true,
    val foodTiming: FoodTiming = FoodTiming.AFTER_FOOD,
    val notes: String = "",
    val totalQuantity: Int = 30,
    val currentStock: Int = 30,
    val refillThreshold: Int = 5,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
