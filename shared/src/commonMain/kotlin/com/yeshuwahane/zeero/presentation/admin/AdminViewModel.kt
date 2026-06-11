package com.yeshuwahane.zeero.presentation.admin

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.usecase.ApproveProductUseCase
import com.yeshuwahane.zeero.domain.usecase.GetProductsUseCase
import com.yeshuwahane.zeero.domain.usecase.GetUsersUseCase
import com.yeshuwahane.zeero.domain.usecase.RejectProductUseCase
import com.yeshuwahane.zeero.domain.usecase.GetSettingsUserUseCase
import com.yeshuwahane.zeero.domain.usecase.DeleteUserUseCase
import com.yeshuwahane.zeero.domain.usecase.UpdateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val approveProductUseCase: ApproveProductUseCase,
    private val rejectProductUseCase: RejectProductUseCase,
    private val getSettingsUserUseCase: GetSettingsUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(AdminUiState())
    val state: StateFlow<AdminUiState> = _state.asStateFlow()

    fun onIntent(intent: AdminIntent) {
        when (intent) {
            is AdminIntent.SelectTab -> _state.update { it.copy(selectedTabIndex = intent.index) }
            is AdminIntent.ApproveProduct -> {
                _state.update { it.copy(processingProductId = intent.id) }
                screenModelScope.launch {
                    val result = approveProductUseCase(intent.id)
                    if (result.isSuccess()) {
                        _state.update { it.copy(showSuccessMessage = "Product approved successfully!", processingProductId = null) }
                    } else {
                        _state.update { it.copy(showErrorMessage = result.error?.message ?: "Failed to approve product.", processingProductId = null) }
                    }
                    loadData()
                }
            }
            is AdminIntent.RejectProduct -> {
                _state.update { it.copy(processingProductId = intent.id) }
                screenModelScope.launch {
                    val result = rejectProductUseCase(intent.id)
                    if (result.isSuccess()) {
                        _state.update { it.copy(showSuccessMessage = "Product rejected and removed successfully!", processingProductId = null) }
                    } else {
                        _state.update { it.copy(showErrorMessage = result.error?.message ?: "Failed to reject product.", processingProductId = null) }
                    }
                    loadData()
                }
            }
            is AdminIntent.DeleteUser -> {
                _state.update { it.copy(deletingUserId = intent.id) }
                screenModelScope.launch {
                    val result = deleteUserUseCase(intent.id)
                    if (result.isSuccess()) {
                        _state.update { it.copy(showSuccessMessage = "User deleted successfully!", deletingUserId = null) }
                    } else {
                        _state.update { it.copy(showErrorMessage = result.error?.message ?: "Failed to delete user.", deletingUserId = null) }
                    }
                    loadData()
                }
            }
            is AdminIntent.EditUser -> {
                screenModelScope.launch {
                    val result = updateUserUseCase(
                        id = intent.id,
                        name = intent.name,
                        email = intent.email,
                        password = intent.password,
                        role = intent.role
                    )
                    if (result.isSuccess()) {
                        _state.update { it.copy(showSuccessMessage = "User updated successfully!") }
                    } else {
                        _state.update { it.copy(showErrorMessage = result.error?.message ?: "Failed to update user.") }
                    }
                    loadData()
                }
            }
            AdminIntent.LoadData -> loadData()
            AdminIntent.DismissDialog -> {
                _state.update { it.copy(showSuccessMessage = null, showErrorMessage = null) }
            }
        }
    }

    private fun loadData() {
        _state.update {
            it.copy(
                pendingProductsResource = DataResource.loading(data = it.pendingProductsResource.data),
                usersResource = DataResource.loading(data = it.usersResource.data)
            )
        }
        screenModelScope.launch {
            val adminUser = getSettingsUserUseCase()
            val productsResult = getProductsUseCase(forceRefresh = true)
            val usersResult = getUsersUseCase()

            val pendingProductsResult = if (productsResult.isSuccess() && productsResult.data != null) {
                DataResource.success(productsResult.data.filter { !it.isApproved })
            } else {
                DataResource.error(productsResult.error, productsResult.data?.filter { !it.isApproved })
            }

            _state.update {
                it.copy(
                    pendingProductsResource = pendingProductsResult,
                    usersResource = usersResult,
                    currentAdminUser = adminUser
                )
            }
        }
    }
}
