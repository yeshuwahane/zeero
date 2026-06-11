package com.yeshuwahane.zeero.presentation.marketplace

import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.model.Product

data class MarketplaceUiState(
    val productsResource: DataResource<List<Product>> = DataResource.initial()
)

sealed interface MarketplaceIntent {
    object LoadProducts : MarketplaceIntent
}
