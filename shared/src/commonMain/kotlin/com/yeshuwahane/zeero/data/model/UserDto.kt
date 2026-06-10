package com.yeshuwahane.zeero.data.model

import com.yeshuwahane.zeero.domain.model.User
import com.yeshuwahane.zeero.domain.model.UserRole

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

fun UserDto.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        password = password,
        role = UserRole.valueOf(role.uppercase())
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        name = name,
        email = email,
        password = password,
        role = role.name
    )
}
