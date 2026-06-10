package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.domain.model.Product
import com.yeshuwahane.zeero.domain.repository.ProductRepository

class GetProductsUseCase(private val productRepository: ProductRepository) {
    operator fun invoke(): List<Product> {
        return productRepository.getProducts()
    }
}
