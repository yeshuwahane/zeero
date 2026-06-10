package com.yeshuwahane.zeero.data.repository

import com.yeshuwahane.zeero.data.MockData
import com.yeshuwahane.zeero.domain.model.User
import com.yeshuwahane.zeero.domain.model.UserRole
import com.yeshuwahane.zeero.domain.repository.UserRepository

class UserRepositoryImpl : UserRepository {
    override fun getUsers(): List<User> {
        return MockData.users
    }

    override fun login(email: String, password: String, role: UserRole): User? {
        return MockData.users.firstOrNull {
            it.email.equals(email, ignoreCase = true) &&
                    it.password == password &&
                    it.role == role
        }
    }
}
