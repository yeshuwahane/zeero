package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.repository.ProductRepository

class UpdateProductUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(
        id: String,
        title: String,
        description: String,
        price: Double,
        category: String,
        supplierId: String,
        isAuction: Boolean,
        durationHours: Int
    ): DataResource<String> {
        return productRepository.updateProduct(
            id = id,
            title = title,
            description = description,
            price = price,
            category = category,
            supplierId = supplierId,
            isAuction = isAuction,
            durationHours = durationHours
        )
    }
}
