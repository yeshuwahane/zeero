package com.yeshuwahane.zeero.presentation.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yeshuwahane.zeero.domain.usecase.LoginUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
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
            is LoginIntent.UpdateEmail -> _state.update { it.copy(email = intent.email, errorMessage = "") }
            is LoginIntent.UpdatePassword -> _state.update { it.copy(password = intent.password, errorMessage = "") }
            is LoginIntent.SelectRole -> _state.update { it.copy(selectedRole = intent.role, errorMessage = "") }
            is LoginIntent.SubmitLogin -> performLogin()
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
            delay(1000) // Simulated auth latency

            val matchedUser = loginUseCase(
                email = currentState.email,
                password = currentState.password,
                role = currentState.selectedRole
            )

            if (matchedUser != null) {
                _state.update { it.copy(isLoading = false, isLoggedIn = true) }
                when (currentState.selectedRole) {
                    com.yeshuwahane.zeero.domain.model.UserRole.CUSTOMER -> _effect.value = LoginEffect.NavigateToCustomerMarketplace
                    com.yeshuwahane.zeero.domain.model.UserRole.SUPPLIER -> _effect.value = LoginEffect.NavigateToSupplierDashboard
                    com.yeshuwahane.zeero.domain.model.UserRole.ADMIN -> _effect.value = LoginEffect.NavigateToAdminDashboard
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Invalid credentials or role mismatch. Check User Directory in Admin dashboard."
                    )
                }
            }
        }
    }
}
