package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.domain.repository.ProductRepository

class ApproveProductUseCase(private val productRepository: ProductRepository) {
    operator fun invoke(productId: String) {
        productRepository.approveProduct(productId)
    }
}
