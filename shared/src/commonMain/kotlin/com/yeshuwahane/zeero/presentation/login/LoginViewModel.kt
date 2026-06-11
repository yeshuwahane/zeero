package com.yeshuwahane.zeero.presentation.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yeshuwahane.zeero.domain.usecase.LoginUseCase
import com.yeshuwahane.zeero.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val _effect = MutableStateFlow<LoginEffect?>(null)
    val effect: StateFlow<LoginEffect?> = _effect.asStateFlow()

    fun resetEffect() {
        _effect.value = null
    }

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UpdateName -> _state.update { it.copy(name = intent.name, errorMessage = "") }
            is LoginIntent.UpdateEmail -> _state.update { it.copy(email = intent.email, errorMessage = "") }
            is LoginIntent.UpdatePassword -> _state.update { it.copy(password = intent.password, errorMessage = "") }
            is LoginIntent.SelectRole -> _state.update { it.copy(selectedRole = intent.role, errorMessage = "") }
            is LoginIntent.SubmitLogin -> performLogin()
            is LoginIntent.SubmitRegister -> performRegister()
        }
    }

    private fun performLogin() {
        val currentState = _state.value
        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            _state.update { it.copy(errorMessage = "Credentials cannot be blank.") }
            return
        }
        _state.update { it.copy(isLoading = true) }
        screenModelScope.launch {
            val matchedUserResource = loginUseCase(
                email = currentState.email,
                password = currentState.password,
                role = currentState.selectedRole
            )

            if (matchedUserResource.isSuccess() && matchedUserResource.data != null) {
                _state.update { it.copy(isLoading = false, isLoggedIn = true) }
                when (currentState.selectedRole) {
                    com.yeshuwahane.zeero.domain.model.UserRole.CUSTOMER -> _effect.value = LoginEffect.NavigateToCustomerMarketplace
                    com.yeshuwahane.zeero.domain.model.UserRole.SUPPLIER -> _effect.value = LoginEffect.NavigateToSupplierDashboard
                    com.yeshuwahane.zeero.domain.model.UserRole.ADMIN -> _effect.value = LoginEffect.NavigateToAdminDashboard
                }
            } else {
                val errorMsg = matchedUserResource.error?.message ?: "Invalid credentials or role mismatch. Check User Directory in Admin dashboard."
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }
            }
        }
    }

    private fun performRegister() {
        val currentState = _state.value
        if (currentState.name.isBlank() || currentState.email.isBlank() || currentState.password.isBlank()) {
            _state.update { it.copy(errorMessage = "All fields are required for sign up.") }
            return
        }
        _state.update { it.copy(isLoading = true) }
        screenModelScope.launch {
            val registeredUserResource = registerUseCase(
                name = currentState.name,
                email = currentState.email,
                password = currentState.password,
                role = currentState.selectedRole
            )

            if (registeredUserResource.isSuccess() && registeredUserResource.data != null) {
                _state.update { it.copy(isLoading = false, isLoggedIn = true) }
                when (currentState.selectedRole) {
                    com.yeshuwahane.zeero.domain.model.UserRole.CUSTOMER -> _effect.value = LoginEffect.NavigateToCustomerMarketplace
                    com.yeshuwahane.zeero.domain.model.UserRole.SUPPLIER -> _effect.value = LoginEffect.NavigateToSupplierDashboard
                    com.yeshuwahane.zeero.domain.model.UserRole.ADMIN -> _effect.value = LoginEffect.NavigateToAdminDashboard
                }
            } else {
                val errorMsg = registeredUserResource.error?.message ?: "Registration failed. Email might already exist."
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }
            }
        }
    }
}
