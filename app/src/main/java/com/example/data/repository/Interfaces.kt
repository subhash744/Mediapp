package com.example.data.repository

import com.example.data.model.ActivityLog
import com.example.data.model.AdherenceStats
import com.example.data.model.AuthSession
import com.example.data.model.ConnectionStatus
import com.example.data.model.DoseRecord
import com.example.data.model.FamilyMember
import com.example.data.model.HistoryLog
import com.example.data.model.Medicine
import com.example.data.model.PairingInfo
import com.example.data.model.ReminderTime
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Supabase-Ready Auth Service Interface
 * Can later be swapped with Supabase GoTrue Auth implementation.
 */
interface AuthService {
    val sessionState: StateFlow<AuthSession>
    suspend fun signUp(name: String, email: String, password: String, role: UserRole): Result<UserProfile>
    suspend fun signIn(email: String, password: String, role: UserRole): Result<UserProfile>
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun verifyEmail(code: String): Result<Unit>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun switchRole(role: UserRole)
}

/**
 * Supabase-Ready Profile Service Interface
 * Maps to `public.profiles` table in Supabase.
 */
interface ProfileService {
    val currentProfile: StateFlow<UserProfile?>
    val profile: StateFlow<UserProfile>
    suspend fun updateProfile(name: String, age: Int?): Result<UserProfile>
    suspend fun updateProfileName(name: String): Result<UserProfile>
}

/**
 * Supabase-Ready Medicine Repository Interface
 * Maps to `public.medicines` table in Supabase with RLS policies.
 */
interface MedicineRepository {
    fun getMedicinesFlow(): Flow<List<Medicine>>
    suspend fun getMedicineById(id: String): Medicine?
    suspend fun addMedicine(medicine: Medicine): Result<Medicine>
    suspend fun updateMedicine(medicine: Medicine): Result<Medicine>
    suspend fun deleteMedicine(id: String): Result<Unit>
    suspend fun archiveMedicine(id: String, archive: Boolean): Result<Unit>
}

/**
 * Supabase-Ready Schedule Repository Interface
 * Maps to `public.medicine_schedules` table in Supabase.
 */
interface ScheduleRepository {
    fun getReminderTimesForMedicine(medicineId: String): Flow<List<ReminderTime>>
    suspend fun updateReminderTimes(medicineId: String, times: List<ReminderTime>): Result<Unit>
}

/**
 * Supabase-Ready Dose Repository Interface
 * Maps to `public.dose_logs` table in Supabase.
 */
interface DoseRepository {
    fun getTodayDosesFlow(): Flow<List<DoseRecord>>
    fun getAllDosesHistoryFlow(): Flow<List<DoseRecord>>
    fun getHistoryLogsFlow(): Flow<List<HistoryLog>>
    fun getAdherenceStatsFlow(): Flow<AdherenceStats>
    fun getActivityLogsFlow(): Flow<List<ActivityLog>>
    suspend fun recordTakeDose(doseId: String): Result<Unit>
    suspend fun recordSkipDose(doseId: String, reason: String): Result<Unit>
    suspend fun undoDoseAction(doseId: String): Result<Unit>
    suspend fun simulateDoseDueNow(): Result<Unit>
}

/**
 * Supabase-Ready Pairing Service Interface
 * Handles temporary pairing code generation, countdown, and matching.
 */
interface PairingService {
    val pairingState: StateFlow<PairingInfo>
    fun generateNewPairingCode(): String
    suspend fun simulateIncomingParentRequest(parent: FamilyMember)
    suspend fun acceptParentConnection(): Result<Unit>
    suspend fun rejectParentConnection(): Result<Unit>
    suspend fun validateChildPairingCode(code: String): Result<FamilyMember>
    suspend fun submitParentConnectionRequest(childId: String): Result<Unit>
    suspend fun simulateChildApproval(): Result<Unit>
    suspend fun submitCodeFromParent(code: String): Boolean
    suspend fun cancelPairing(): Result<Unit>
}

/**
 * Supabase-Ready Family Connection Repository
 * Maps to `public.family_connections` table in Supabase.
 */
interface FamilyConnectionRepository {
    val connectionStatus: StateFlow<ConnectionStatus>
    val connectedParent: StateFlow<FamilyMember?>
    val connectedChild: StateFlow<FamilyMember?>
    suspend fun disconnectFamily(): Result<Unit>
    suspend fun sendParentNudge(message: String): Result<Unit>
}

/**
 * Supabase-Ready Realtime Sync Service
 * Manages Postgres change listeners and channel status.
 */
interface SyncService {
    val isSyncing: StateFlow<Boolean>
    val lastSyncedTimestamp: StateFlow<Long>
    suspend fun triggerManualSync(): Result<Unit>
}
