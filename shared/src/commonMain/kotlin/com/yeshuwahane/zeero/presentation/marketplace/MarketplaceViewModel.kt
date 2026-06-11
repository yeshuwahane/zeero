package com.yeshuwahane.zeero.presentation.marketplace

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.usecase.GetProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MarketplaceViewModel(
    private val getProductsUseCase: GetProductsUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(MarketplaceUiState())
    val state: StateFlow<MarketplaceUiState> = _state.asStateFlow()

    fun onIntent(intent: MarketplaceIntent) {
        when (intent) {
            MarketplaceIntent.LoadProducts -> {
                screenModelScope.launch {
                    val cached = getProductsUseCase.getCached().filter { p -> p.isApproved }
                    if (cached.isNotEmpty()) {
                        _state.update { it.copy(productsResource = DataResource.success(cached)) }
                    } else {
                        _state.update { it.copy(productsResource = DataResource.loading(data = it.productsResource.data)) }
                    }
                    val productsResult = getProductsUseCase()
                    val filteredResult = if (productsResult.isSuccess() && productsResult.data != null) {
                        DataResource.success(productsResult.data.filter { p -> p.isApproved })
                    } else {
                        DataResource.error(productsResult.error, productsResult.data?.filter { p -> p.isApproved })
                    }
                    _state.update { it.copy(productsResource = filteredResult) }
                }
            }
        }
    }
}
