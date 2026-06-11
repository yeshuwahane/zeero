package com.yeshuwahane.zeero.data

import androidx.compose.runtime.mutableStateListOf
import com.yeshuwahane.zeero.domain.model.Product
import com.yeshuwahane.zeero.domain.model.User
import com.yeshuwahane.zeero.domain.model.UserRole
import com.yeshuwahane.zeero.getCurrentTimeMillis

object MockData {
    val products = mutableStateListOf<Product>(
        Product(
            id = "1",
            title = "iPhone 15 Pro Max (256GB)",
            description = "Titanium design, A17 Pro chip, customizable Action button, and the most powerful iPhone camera system ever. Excellent condition, barely used.",
            price = 999.00,
            imageUrl = "electronics",
            supplierId = "sup_01",
            isApproved = true,
            currentHighestBid = 1050.00,
            highestBidderName = "Alice Smith",
            auctionEndTimeMillis = getCurrentTimeMillis() + 3600000 * 2 // 2 hours from now
        ),
        Product(
            id = "2",
            title = "MacBook Pro M3 Max",
            description = "16-inch liquid retina XDR display, 36GB unified memory, 1TB SSD. The ultimate powerhouse for developers and creators alike. Sealed in box.",
            price = 2499.00,
            imageUrl = "electronics",
            supplierId = "sup_02",
            isApproved = true,
            currentHighestBid = 2600.00,
            highestBidderName = "Bob Jones",
            auctionEndTimeMillis = getCurrentTimeMillis() + 3600000 * 5 // 5 hours from now
        ),
        Product(
            id = "3",
            title = "Sony WH-1000XM5 Headphones",
            description = "Industry-leading noise canceling wireless headphones with auto noise-canceling optimizer, crystal clear hands-free calling, and Alexa voice control.",
            price = 348.00,
            imageUrl = "audio",
            supplierId = "sup_01",
            isApproved = true,
            currentHighestBid = 0.0,
            highestBidderName = "",
            auctionEndTimeMillis = 0L // Not an auction, direct sale
        ),
        Product(
            id = "4",
            title = "Air Jordan 1 Retro High OG",
            description = "Classic Chicago colorway, premium leather upper, comfortable air-sole cushioning. Perfect collector's item in size 10.",
            price = 180.00,
            imageUrl = "fashion",
            supplierId = "sup_03",
            isApproved = true,
            currentHighestBid = 210.00,
            highestBidderName = "Charlie Brown",
            auctionEndTimeMillis = getCurrentTimeMillis() + 3600000 * 12 // 12 hours from now
        ),
        Product(
            id = "5",
            title = "Vintage Leather Jacket",
            description = "Genuine brown leather jacket, distressed style from the 90s. Heavyweight material, perfect for autumn and winter.",
            price = 120.00,
            imageUrl = "fashion",
            supplierId = "sup_03",
            isApproved = false, // Pending Approval
            currentHighestBid = 0.0,
            highestBidderName = "",
            auctionEndTimeMillis = 0L
        ),
        Product(
            id = "6",
            title = "Mechanical Gaming Keyboard",
            description = "Custom hot-swappable mechanical keyboard, linear yellow switches, PBT keycaps, RGB backlighting. Brand new construction.",
            price = 85.00,
            imageUrl = "electronics",
            supplierId = "sup_02",
            isApproved = false, // Pending Approval
            currentHighestBid = 0.0,
            highestBidderName = "",
            auctionEndTimeMillis = getCurrentTimeMillis() + 3600000 * 24 // 24 hours from now
        )
    )

    val users = mutableStateListOf<User>(
        User("usr_01", "Alice Smith", "alice@customer.com", "alice123", UserRole.CUSTOMER),
        User("usr_02", "Bob Jones", "bob@customer.com", "bob123", UserRole.CUSTOMER),
        User("usr_03", "Charlie Brown", "charlie@customer.com", "charlie123", UserRole.CUSTOMER),
        User("sup_01", "Global Tech Supplies", "info@globaltech.com", "global123", UserRole.SUPPLIER),
        User("sup_02", "Apex Electronics", "sales@apexelectronics.com", "apex123", UserRole.SUPPLIER),
        User("sup_03", "Retro Thrift Co.", "retrothrift@gmail.com", "retro123", UserRole.SUPPLIER),
        User("adm_01", "Chief Admin", "admin@zeerostock.com", "admin123", UserRole.ADMIN),
        User("adm_02", "Operations Manager", "manager@zeerostock.com", "manager123", UserRole.ADMIN)
    )

    fun placeBid(productId: String, amount: Double, bidderName: String): Boolean {
        val index = products.indexOfFirst { it.id == productId }
        if (index != -1) {
            val product = products[index]
            val minimumRequired = if (product.currentHighestBid > 0.0) product.currentHighestBid else product.price
            if (amount > minimumRequired) {
                products[index] = product.copy(
                    currentHighestBid = amount,
                    highestBidderName = bidderName
                )
                return true
            }
        }
        return false
    }

    fun addProduct(title: String, description: String, price: Double, category: String, supplierId: String, isAuction: Boolean, durationHours: Int): Product {
        // Safe generation of next ID
        val nextId = ((products.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: 0) + 1).toString()
        val endTime = if (isAuction) {
            getCurrentTimeMillis() + (durationHours.toLong() * 3600L * 1000L)
        } else {
            0L
        }
        val newProduct = Product(
            id = nextId,
            title = title,
            description = description,
            price = price,
            imageUrl = category,
            supplierId = supplierId,
            isApproved = false, // Always requires admin approval
            currentHighestBid = 0.0,
            highestBidderName = "",
            auctionEndTimeMillis = endTime
        )
        products.add(newProduct)
        return newProduct
    }

    fun approveProduct(productId: String) {
        val index = products.indexOfFirst { it.id == productId }
        if (index != -1) {
            products[index] = products[index].copy(isApproved = true)
        }
    }

    fun rejectProduct(productId: String) {
        products.removeAll { it.id == productId }
    }
}
