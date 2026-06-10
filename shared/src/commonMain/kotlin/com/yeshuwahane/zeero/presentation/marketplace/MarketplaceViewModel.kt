package com.yeshuwahane.zeero.presentation.marketplace

import cafe.adriel.voyager.core.model.ScreenModel
import com.yeshuwahane.zeero.domain.usecase.GetProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MarketplaceViewModel(
    private val getProductsUseCase: GetProductsUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(MarketplaceUiState())
    val state: StateFlow<MarketplaceUiState> = _state.asStateFlow()

    fun onIntent(intent: MarketplaceIntent) {
        when (intent) {
            MarketplaceIntent.LoadProducts -> {
                _state.update { it.copy(products = getProductsUseCase().filter { p -> p.isApproved }) }
            }
        }
    }
}
