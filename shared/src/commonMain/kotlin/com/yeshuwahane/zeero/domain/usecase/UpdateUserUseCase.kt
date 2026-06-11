package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.model.UserRole
import com.yeshuwahane.zeero.domain.repository.UserRepository

class UpdateUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(
        id: String,
        name: String,
        email: String,
        password: String,
        role: UserRole
    ): DataResource<String> {
        return userRepository.updateUser(id, name, email, password, role)
    }
}
