package com.yeshuwahane.zeero.presentation.admin

import cafe.adriel.voyager.core.model.ScreenModel
import com.yeshuwahane.zeero.domain.usecase.ApproveProductUseCase
import com.yeshuwahane.zeero.domain.usecase.GetProductsUseCase
import com.yeshuwahane.zeero.domain.usecase.GetUsersUseCase
import com.yeshuwahane.zeero.domain.usecase.RejectProductUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AdminViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val approveProductUseCase: ApproveProductUseCase,
    private val rejectProductUseCase: RejectProductUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(AdminUiState())
    val state: StateFlow<AdminUiState> = _state.asStateFlow()

    fun onIntent(intent: AdminIntent) {
        when (intent) {
            is AdminIntent.SelectTab -> _state.update { it.copy(selectedTabIndex = intent.index) }
            is AdminIntent.ApproveProduct -> {
                approveProductUseCase(intent.id)
                loadMockData()
            }
            is AdminIntent.RejectProduct -> {
                rejectProductUseCase(intent.id)
                loadMockData()
            }
            AdminIntent.LoadData -> loadMockData()
        }
    }

    private fun loadMockData() {
        _state.update {
            it.copy(
                pendingProducts = getProductsUseCase().filter { !it.isApproved },
                users = getUsersUseCase()
            )
        }
    }
}
