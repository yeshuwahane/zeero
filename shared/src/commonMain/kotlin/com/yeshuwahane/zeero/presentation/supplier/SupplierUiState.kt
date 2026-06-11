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
    val validationError: String = "",
    val selectedImages: List<Pair<String, ByteArray>> = emptyList()
)

sealed interface SupplierIntent {
    data class UpdateTitle(val title: String) : SupplierIntent
    data class UpdateDescription(val description: String) : SupplierIntent
    data class UpdatePrice(val price: String) : SupplierIntent
    data class UpdateCategory(val category: String) : SupplierIntent
    data class ToggleAuction(val isAuction: Boolean) : SupplierIntent
    data class UpdateDuration(val duration: String) : SupplierIntent
    data class AddSelectedImages(val images: List<Pair<String, ByteArray>>) : SupplierIntent
    data class RemoveSelectedImage(val index: Int) : SupplierIntent
    object SubmitUpload : SupplierIntent
    object DismissDialog : SupplierIntent
}
