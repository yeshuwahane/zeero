package com.yeshuwahane.zeero.domain.model

enum class UserRole {
    CUSTOMER,
    SUPPLIER,
    ADMIN
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole
)
