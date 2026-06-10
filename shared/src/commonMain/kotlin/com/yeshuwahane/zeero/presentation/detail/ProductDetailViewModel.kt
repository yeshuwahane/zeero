package com.yeshuwahane.zeero.presentation.detail

import cafe.adriel.voyager.core.model.ScreenModel
import com.yeshuwahane.zeero.domain.usecase.GetProductByIdUseCase
import com.yeshuwahane.zeero.domain.usecase.PlaceBidUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProductDetailViewModel(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val placeBidUseCase: PlaceBidUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    fun onIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.LoadProduct -> {
                val prod = getProductByIdUseCase(intent.id)
                _state.update { it.copy(product = prod, validationError = "", showSuccess = false) }
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
                val prod = currentState.product
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
                        val success = placeBidUseCase(intent.productId, amount, currentState.bidderName)
                        if (success) {
                            val updatedProduct = getProductByIdUseCase(intent.productId)
                            _state.update {
                                it.copy(
                                    product = updatedProduct,
                                    bidAmount = "",
                                    validationError = "",
                                    showSuccess = true
                                )
                            }
                        }
                    }
                }
            }
            DetailIntent.SubmitBuyout -> {
                _state.update { it.copy(showSuccess = true) }
            }
        }
    }
}
