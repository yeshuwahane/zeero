package com.yeshuwahane.zeero.domain.model

data class Product(
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
) {
    val isAuction: Boolean
        get() = auctionEndTimeMillis > 0L
}
