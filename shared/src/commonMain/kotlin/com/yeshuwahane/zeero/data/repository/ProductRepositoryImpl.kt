package com.yeshuwahane.zeero.data.repository

import com.russhwolf.settings.Settings
import com.yeshuwahane.zeero.data.db.ProductDao
import com.yeshuwahane.zeero.data.utils.DataResource
import com.yeshuwahane.zeero.data.utils.apiCall
import com.yeshuwahane.zeero.domain.model.Product
import com.yeshuwahane.zeero.domain.model.User
import com.yeshuwahane.zeero.domain.model.UserRole
import com.yeshuwahane.zeero.domain.repository.ProductRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
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

@Serializable
data class BidRequestDto(val amount: Double, val bidderName: String)

@Serializable
data class UploadProductRequestDto(
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val supplierId: String,
    val isAuction: Boolean,
    val durationHours: Int
)

@Serializable
data class ImageUploadResponseDto(val url: String)

@Serializable
private data class LastUpdatedDto(val lastUpdated: Long)

@Serializable
private data class UserDtoHelper(val id: String, val name: String, val email: String, val password: String, val role: String)

class ProductRepositoryImpl(
    private val httpClient: HttpClient,
    private val dao: ProductDao,
    private val settings: Settings
) : ProductRepository {

    private val json = Json { ignoreUnknownKeys = true }

    private fun getSettingsUserSync(): User? {
        val userJson = settings.getStringOrNull("settings_user") ?: return null
        return try {
            val dto = json.decodeFromString(UserDtoHelper.serializer(), userJson)
            User(dto.id, dto.name, dto.email, dto.password, UserRole.valueOf(dto.role.uppercase()))
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getProducts(forceRefresh: Boolean): DataResource<List<Product>> {
        if (!forceRefresh) {
            val lastUpdatedResource = apiCall<LastUpdatedDto> {
                httpClient.get("/api/products/last-updated")
            }
            if (lastUpdatedResource.isSuccess() && lastUpdatedResource.data != null) {
                val remoteLastUpdated = lastUpdatedResource.data.lastUpdated
                val localLastUpdated = settings.getLong("settings_products_last_updated", 0L)
                if (remoteLastUpdated == localLastUpdated) {
                    val cached = dao.getAll().map { it.toDomain() }
                    if (cached.isNotEmpty()) {
                        return DataResource.success(cached)
                    }
                }
            }
        }

        val resource = apiCall<List<ProductDto>> {
            httpClient.get("/api/products")
        }
        return if (resource.isSuccess() && resource.data != null) {
            val products = resource.data
            dao.clear()
            dao.insertAll(products)

            val lastUpdatedResource = apiCall<LastUpdatedDto> {
                httpClient.get("/api/products/last-updated")
            }
            if (lastUpdatedResource.isSuccess() && lastUpdatedResource.data != null) {
                settings.putLong("settings_products_last_updated", lastUpdatedResource.data.lastUpdated)
            }

            DataResource.success(products.map { it.toDomain() })
        } else {
            val cached = dao.getAll().map { it.toDomain() }
            DataResource.error(resource.error, cached)
        }
    }

    override suspend fun getProductById(id: String): DataResource<Product> {
        val resource = apiCall<ProductDto> {
            httpClient.get("/api/products/$id")
        }
        return if (resource.isSuccess() && resource.data != null) {
            DataResource.success(resource.data.toDomain())
        } else {
            val cached = dao.getAll().firstOrNull { it.id == id }?.toDomain()
            if (cached != null) {
                DataResource.success(cached)
            } else {
                DataResource.error(resource.error, null)
            }
        }
    }

    override suspend fun placeBid(productId: String, amount: Double, bidderName: String): DataResource<String> {
        val resource = apiCall<String> {
            httpClient.post("/api/products/$productId/bid") {
                contentType(ContentType.Application.Json)
                setBody(BidRequestDto(amount, bidderName))
            }
        }
        if (resource.isSuccess()) {
            val cachedProducts = dao.getAll()
            val updated = cachedProducts.map {
                if (it.id == productId) {
                    it.copy(currentHighestBid = amount, highestBidderName = bidderName)
                } else {
                    it
                }
            }
            dao.clear()
            dao.insertAll(updated)
        }
        return resource
    }

    override suspend fun addProduct(
        title: String,
        description: String,
        price: Double,
        category: String,
        supplierId: String,
        isAuction: Boolean,
        durationHours: Int
    ): DataResource<Product> {
        val resource = apiCall<ProductDto> {
            httpClient.post("/api/products") {
                contentType(ContentType.Application.Json)
                setBody(UploadProductRequestDto(title, description, price, category, supplierId, isAuction, durationHours))
            }
        }
        return if (resource.isSuccess() && resource.data != null) {
            val p = resource.data
            val currentList = dao.getAll().toMutableList()
            currentList.add(p)
            dao.clear()
            dao.insertAll(currentList)
            DataResource.success(p.toDomain())
        } else {
            DataResource.error(resource.error, null)
        }
    }

    override suspend fun approveProduct(productId: String): DataResource<String> {
        val user = getSettingsUserSync()
        return apiCall<String> {
            httpClient.post("/api/products/$productId/approve") {
                header("X-User-Id", user?.id ?: "")
            }
        }
    }

    override suspend fun rejectProduct(productId: String): DataResource<String> {
        val user = getSettingsUserSync()
        return apiCall<String> {
            httpClient.post("/api/products/$productId/reject") {
                header("X-User-Id", user?.id ?: "")
            }
        }
    }

    override suspend fun uploadProductImage(imageBytes: ByteArray): DataResource<String> {
        val resource = apiCall<ImageUploadResponseDto> {
            httpClient.post("/api/products/upload-image") {
                setBody(MultiPartFormDataContent(
                    formData {
                        append("image", imageBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/png")
                            append(HttpHeaders.ContentDisposition, "filename=upload.png")
                        })
                    }
                ))
            }
        }
        return if (resource.isSuccess() && resource.data != null) {
            DataResource.success(resource.data.url)
        } else {
            DataResource.error(resource.error, null)
        }
    }

    override suspend fun updateProduct(
        id: String,
        title: String,
        description: String,
        price: Double,
        category: String,
        supplierId: String,
        isAuction: Boolean,
        durationHours: Int
    ): DataResource<String> {
        val resource = apiCall<String> {
            httpClient.post("/api/products/$id/update") {
                contentType(ContentType.Application.Json)
                setBody(UploadProductRequestDto(title, description, price, category, supplierId, isAuction, durationHours))
            }
        }
        return resource
    }

    override suspend fun getCachedProducts(): List<Product> {
        return dao.getAll().map { it.toDomain() }
    }

    override suspend fun getCachedProductById(id: String): Product? {
        return dao.getAll().firstOrNull { it.id == id }?.toDomain()
    }

    private fun ProductDto.toDomain(): Product = Product(
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
