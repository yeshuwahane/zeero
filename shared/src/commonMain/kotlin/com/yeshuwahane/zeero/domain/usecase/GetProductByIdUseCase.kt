package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.model.Product
import com.yeshuwahane.zeero.domain.repository.ProductRepository

class GetProductByIdUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(id: String): DataResource<Product> {
        return productRepository.getProductById(id)
    }

    suspend fun getCached(id: String): Product? {
        return productRepository.getCachedProductById(id)
    }
}
