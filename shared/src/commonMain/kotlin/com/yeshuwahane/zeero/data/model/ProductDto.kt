package com.yeshuwahane.zeero.data.model

import com.yeshuwahane.zeero.domain.model.Product

data class ProductDto(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val supplierId: String,
    val isApproved: Boolean,
    val currentHighestBid: Double,
    val highestBidderName: String,
    val auctionEndTimeMillis: Long
)

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        description = description,
        price = price,
        imageUrl = imageUrl,
        supplierId = supplierId,
        isApproved = isApproved,
        currentHighestBid = currentHighestBid,
        highestBidderName = highestBidderName,
        auctionEndTimeMillis = auctionEndTimeMillis
    )
}

fun Product.toDto(): ProductDto {
    return ProductDto(
        id = id,
        title = title,
        description = description,
        price = price,
        imageUrl = imageUrl,
        supplierId = supplierId,
        isApproved = isApproved,
        currentHighestBid = currentHighestBid,
        highestBidderName = highestBidderName,
        auctionEndTimeMillis = auctionEndTimeMillis
    )
}
