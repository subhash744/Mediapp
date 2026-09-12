package com.example.data.repository

import com.example.data.model.Medicine
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AppDataContainer {
    val authService: LocalAuthService = LocalAuthService()
    val profileService: LocalProfileService = LocalProfileService(authService)
    val medicineRepository: LocalMedicineRepository = LocalMedicineRepository()
    val scheduleRepository: LocalScheduleRepository = LocalScheduleRepository(medicineRepository)
    val doseRepository: LocalDoseRepository = LocalDoseRepository(medicineRepository)
    val familyConnectionRepository: LocalFamilyConnectionRepository = LocalFamilyConnectionRepository()
    val pairingService: LocalPairingService = LocalPairingService(familyConnectionRepository)
    val syncService: LocalSyncService = LocalSyncService()

    fun resetAllDemoData() {
        medicineRepository.resetData()
        doseRepository.resetData()
        pairingService.resetData()
        familyConnectionRepository.resetData()
    }

    fun setEmptyStateForTesting() {
        medicineRepository.clearAll()
        doseRepository.clearAll()
    }

    fun onMedicineAdded(medicine: Medicine) {
        doseRepository.syncFromNewMedicine(medicine)
    }

    fun switchRoleForTesting(role: UserRole) {
        CoroutineScope(Dispatchers.Main).launch {
            authService.switchRole(role)
        }
    }
}
