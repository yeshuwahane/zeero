package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.domain.repository.ProductRepository

class PlaceBidUseCase(private val productRepository: ProductRepository) {
    operator fun invoke(productId: String, amount: Double, bidderName: String): Boolean {
        return productRepository.placeBid(productId, amount, bidderName)
    }
}
