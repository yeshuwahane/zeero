package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.domain.model.User
import com.yeshuwahane.zeero.domain.model.UserRole
import com.yeshuwahane.zeero.domain.repository.UserRepository

class LoginUseCase(private val userRepository: UserRepository) {
    operator fun invoke(email: String, password: String, role: UserRole): User? {
        return userRepository.login(email, password, role)
    }
}
