package com.yeshuwahane.zeero.data.repository

import com.russhwolf.settings.Settings
import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.data.utils.apiCall
import com.yeshuwahane.zeero.domain.model.User
import com.yeshuwahane.zeero.domain.model.UserRole
import com.yeshuwahane.zeero.domain.repository.UserRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LoginRequestDto(val email: String, val password: String, val role: String)

@Serializable
data class RegisterRequestDto(val name: String, val email: String, val password: String, val role: String)

@Serializable
data class UserDto(val id: String, val name: String, val email: String, val password: String, val role: String)

class UserRepositoryImpl(
    private val httpClient: HttpClient,
    private val settings: Settings
) : UserRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val settingsUserKey = "settings_user"

    override suspend fun getUsers(): DataResource<List<User>> {
        val resource = apiCall<List<UserDto>> {
            httpClient.get("/api/auth/users")
        }
        return if (resource.isSuccess() && resource.data != null) {
            DataResource.success(resource.data.map {
                User(it.id, it.name, it.email, it.password, UserRole.valueOf(it.role.uppercase()))
            })
        } else {
            DataResource.error(resource.error, emptyList())
        }
    }

    override suspend fun login(email: String, password: String, role: UserRole): DataResource<User> {
        val resource = apiCall<UserDto> {
            httpClient.post("/api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDto(email, password, role.name))
            }
        }
        return if (resource.isSuccess() && resource.data != null) {
            val userDto = resource.data
            try {
                val userJson = json.encodeToString(UserDto.serializer(), userDto)
                settings.putString(settingsUserKey, userJson)
            } catch (e: Exception) {
                // Ignore serialization issues
            }
            DataResource.success(User(userDto.id, userDto.name, userDto.email, userDto.password, UserRole.valueOf(userDto.role.uppercase())))
        } else {
            DataResource.error(resource.error, null)
        }
    }

    override suspend fun register(name: String, email: String, password: String, role: UserRole): DataResource<User> {
        val resource = apiCall<UserDto> {
            httpClient.post("/api/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequestDto(name, email, password, role.name))
            }
        }
        return if (resource.isSuccess() && resource.data != null) {
            val userDto = resource.data
            try {
                val userJson = json.encodeToString(UserDto.serializer(), userDto)
                settings.putString(settingsUserKey, userJson)
            } catch (e: Exception) {
                // Ignore serialization issues
            }
            DataResource.success(User(userDto.id, userDto.name, userDto.email, userDto.password, UserRole.valueOf(userDto.role.uppercase())))
        } else {
            DataResource.error(resource.error, null)
        }
    }

    override suspend fun getSettingsUser(): User? {
        val userJson = settings.getStringOrNull(settingsUserKey) ?: return null
        return try {
            val userDto = json.decodeFromString(UserDto.serializer(), userJson)
            User(userDto.id, userDto.name, userDto.email, userDto.password, UserRole.valueOf(userDto.role.uppercase()))
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun clearSettingsUser(): Boolean {
        settings.remove(settingsUserKey)
        return true
    }

    override suspend fun deleteUser(id: String): DataResource<String> {
        val admin = getSettingsUser()
        return apiCall<String> {
            httpClient.delete("/api/auth/users/$id") {
                header("X-User-Id", admin?.id ?: "")
            }
        }
    }

    override suspend fun updateUser(id: String, name: String, email: String, password: String, role: UserRole): DataResource<String> {
        val admin = getSettingsUser()
        return apiCall<String> {
            httpClient.put("/api/auth/users/$id") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequestDto(name, email, password, role.name))
                header("X-User-Id", admin?.id ?: "")
            }
        }
    }
}
