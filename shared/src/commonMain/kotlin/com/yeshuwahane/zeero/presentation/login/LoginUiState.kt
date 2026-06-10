package com.yeshuwahane.zeero.presentation.login

import com.yeshuwahane.zeero.domain.model.UserRole

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val selectedRole: UserRole = UserRole.CUSTOMER,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isLoggedIn: Boolean = false
)

sealed interface LoginIntent {
    data class UpdateEmail(val email: String) : LoginIntent
    data class UpdatePassword(val password: String) : LoginIntent
    data class SelectRole(val role: UserRole) : LoginIntent
    object SubmitLogin : LoginIntent
}

sealed interface LoginEffect {
    object NavigateToCustomerMarketplace : LoginEffect
    object NavigateToSupplierDashboard : LoginEffect
    object NavigateToAdminDashboard : LoginEffect
}
