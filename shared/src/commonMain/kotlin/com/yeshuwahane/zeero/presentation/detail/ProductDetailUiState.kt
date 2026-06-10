package com.yeshuwahane.zeero.presentation.detail

import com.yeshuwahane.zeero.domain.model.Product

data class DetailUiState(
    val product: Product? = null,
    val bidderName: String = "",
    val bidAmount: String = "",
    val currentTimeMillis: Long = 0L,
    val validationError: String = "",
    val showSuccess: Boolean = false
)

sealed interface DetailIntent {
    data class LoadProduct(val id: String) : DetailIntent
    data class UpdateBidderName(val name: String) : DetailIntent
    data class UpdateBidAmount(val amount: String) : DetailIntent
    data class SubmitBid(val productId: String) : DetailIntent
    data class TickTimer(val time: Long) : DetailIntent
    object SubmitBuyout : DetailIntent
}
