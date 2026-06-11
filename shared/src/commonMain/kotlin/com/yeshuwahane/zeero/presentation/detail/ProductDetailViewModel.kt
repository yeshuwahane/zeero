package com.yeshuwahane.zeero.presentation.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.usecase.GetProductByIdUseCase
import com.yeshuwahane.zeero.domain.usecase.PlaceBidUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val placeBidUseCase: PlaceBidUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    fun onIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.LoadProduct -> {
                screenModelScope.launch {
                    val cached = getProductByIdUseCase.getCached(intent.id)
                    if (cached != null) {
                        _state.update { it.copy(productResource = DataResource.success(cached), validationError = "", showSuccess = false) }
                    } else {
                        _state.update { it.copy(productResource = DataResource.loading(data = it.productResource.data), validationError = "", showSuccess = false) }
                    }
                    val prodResult = getProductByIdUseCase(intent.id)
                    _state.update { it.copy(productResource = prodResult) }
                }
            }
            is DetailIntent.UpdateBidderName -> {
                _state.update { it.copy(bidderName = intent.name, validationError = "") }
            }
            is DetailIntent.UpdateBidAmount -> {
                _state.update { it.copy(bidAmount = intent.amount, validationError = "") }
            }
            is DetailIntent.TickTimer -> {
                _state.update { it.copy(currentTimeMillis = intent.time) }
            }
            is DetailIntent.SubmitBid -> {
                val currentState = _state.value
                val prod = currentState.productResource.data
                if (prod != null) {
                    val amount = currentState.bidAmount.toDoubleOrNull()
                    val minRequired = if (prod.currentHighestBid > 0.0) prod.currentHighestBid else prod.price

                    if (currentState.bidderName.isBlank()) {
                        _state.update { it.copy(validationError = "Please enter your name.") }
                    } else if (amount == null) {
                        _state.update { it.copy(validationError = "Please enter a valid numeric amount.") }
                    } else if (amount <= minRequired) {
                        _state.update { it.copy(validationError = "Bid must be greater than $$minRequired") }
                    } else {
                        screenModelScope.launch {
                            val placeBidResult = placeBidUseCase(intent.productId, amount, currentState.bidderName)
                            if (placeBidResult.isSuccess()) {
                                val updatedProductResult = getProductByIdUseCase(intent.productId)
                                _state.update {
                                    it.copy(
                                        productResource = updatedProductResult,
                                        bidAmount = "",
                                        validationError = "",
                                        showSuccess = true
                                    )
                                }
                            } else {
                                val errorMsg = placeBidResult.error?.message ?: "Failed to place bid."
                                _state.update { it.copy(validationError = errorMsg) }
                            }
                        }
                    }
                }
            }
            DetailIntent.SubmitBuyout -> {
                _state.update { it.copy(showSuccess = true) }
            }
            DetailIntent.DismissDialog -> {
                _state.update { it.copy(showSuccess = false, validationError = "") }
            }
        }
    }
}
