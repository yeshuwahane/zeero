package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.repository.ProductRepository

class RejectProductUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(productId: String): DataResource<String> {
        return productRepository.rejectProduct(productId)
    }
}
