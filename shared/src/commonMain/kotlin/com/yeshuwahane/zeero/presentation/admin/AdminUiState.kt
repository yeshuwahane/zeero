package com.yeshuwahane.zeero.presentation.admin

import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.model.Product
import com.yeshuwahane.zeero.domain.model.User
import com.yeshuwahane.zeero.domain.model.UserRole

data class AdminUiState(
    val selectedTabIndex: Int = 0,
    val pendingProductsResource: DataResource<List<Product>> = DataResource.initial(),
    val usersResource: DataResource<List<User>> = DataResource.initial(),
    val currentAdminUser: User? = null,
    val showSuccessMessage: String? = null,
    val showErrorMessage: String? = null
)

sealed interface AdminIntent {
    data class SelectTab(val index: Int) : AdminIntent
    data class ApproveProduct(val id: String) : AdminIntent
    data class RejectProduct(val id: String) : AdminIntent
    data class DeleteUser(val id: String) : AdminIntent
    data class EditUser(
        val id: String,
        val name: String,
        val email: String,
        val password: String,
        val role: UserRole
    ) : AdminIntent
    object LoadData : AdminIntent
    object DismissDialog : AdminIntent
}
