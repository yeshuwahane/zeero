package com.yeshuwahane.zeero.presentation.login

import com.yeshuwahane.zeero.domain.model.UserRole

data class LoginUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val selectedRole: UserRole = UserRole.CUSTOMER,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isLoggedIn: Boolean = false
)

sealed interface LoginIntent {
    data class UpdateName(val name: String) : LoginIntent
    data class UpdateEmail(val email: String) : LoginIntent
    data class UpdatePassword(val password: String) : LoginIntent
    data class SelectRole(val role: UserRole) : LoginIntent
    object SubmitLogin : LoginIntent
    object SubmitRegister : LoginIntent
    object DismissDialog : LoginIntent
}

sealed interface LoginEffect {
    object NavigateToCustomerMarketplace : LoginEffect
    object NavigateToSupplierDashboard : LoginEffect
    object NavigateToAdminDashboard : LoginEffect
}
