package com.yeshuwahane.zeero.presentation.detail

import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.model.Product

data class DetailUiState(
    val productResource: DataResource<Product> = DataResource.initial(),
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
    object DismissDialog : DetailIntent
}
