package com.yeshuwahane.zeero.presentation.marketplace

import com.yeshuwahane.zeero.domain.model.Product

data class MarketplaceUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false
)

sealed interface MarketplaceIntent {
    object LoadProducts : MarketplaceIntent
}
