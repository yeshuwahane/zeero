package com.yeshuwahane.zeero.domain.usecase

import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.model.Product
import com.yeshuwahane.zeero.domain.repository.ProductRepository

class GetProductsUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(forceRefresh: Boolean = false): DataResource<List<Product>> {
        return productRepository.getProducts(forceRefresh)
    }

    suspend fun getCached(): List<Product> {
        return productRepository.getCachedProducts()
    }
}
