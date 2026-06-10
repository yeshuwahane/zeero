package com.yeshuwahane.zeero.domain.repository

import com.yeshuwahane.zeero.domain.model.User
import com.yeshuwahane.zeero.domain.model.UserRole

interface UserRepository {
    fun getUsers(): List<User>
    fun login(email: String, password: String, role: UserRole): User?
}
