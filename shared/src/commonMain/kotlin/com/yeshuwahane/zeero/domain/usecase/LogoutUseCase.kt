package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.domain.repository.UserRepository

class LogoutUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(): Boolean {
        return userRepository.clearSettingsUser()
    }
}
