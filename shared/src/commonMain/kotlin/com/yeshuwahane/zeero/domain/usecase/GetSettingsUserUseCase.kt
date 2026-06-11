package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.domain.model.User
import com.yeshuwahane.zeero.domain.repository.UserRepository

class GetSettingsUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(): User? {
        return userRepository.getSettingsUser()
    }
}
