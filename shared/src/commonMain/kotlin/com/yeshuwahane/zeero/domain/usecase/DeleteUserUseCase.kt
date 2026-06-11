package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.repository.UserRepository

class DeleteUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(id: String): DataResource<String> {
        return userRepository.deleteUser(id)
    }
}
