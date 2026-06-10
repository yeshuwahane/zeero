package com.yeshuwahane.zeero.presentation.supplier

data class SupplierUiState(
    val title: String = "",
    val description: String = "",
    val priceString: String = "",
    val selectedCategory: String = "electronics",
    val isAuction: Boolean = false,
    val durationHoursString: String = "24",
    val isLoading: Boolean = false,
    val showSuccess: Boolean = false,
    val validationError: String = ""
)

sealed interface SupplierIntent {
    data class UpdateTitle(val title: String) : SupplierIntent
    data class UpdateDescription(val description: String) : SupplierIntent
    data class UpdatePrice(val price: String) : SupplierIntent
    data class UpdateCategory(val category: String) : SupplierIntent
    data class ToggleAuction(val isAuction: Boolean) : SupplierIntent
    data class UpdateDuration(val duration: String) : SupplierIntent
    object SubmitUpload : SupplierIntent
}
