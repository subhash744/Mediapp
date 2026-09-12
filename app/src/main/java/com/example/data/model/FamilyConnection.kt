package com.example.data.model

enum class ConnectionStatus {
    DISCONNECTED,
    WAITING_FOR_PARENT,
    PARENT_WANTS_TO_CONNECT,
    WAITING_FOR_CHILD_APPROVAL,
    CONNECTED
}

data class FamilyMember(
    val id: String,
    val name: String,
    val maskedEmail: String,
    val fullEmail: String,
    val role: UserRole,
    val connectedSince: String = "September 2026",
    val permissions: List<String> = listOf("View medicines", "Track adherence", "Send reminder nudges")
)

data class PairingInfo(
    val code: String = "482 731",
    val generatedAt: Long = System.currentTimeMillis(),
    val expiresInSeconds: Long = 600, // 10 minutes
    val pendingParent: FamilyMember? = null,
    val pendingChild: FamilyMember? = null,
    val status: ConnectionStatus = ConnectionStatus.WAITING_FOR_PARENT
)
