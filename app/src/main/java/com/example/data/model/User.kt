package com.example.data.model

import java.util.UUID

enum class UserRole {
    CHILD,
    PARENT
}

data class UserProfile(
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val fullName: String,
    val role: UserRole,
    val age: Int? = null,
    val avatarUrl: String? = null,
    val isEmailVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val name: String get() = fullName
}

data class AuthSession(
    val user: UserProfile? = null,
    val token: String? = null,
    val isAuthenticated: Boolean = false
) {
    val role: UserRole get() = user?.role ?: UserRole.CHILD
}
