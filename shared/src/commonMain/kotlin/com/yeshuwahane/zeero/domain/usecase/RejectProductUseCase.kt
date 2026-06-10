package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.domain.repository.ProductRepository

class RejectProductUseCase(private val productRepository: ProductRepository) {
    operator fun invoke(productId: String) {
        productRepository.rejectProduct(productId)
    }
}
