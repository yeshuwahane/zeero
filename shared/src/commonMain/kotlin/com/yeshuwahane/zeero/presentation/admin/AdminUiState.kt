package com.yeshuwahane.zeero.presentation.admin

import com.yeshuwahane.zeero.domain.model.Product
import com.yeshuwahane.zeero.domain.model.User

data class AdminUiState(
    val selectedTabIndex: Int = 0,
    val pendingProducts: List<Product> = emptyList(),
    val users: List<User> = emptyList()
)

sealed interface AdminIntent {
    data class SelectTab(val index: Int) : AdminIntent
    data class ApproveProduct(val id: String) : AdminIntent
    data class RejectProduct(val id: String) : AdminIntent
    object LoadData : AdminIntent
}
