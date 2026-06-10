package com.yeshuwahane.zeero.data.repository

import com.yeshuwahane.zeero.data.MockData
import com.yeshuwahane.zeero.domain.model.Product
import com.yeshuwahane.zeero.domain.repository.ProductRepository

class ProductRepositoryImpl : ProductRepository {
    override fun getProducts(): List<Product> {
        return MockData.products
    }

    override fun getProductById(id: String): Product? {
        return MockData.products.firstOrNull { it.id == id }
    }

    override fun placeBid(productId: String, amount: Double, bidderName: String): Boolean {
        return MockData.placeBid(productId, amount, bidderName)
    }

    override fun addProduct(
        title: String,
        description: String,
        price: Double,
        category: String,
        supplierId: String,
        isAuction: Boolean,
        durationHours: Int
    ): Product {
        return MockData.addProduct(title, description, price, category, supplierId, isAuction, durationHours)
    }

    override fun approveProduct(productId: String) {
        MockData.approveProduct(productId)
    }

    override fun rejectProduct(productId: String) {
        MockData.rejectProduct(productId)
    }
}
