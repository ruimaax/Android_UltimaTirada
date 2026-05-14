package com.ultimatirada.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ApiException(message: String, val statusCode: Int? = null) : Exception(message)

class ApiClient {
    private val baseUrls: List<String> = if (BuildConfig.DEBUG) {
        listOf("http://10.0.2.2:8080", "http://127.0.0.1:8080", "https://ultimatirada.com")
    } else {
        listOf("https://ultimatirada.com")
    }
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }
    private var authToken: String? = null

    fun setToken(token: String?) {
        authToken = token
    }

    suspend fun fetchProducts(): List<ApiProduct> = getList("/api/products", ApiProduct.serializer())
    suspend fun fetchEvents(): List<ApiEvent> = getList("/api/events", ApiEvent.serializer())
    suspend fun fetchEvent(id: Int): ApiEvent = get("/api/events/$id", ApiEvent.serializer())
    suspend fun fetchRanking(): List<ApiRankingEntry> = getList("/api/users/ranking", ApiRankingEntry.serializer())
    suspend fun fetchStats(): ApiStats = get("/api/stats", ApiStats.serializer())
    suspend fun fetchMyProfile(): ApiUser = get("/api/profile/me", ApiUser.serializer())
    suspend fun fetchMatchHistory(): List<ApiMatchHistory> = getList("/api/my/match-history", ApiMatchHistory.serializer())
    suspend fun fetchCommunityFeed(type: CommunityFeedType): List<ApiCommunityPost> =
        getList("/api/feed?type=${type.apiValue}", ApiCommunityPost.serializer())

    suspend fun fetchYouTubeVideos(): List<ApiYouTubeVideo> =
        get("/api/youtube-videos", ApiYouTubeResponse.serializer()).videos

    suspend fun login(credential: String, password: String): ApiLoginResponse =
        post(
            path = "/api/auth/login",
            body = buildJsonObject {
                put("login", credential)
                put("password", password)
            },
            serializer = ApiLoginResponse.serializer(),
            authenticated = false,
        )

    suspend fun publishPost(content: String) {
        postRaw("/api/posts", buildJsonObject { put("content", content) })
    }

    suspend fun likePost(id: Int) = mutate("/api/posts/$id/like", "POST")
    suspend fun unlikePost(id: Int) = mutate("/api/posts/$id/like", "DELETE")
    suspend fun fetchComments(postId: Int): List<ApiComment> =
        getList("/api/posts/$postId/comments", ApiComment.serializer())

    suspend fun postComment(postId: Int, content: String) {
        postRaw("/api/posts/$postId/comments", buildJsonObject { put("content", content) })
    }

    suspend fun fetchPublicProfile(userId: Int): ApiUser = get("/api/profile/$userId", ApiUser.serializer())
    suspend fun followUser(userId: Int) = mutate("/api/profile/$userId/follow", "POST")
    suspend fun unfollowUser(userId: Int) = mutate("/api/profile/$userId/follow", "DELETE")

    suspend fun addToCart(productId: Int, quantity: Int) {
        postRaw(
            "/api/cart/add",
            buildJsonObject {
                put("product_id", productId)
                put("quantity", quantity)
            },
        )
    }

    suspend fun fetchServerCart(): List<ApiServerCartItem> = getList("/api/cart", ApiServerCartItem.serializer())
    suspend fun deleteCartItem(id: Int) = mutate("/api/cart/$id", "DELETE")

    suspend fun checkoutCart(
        localItems: List<CartCheckoutItem>,
        paymentMethod: String,
        buyerPhone: String,
        buyerEmail: String,
        buyerNote: String,
        discountCode: String,
        useChequetienda: Boolean,
    ): ApiCheckoutResponse {
        runCatching { fetchServerCart() }.getOrDefault(emptyList()).forEach { runCatching { deleteCartItem(it.id) } }
        localItems.forEach { addToCart(it.productId, it.quantity) }
        return post(
            "/api/cart/checkout",
            buildJsonObject {
                put("delivery_method", "Recogida a convenir con la asociación")
                put("payment_method", paymentMethod)
                put("buyer_phone", buyerPhone)
                put("buyer_email", buyerEmail)
                put("buyer_note", buyerNote)
                put("discount_code", discountCode)
                put("use_chequetienda", useChequetienda)
            },
            ApiCheckoutResponse.serializer(),
        )
    }

    suspend fun joinEvent(
        id: Int,
        paymentMethod: String? = null,
        buyerPhone: String = "",
        buyerEmail: String = "",
        buyerNote: String = "",
        discountCode: String = "",
        useChequetienda: Boolean = false,
    ): ApiBasicResponse {
        val body = buildJsonObject {
            if (paymentMethod != null) {
                put("payment_method", paymentMethod)
                put("buyer_phone", buyerPhone)
                put("buyer_email", buyerEmail)
                put("buyer_note", buyerNote)
                put("discount_code", discountCode)
                put("use_chequetienda", useChequetienda)
            }
        }
        return post("/api/events/$id/join", body, ApiBasicResponse.serializer())
    }

    suspend fun validateDiscount(code: String, scope: String): ApiDiscountResponse =
        post(
            "/api/discounts/validate",
            buildJsonObject {
                put("code", code)
                put("scope", scope)
            },
            ApiDiscountResponse.serializer(),
        )

    suspend fun claimRewardedAdPoints(): ApiAdRewardResponse =
        post(
            "/api/rewards/ad-video",
            buildJsonObject { put("reward_type", "rewarded_video_points") },
            ApiAdRewardResponse.serializer(),
        )

    suspend fun fetchCardSaleAvailability(date: String): ApiCardSaleAvailability =
        get("/api/card-sales/appointments/availability?date=$date", ApiCardSaleAvailability.serializer())

    suspend fun fetchAllyMe(): AllyMeResponse {
        return try {
            parseAllyMeResponse(getJson("/api/ally/me"))
        } catch (error: ApiException) {
            if (error.statusCode == 404) fetchAllyMeFallbackFromJoinedEvents() else throw error
        }
    }

    private suspend fun fetchAllyMeFallbackFromJoinedEvents(): AllyMeResponse {
        val joinedIds = runCatching {
            getJson("/api/auth/me").jsonObject["joinedEvents"]?.let { element ->
                element as? kotlinx.serialization.json.JsonArray
            }?.mapNotNull { it.jsonPrimitive.content.toIntOrNull() }
        }.getOrNull().orEmpty()
        val joined = fetchEvents()
            .filter { it.isJoined == true || joinedIds.contains(it.id) }
            .map { AllyEvent(id = it.id.toString(), title = it.title, isJoined = true) }
        return AllyMeResponse(events = joined)
    }

    suspend fun submitSellRequest(
        cardName: String?,
        collection: String?,
        condition: String?,
        bulkList: String?,
        contactPhone: String?,
        contactEmail: String?,
        preferredContact: String,
        appointmentDate: String,
        appointmentSlotIndex: Int,
        photoDataUrl: String? = null,
    ) {
        if (!bulkList.isNullOrBlank()) {
            submitBulkCardSale(bulkList, contactPhone, contactEmail, preferredContact, appointmentDate, appointmentSlotIndex)
            return
        }

        postRaw(
            "/api/card-sales",
            buildJsonObject {
                put("preferred_contact", preferredContact)
                put("appointment_date", appointmentDate)
                put("appointment_slot_index", appointmentSlotIndex)
                if (!cardName.isNullOrBlank()) put("card_name", cardName)
                if (!collection.isNullOrBlank()) put("collection_name", collection)
                if (!condition.isNullOrBlank()) put("card_condition", condition)
                if (!contactPhone.isNullOrBlank()) put("contact_phone", contactPhone)
                if (!contactEmail.isNullOrBlank()) put("contact_email", contactEmail)
                if (!photoDataUrl.isNullOrBlank()) {
                    put("photo_urls", buildJsonArray { add(photoDataUrl) })
                }
            },
        )
    }

    private suspend fun submitBulkCardSale(
        bulkList: String,
        contactPhone: String?,
        contactEmail: String?,
        preferredContact: String,
        appointmentDate: String,
        appointmentSlotIndex: Int,
    ) {
        val cards = bulkList.lineSequence().mapNotNull { line ->
            val parts = line.trim().split(Regex("\\s+[-–—|]\\s+"), limit = 2)
            if (parts.size == 2 && parts[0].isNotBlank() && parts[1].isNotBlank()) {
                buildJsonObject {
                    put("card_name", parts[0].trim())
                    put("collection_name", parts[1].trim())
                }
            } else {
                null
            }
        }.toList()

        postRaw(
            "/api/card-sales/bulk",
            buildJsonObject {
                put("cards", json.encodeToJsonElement(cards))
                put("preferred_contact", preferredContact)
                put("appointment_date", appointmentDate)
                put("appointment_slot_index", appointmentSlotIndex)
                if (!contactPhone.isNullOrBlank()) put("contact_phone", contactPhone)
                if (!contactEmail.isNullOrBlank()) put("contact_email", contactEmail)
            },
        )
    }

    private suspend fun <T> get(path: String, serializer: KSerializer<T>): T =
        request(path, "GET", null, serializer)

    private suspend fun <T> getList(path: String, serializer: KSerializer<T>): List<T> =
        request(path, "GET", null, ListSerializer(serializer))

    private suspend fun <T> post(path: String, body: JsonObject, serializer: KSerializer<T>, authenticated: Boolean = true): T =
        request(path, "POST", body.toString(), serializer, authenticated)

    private suspend fun postRaw(path: String, body: JsonObject) {
        request(path, "POST", body.toString(), UnitSerializer)
    }

    private suspend fun mutate(path: String, method: String) {
        request(path, method, null, UnitSerializer)
    }

    private suspend fun getJson(path: String): JsonElement =
        requestRaw(path, "GET", null).let { json.parseToJsonElement(it) }

    private suspend fun <T> request(
        path: String,
        method: String,
        body: String?,
        serializer: KSerializer<T>,
        authenticated: Boolean = true,
    ): T = withContext(Dispatchers.IO) {
        val text = requestRaw(path, method, body, authenticated)
        if (serializer == UnitSerializer) {
            @Suppress("UNCHECKED_CAST")
            Unit as T
        } else {
            json.decodeFromString(serializer, text)
        }
    }

    private suspend fun requestRaw(
        path: String,
        method: String,
        body: String?,
        authenticated: Boolean = true,
    ): String = withContext(Dispatchers.IO) {
        var lastNetworkError: IOException? = null
        baseUrls.forEachIndexed { index, baseUrl ->
            try {
                return@withContext requestRawOnce(baseUrl, path, method, body, authenticated)
            } catch (error: IOException) {
                lastNetworkError = error
                if (index == baseUrls.lastIndex) {
                    throw ApiException("No se pudo conectar al servidor")
                }
            }
        }
        throw ApiException(lastNetworkError?.localizedMessage ?: "No se pudo conectar al servidor")
    }

    private fun requestRawOnce(
        baseUrl: String,
        path: String,
        method: String,
        body: String?,
        authenticated: Boolean,
    ): String {
        val connection = (URL("$baseUrl$path").openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 20_000
            readTimeout = 20_000
            setRequestProperty("Accept", "application/json")
            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }
            if (authenticated) authToken?.let { setRequestProperty("Authorization", "Bearer $it") }
        }

        body?.let {
            OutputStreamWriter(connection.outputStream).use { writer -> writer.write(it) }
        }

        val status = connection.responseCode
        val stream = if (status in 200..299) connection.inputStream else connection.errorStream
        val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
        connection.disconnect()

        if (status !in 200..299) {
            val message = runCatching {
                json.parseToJsonElement(text).jsonObject["error"]?.jsonPrimitive?.content
            }.getOrNull()
            throw ApiException(message ?: "Error del servidor ($status)", status)
        }

        return text
    }
}

private object UnitSerializer : KSerializer<Unit> {
    override val descriptor = kotlinx.serialization.descriptors.PrimitiveSerialDescriptor(
        "Unit",
        kotlinx.serialization.descriptors.PrimitiveKind.STRING,
    )

    override fun deserialize(decoder: kotlinx.serialization.encoding.Decoder) = Unit
    override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: Unit) = Unit
}
