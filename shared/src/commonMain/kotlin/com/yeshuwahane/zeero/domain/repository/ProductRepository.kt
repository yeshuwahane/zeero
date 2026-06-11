package com.yeshuwahane.zeero.domain.repository

import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(forceRefresh: Boolean = false): DataResource<List<Product>>
    suspend fun getProductById(id: String): DataResource<Product>
    suspend fun getCachedProducts(): List<Product>
    suspend fun getCachedProductById(id: String): Product?
    suspend fun placeBid(productId: String, amount: Double, bidderName: String): DataResource<String>
    suspend fun addProduct(
        title: String,
        description: String,
        price: Double,
        category: String,
        supplierId: String,
        isAuction: Boolean,
        durationHours: Int
    ): DataResource<Product>
    suspend fun approveProduct(productId: String): DataResource<String>
    suspend fun rejectProduct(productId: String): DataResource<String>
    suspend fun uploadProductImage(imageBytes: ByteArray): DataResource<String>
}
