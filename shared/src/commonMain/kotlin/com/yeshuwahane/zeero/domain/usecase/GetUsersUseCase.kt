package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.domain.model.User
import com.yeshuwahane.zeero.domain.repository.UserRepository

class GetUsersUseCase(private val userRepository: UserRepository) {
    operator fun invoke(): List<User> {
        return userRepository.getUsers()
    }
}
