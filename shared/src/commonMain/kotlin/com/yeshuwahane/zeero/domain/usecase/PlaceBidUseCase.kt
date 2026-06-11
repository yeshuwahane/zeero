package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.repository.ProductRepository

class PlaceBidUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(productId: String, amount: Double, bidderName: String): DataResource<String> {
        return productRepository.placeBid(productId, amount, bidderName)
    }
}
