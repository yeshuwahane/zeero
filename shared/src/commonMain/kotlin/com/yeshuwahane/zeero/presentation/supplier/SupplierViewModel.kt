package com.yeshuwahane.zeero.presentation.supplier

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yeshuwahane.zeero.domain.usecase.AddProductUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SupplierViewModel(
    private val addProductUseCase: AddProductUseCase
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
            SupplierIntent.SubmitUpload -> performUpload()
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
                delay(1200) // Simulated upload delay

                addProductUseCase(
                    title = currentState.title,
                    description = currentState.description,
                    price = price,
                    category = currentState.selectedCategory,
                    supplierId = "sup_current",
                    isAuction = currentState.isAuction,
                    durationHours = duration ?: 24
                )

                _state.value = SupplierUiState(showSuccess = true) // Reset form with success marker
            }
        }
    }
}
