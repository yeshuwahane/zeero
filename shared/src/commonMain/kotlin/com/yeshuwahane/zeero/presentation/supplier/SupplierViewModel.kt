package com.yeshuwahane.zeero.presentation.supplier

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.usecase.AddProductUseCase
import com.yeshuwahane.zeero.domain.usecase.UploadProductImageUseCase
import com.yeshuwahane.zeero.domain.usecase.GetProductsUseCase
import com.yeshuwahane.zeero.domain.usecase.GetSettingsUserUseCase
import com.yeshuwahane.zeero.domain.usecase.RejectProductUseCase
import com.yeshuwahane.zeero.domain.usecase.UpdateProductUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SupplierViewModel(
    private val addProductUseCase: AddProductUseCase,
    private val uploadProductImageUseCase: UploadProductImageUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val getSettingsUserUseCase: GetSettingsUserUseCase,
    private val rejectProductUseCase: RejectProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(SupplierUiState())
    val state: StateFlow<SupplierUiState> = _state.asStateFlow()

    fun onIntent(intent: SupplierIntent) {
        when (intent) {
            is SupplierIntent.UpdateTitle -> _state.update { it.copy(title = intent.title, validationError = "", showSuccess = false) }
            is SupplierIntent.UpdateDescription -> _state.update { it.copy(description = intent.description, validationError = "", showSuccess = false) }
            is SupplierIntent.UpdatePrice -> _state.update { it.copy(priceString = intent.price, validationError = "", showSuccess = false) }
            is SupplierIntent.UpdateCategory -> _state.update { it.copy(selectedCategory = intent.category, validationError = "", showSuccess = false) }
            is SupplierIntent.ToggleAuction -> _state.update { it.copy(isAuction = intent.isAuction, validationError = "", showSuccess = false) }
            is SupplierIntent.UpdateDuration -> _state.update { it.copy(durationHoursString = intent.duration, validationError = "", showSuccess = false) }
            is SupplierIntent.AddSelectedImages -> {
                _state.update {
                    val updatedList = (it.selectedImages + intent.images).take(8)
                    it.copy(selectedImages = updatedList, validationError = "", showSuccess = false)
                }
            }
            is SupplierIntent.RemoveSelectedImage -> {
                _state.update {
                    if (intent.index in it.selectedImages.indices) {
                        val updatedList = it.selectedImages.toMutableList().apply { removeAt(intent.index) }
                        it.copy(selectedImages = updatedList, validationError = "", showSuccess = false)
                    } else {
                        it
                    }
                }
            }
            SupplierIntent.SubmitUpload -> performUpload()
            SupplierIntent.DismissDialog -> _state.update { it.copy(showSuccess = false, validationError = "") }
            is SupplierIntent.SelectTab -> _state.update { it.copy(selectedTabIndex = intent.index) }
            is SupplierIntent.EditProduct -> {
                val prod = intent.product
                _state.update {
                    it.copy(
                        title = prod.title,
                        description = prod.description,
                        priceString = prod.price.toString(),
                        selectedCategory = prod.imageUrl.split(",").firstOrNull() ?: "electronics",
                        isAuction = prod.isAuction,
                        durationHoursString = "24",
                        editingProductId = prod.id,
                        selectedTabIndex = 1, // Switch to form tab
                        validationError = "",
                        showSuccess = false
                    )
                }
            }
            is SupplierIntent.RemoveProduct -> {
                screenModelScope.launch {
                    val result = rejectProductUseCase(intent.productId)
                    if (result.isSuccess()) {
                        loadData()
                    } else {
                        _state.update { it.copy(validationError = result.error?.message ?: "Failed to remove product.") }
                    }
                }
            }
            SupplierIntent.CancelEdit -> {
                _state.update {
                    it.copy(
                        title = "",
                        description = "",
                        priceString = "",
                        selectedCategory = "electronics",
                        isAuction = false,
                        durationHoursString = "24",
                        editingProductId = null,
                        selectedTabIndex = 0, // Switch back to list tab
                        selectedImages = emptyList(),
                        validationError = "",
                        showSuccess = false
                    )
                }
            }
            SupplierIntent.LoadData -> loadData()
        }
    }

    private fun loadData() {
        _state.update { it.copy(isLoading = true) }
        screenModelScope.launch {
            val user = getSettingsUserUseCase()
            val productsResult = getProductsUseCase(forceRefresh = true)
            if (productsResult.isSuccess() && productsResult.data != null) {
                val supplierProducts = if (user != null) {
                    productsResult.data.filter { it.supplierId == user.id }
                } else {
                    productsResult.data.filter { it.supplierId == "sup_current" }
                }
                _state.update {
                    it.copy(
                        supplierProducts = supplierProducts,
                        currentSupplier = user,
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun performUpload() {
        val currentState = _state.value
        val price = currentState.priceString.toDoubleOrNull()
        val duration = currentState.durationHoursString.toIntOrNull()

        if (currentState.title.isBlank()) {
            _state.update { it.copy(validationError = "Please enter product title.") }
        } else if (currentState.description.isBlank()) {
            _state.update { it.copy(validationError = "Please enter description.") }
        } else if (price == null || price <= 0.0) {
            _state.update { it.copy(validationError = "Please enter a valid base price.") }
        } else if (currentState.isAuction && (duration == null || duration <= 0)) {
            _state.update { it.copy(validationError = "Please enter a valid auction duration in hours.") }
        } else {
            _state.update { it.copy(isLoading = true, validationError = "") }
            screenModelScope.launch {
                val imageList = mutableListOf<String>()
                
                // Upload each selected gallery image sequentially
                for (imagePair in currentState.selectedImages) {
                    val uploadResult = uploadProductImageUseCase(imagePair.second)
                    if (uploadResult.isSuccess() && uploadResult.data != null) {
                        imageList.add(uploadResult.data)
                    }
                }
                
                // Fallback to beautiful categories if no gallery images were selected/uploaded
                if (imageList.isEmpty()) {
                    imageList.add(currentState.selectedCategory)
                    val derivedTheme = when (currentState.selectedCategory) {
                        "electronics" -> "audio"
                        "audio" -> "fashion"
                        "fashion" -> "others"
                        else -> "electronics"
                    }
                    imageList.add(derivedTheme)
                }
                
                val finalImageUrl = imageList.joinToString(",")
                val supplierId = currentState.currentSupplier?.id ?: "sup_current"

                val isEditing = currentState.editingProductId != null
                val result = if (isEditing) {
                    updateProductUseCase(
                        id = currentState.editingProductId!!,
                        title = currentState.title,
                        description = currentState.description,
                        price = price,
                        category = finalImageUrl,
                        supplierId = supplierId,
                        isAuction = currentState.isAuction,
                        durationHours = duration ?: 24
                    )
                } else {
                    addProductUseCase(
                        title = currentState.title,
                        description = currentState.description,
                        price = price,
                        category = finalImageUrl,
                        supplierId = supplierId,
                        isAuction = currentState.isAuction,
                        durationHours = duration ?: 24
                    )
                }

                if (result.isSuccess()) {
                    // Reset editing form fields, show success popup, switch back to listings
                    _state.update {
                        it.copy(
                            title = "",
                            description = "",
                            priceString = "",
                            selectedCategory = "electronics",
                            isAuction = false,
                            durationHoursString = "24",
                            selectedImages = emptyList(),
                            editingProductId = null,
                            showSuccess = true,
                            isLoading = false,
                            selectedTabIndex = 0
                        )
                    }
                    loadData()
                } else {
                    val errorMsg = result.error?.message ?: "Failed to save product listing."
                    _state.update { it.copy(isLoading = false, validationError = errorMsg) }
                }
            }
        }
    }
}
