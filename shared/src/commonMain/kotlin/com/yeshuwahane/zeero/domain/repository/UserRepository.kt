package com.yeshuwahane.zeero.domain.repository

import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.model.User
import com.yeshuwahane.zeero.domain.model.UserRole

interface UserRepository {
    suspend fun getUsers(): DataResource<List<User>>
    suspend fun login(email: String, password: String, role: UserRole): DataResource<User>
    suspend fun register(name: String, email: String, password: String, role: UserRole): DataResource<User>
    suspend fun getSettingsUser(): User?
    suspend fun clearSettingsUser(): Boolean
    suspend fun deleteUser(id: String): DataResource<String>
    suspend fun updateUser(id: String, name: String, email: String, password: String, role: UserRole): DataResource<String>
}
