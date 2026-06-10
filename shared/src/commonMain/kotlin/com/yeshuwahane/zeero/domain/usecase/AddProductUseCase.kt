package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.domain.model.Product
import com.yeshuwahane.zeero.domain.repository.ProductRepository

class AddProductUseCase(private val productRepository: ProductRepository) {
    operator fun invoke(
        title: String,
        description: String,
        price: Double,
        category: String,
        supplierId: String,
        isAuction: Boolean,
        durationHours: Int
    ): Product {
        return productRepository.addProduct(title, description, price, category, supplierId, isAuction, durationHours)
    }
}
