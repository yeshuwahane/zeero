package com.yeshuwahane.zeero.domain.repository

import com.yeshuwahane.zeero.domain.model.Product

interface ProductRepository {
    fun getProducts(): List<Product>
    fun getProductById(id: String): Product?
    fun placeBid(productId: String, amount: Double, bidderName: String): Boolean
    fun addProduct(title: String, description: String, price: Double, category: String, supplierId: String, isAuction: Boolean, durationHours: Int): Product
    fun approveProduct(productId: String)
    fun rejectProduct(productId: String)
}
