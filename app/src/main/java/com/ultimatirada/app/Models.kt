package com.ultimatirada.app

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Serializable
data class ApiProduct(
    val id: Int,
    val name: String,
    val price: Double,
    val collection: String? = null,
    val category: String? = null,
    val description: String? = null,
    val stock: Int = 0,
    val featured: Int = 0,
    val color: String? = null,
    val image: String? = null,
    val sales: Int? = null,
    val brand: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
) {
    val priceFormatted: String get() = "%.2f€".format(Locale.US, price)
    val isFeatured: Boolean get() = featured == 1
    val isInStock: Boolean get() = stock > 0
}

@Serializable
data class ApiEventPlayer(
    val id: Int,
    val name: String,
    val nick: String,
    @SerialName("avatar_color") val avatarColor: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
) {
    val initial: String get() = nick.take(1).uppercase()
}

@Serializable
data class ApiEvent(
    val id: Int,
    val title: String,
    val date: String,
    val time: String,
    val type: String? = null,
    val format: String? = null,
    val description: String? = null,
    @SerialName("max_players") val maxPlayers: Int? = null,
    val price: Double? = null,
    val prizes: String? = null,
    val location: String? = null,
    val image: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("requires_payment") val requiresPayment: Int? = null,
    val currentPlayers: Int? = null,
    val isJoined: Boolean? = null,
    val paymentPending: Boolean? = null,
    val players: List<ApiEventPlayer>? = null,
) {
    val startDate: LocalDateTime?
        get() = runCatching {
            LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(time.take(5)))
        }.getOrNull()

    val dayOfMonth: String get() = startDate?.dayOfMonth?.toString()?.padStart(2, '0') ?: "--"

    val monthShort: String
        get() = startDate?.month?.getDisplayName(java.time.format.TextStyle.SHORT, Locale("es", "ES"))
            ?.replace(".", "")
            ?.uppercase()
            .orEmpty()

    val detailLine: String
        get() = listOfNotNull(format, type?.replaceFirstChar { it.uppercase() }, location)
            .filter { it.isNotBlank() }
            .ifEmpty { listOfNotNull(description) }
            .joinToString(" · ")
}

@Serializable
data class ApiRankingEntry(
    val id: Int,
    val name: String,
    val nick: String,
    @SerialName("avatar_color") val avatarColor: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("favorite_format") val favoriteFormat: String? = null,
    val points: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
) {
    val initial: String get() = nick.take(1).uppercase()
}

@Serializable
data class ApiYouTubeResponse(val videos: List<ApiYouTubeVideo> = emptyList())

@Serializable
data class ApiYouTubeVideo(
    val id: String,
    val title: String,
    val published: String? = null,
)

@Serializable
data class ApiStats(
    val users: Int = 0,
    val events: Int = 0,
    val products: Int = 0,
    val registrations: Int? = null,
)

enum class CommunityFeedType(val label: String, val apiValue: String) {
    ForYou("Para ti", "forYou"),
    Following("Siguiendo", "following"),
}

@Serializable
data class ApiCommunityPost(
    val id: Int,
    @SerialName("user_id") val userId: Int,
    val content: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("is_pinned") val isPinnedRaw: Int = 0,
    @SerialName("created_at") val createdAt: String,
    @SerialName("user_name") val userName: String,
    @SerialName("user_nick") val userNick: String,
    @SerialName("avatar_color") val avatarColor: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("user_role") val userRole: String? = null,
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    @SerialName("comments_count") val commentsCount: Int = 0,
    @SerialName("latest_medal_url") val latestMedalUrl: String? = null,
    @SerialName("latest_medal_name") val latestMedalName: String? = null,
) {
    val isPinned: Boolean get() = isPinnedRaw != 0
    val initials: String
        get() = userName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("")
            .ifBlank { userNick.take(1).uppercase() }
}

@Serializable
data class ApiLoginResponse(
    val user: ApiUser,
    val token: String,
)

@Serializable
data class ApiUser(
    val id: Int,
    val name: String,
    val nick: String,
    val email: String,
    val phone: String? = null,
    val bio: String? = null,
    val location: String? = null,
    @SerialName("favorite_format") val favoriteFormat: String? = null,
    @SerialName("avatar_color") val avatarColor: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val points: Int? = null,
    val wins: Int? = null,
    val losses: Int? = null,
    val draws: Int? = null,
    val role: String? = null,
    @SerialName("socio_tier") val socioTier: String? = null,
    @SerialName("socio_status") val socioStatus: String? = null,
    @SerialName("chequetienda_balance") val chequetiendaBalance: Double? = null,
    val followers: Int? = null,
    val following: Int? = null,
    val posts: Int? = null,
    val medals: List<ApiUserMedal>? = null,
    val isFollowing: Boolean? = null,
) {
    val initial: String get() = nick.take(1).uppercase()
    val initials: String
        get() = name.split(" ").filter { it.isNotBlank() }.take(2)
            .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifBlank { initial }
}

@Serializable
data class ApiUserMedal(
    val id: Int,
    val name: String,
    @SerialName("image_url") val imageUrl: String? = null,
)

@Serializable
data class ApiComment(
    val id: Int,
    @SerialName("post_id") val postId: Int,
    @SerialName("user_id") val userId: Int,
    val content: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("user_name") val userName: String,
    @SerialName("user_nick") val userNick: String,
    @SerialName("avatar_color") val avatarColor: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

@Serializable
data class ApiBasicResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val pendingPayment: Boolean? = null,
)

@Serializable
data class ApiDiscountResponse(
    val code: String,
    val percent: Double,
    val scope: String? = null,
)

@Serializable
data class ApiAdRewardResponse(
    val success: Boolean? = null,
    val message: String? = null,
    @SerialName("points_added") val pointsAdded: Int? = null,
    @SerialName("total_points") val totalPoints: Int? = null,
    @SerialName("next_available_at") val nextAvailableAt: String? = null,
)

@Serializable
data class ApiCheckoutResponse(
    val success: Boolean? = null,
    val message: String? = null,
    @SerialName("order_code") val orderCode: String? = null,
    val total: Double? = null,
    val subtotal: Double? = null,
    @SerialName("discount_code") val discountCode: String? = null,
    @SerialName("discount_percent") val discountPercent: Double? = null,
    @SerialName("discount_amount") val discountAmount: Double? = null,
    @SerialName("total_items") val totalItems: Int? = null,
    @SerialName("payment_method") val paymentMethod: String? = null,
    @SerialName("pickup_slot") val pickupSlot: String? = null,
    @SerialName("magic_phrase") val magicPhrase: String? = null,
)

@Serializable
data class ApiServerCartItem(val id: Int)

@Serializable
data class CartCheckoutItem(val productId: Int, val quantity: Int)

@Serializable
data class ApiCardSaleAvailability(
    val date: String,
    val slots: List<ApiCardSaleSlot> = emptyList(),
)

@Serializable
data class ApiCardSaleSlot(
    @SerialName("slot_index") val slotIndex: Int,
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String,
    val available: Boolean,
    val isMine: Boolean? = null,
    @SerialName("appointment_id") val appointmentId: Int? = null,
)

@Serializable
data class ApiMatchHistory(
    val id: Int,
    val opponent: String? = null,
    val result: String,
    val note: String? = null,
    @SerialName("created_at") val createdAt: String,
) {
    val resultLabel: String
        get() = when (result.lowercase()) {
            "win" -> "Victoria"
            "loss" -> "Derrota"
            "draw" -> "Empate"
            else -> result.replaceFirstChar { it.uppercase() }
        }
}

fun String.isoDateLabel(): String = runCatching {
    LocalDate.parse(take(10)).format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "ES")))
}.getOrElse { this }
