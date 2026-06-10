package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.domain.model.Product
import com.yeshuwahane.zeero.domain.repository.ProductRepository

class GetProductByIdUseCase(private val productRepository: ProductRepository) {
    operator fun invoke(id: String): Product? {
        return productRepository.getProductById(id)
    }
}
