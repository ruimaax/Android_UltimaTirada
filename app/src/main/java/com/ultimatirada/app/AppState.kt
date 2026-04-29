package com.ultimatirada.app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.time.LocalDate

private const val TOKEN_KEY = "ut_auth_token"

data class UiState(
    val products: List<ApiProduct> = emptyList(),
    val events: List<ApiEvent> = emptyList(),
    val ranking: List<ApiRankingEntry> = emptyList(),
    val youtubeVideos: List<ApiYouTubeVideo> = emptyList(),
    val stats: ApiStats? = null,
    val communityPosts: List<ApiCommunityPost> = emptyList(),
    val currentUser: ApiUser? = null,
    val isCheckingAuth: Boolean = true,
    val isLoading: Boolean = false,
    val hasLoadedOnce: Boolean = false,
    val errorMessage: String? = null,
) {
    val isLoggedIn: Boolean get() = currentUser != null
    val featuredProducts: List<ApiProduct>
        get() = products.filter { it.isFeatured }.ifEmpty { products.take(6) }
    val upcomingEvents: List<ApiEvent>
        get() = events.filter { (it.startDate?.toLocalDate() ?: LocalDate.MIN) >= LocalDate.now() }
            .sortedBy { it.startDate }
    val sortedRanking: List<ApiRankingEntry> get() = ranking.sortedByDescending { it.points }
    val topThree: List<ApiRankingEntry> get() = sortedRanking.take(3)
    val statsPlayers: Int get() = stats?.users ?: ranking.size
    val statsEvents: Int get() = stats?.events ?: upcomingEvents.size
    val statsProducts: Int get() = stats?.products ?: products.size
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("ultima_tirada", Context.MODE_PRIVATE)
    private val api = ApiClient()
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { restoreSession() }
    }

    private suspend fun restoreSession() {
        val saved = prefs.getString(TOKEN_KEY, null)
        if (!saved.isNullOrBlank()) {
            api.setToken(saved)
            runCatching { api.fetchMyProfile() }
                .onSuccess { user -> _uiState.update { it.copy(currentUser = user) } }
                .onFailure {
                    api.setToken(null)
                    prefs.edit().remove(TOKEN_KEY).apply()
                }
        }
        _uiState.update { it.copy(isCheckingAuth = false) }
    }

    fun login(credential: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            runCatching { api.login(credential, password) }
                .onSuccess { response ->
                    api.setToken(response.token)
                    prefs.edit().putString(TOKEN_KEY, response.token).apply()
                    _uiState.update { it.copy(currentUser = response.user) }
                    onResult(null)
                    loadAll()
                }
                .onFailure { onResult(it.message ?: "No se pudo iniciar sesión") }
        }
    }

    fun logout() {
        api.setToken(null)
        prefs.edit().remove(TOKEN_KEY).apply()
        _uiState.update { it.copy(currentUser = null) }
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val productsTask = async { loadProductsInternal() }
            val eventsTask = async { loadEventsInternal() }
            val rankingTask = async { loadRankingInternal() }
            val videosTask = async { runCatching { api.fetchYouTubeVideos() } }
            val statsTask = async { runCatching { api.fetchStats() } }
            val feedTask = async { runCatching { api.fetchCommunityFeed(CommunityFeedType.ForYou) } }

            val products = productsTask.await()
            val events = eventsTask.await()
            val ranking = rankingTask.await()
            val videos = videosTask.await()
            val stats = statsTask.await()
            val feed = feedTask.await()

            _uiState.update { state ->
                state.copy(
                    products = products.getOrDefault(state.products),
                    events = events.getOrDefault(state.events),
                    ranking = ranking.getOrDefault(state.ranking),
                    youtubeVideos = videos.getOrDefault(state.youtubeVideos),
                    stats = stats.getOrDefault(state.stats),
                    communityPosts = feed.getOrDefault(state.communityPosts),
                    isLoading = false,
                    hasLoadedOnce = true,
                    errorMessage = if (products.isFailure && events.isFailure && ranking.isFailure && !state.hasLoadedOnce) {
                        "No se pudieron cargar los datos. Comprueba tu conexión."
                    } else {
                        null
                    },
                )
            }
        }
    }

    fun loadProducts() = viewModelScope.launch { loadProductsInternal().onSuccess { data -> _uiState.update { it.copy(products = data) } } }
    fun loadEvents() = viewModelScope.launch { loadEventsInternal().onSuccess { data -> _uiState.update { it.copy(events = data) } } }
    fun loadRanking() = viewModelScope.launch { loadRankingInternal().onSuccess { data -> _uiState.update { it.copy(ranking = data) } } }

    fun loadCommunity(type: CommunityFeedType) = viewModelScope.launch {
        runCatching { api.fetchCommunityFeed(type) }
            .onSuccess { posts -> _uiState.update { it.copy(communityPosts = posts) } }
    }

    fun publishPost(content: String, onResult: (String?) -> Unit) = viewModelScope.launch {
        runCatching { api.publishPost(content) }
            .onSuccess {
                onResult(null)
                loadCommunity(CommunityFeedType.ForYou)
            }
            .onFailure { onResult(it.message ?: "Error al publicar") }
    }

    fun toggleLike(post: ApiCommunityPost) = viewModelScope.launch {
        _uiState.update { state ->
            state.copy(
                communityPosts = state.communityPosts.map {
                    if (it.id == post.id) it.copy(
                        likedByMe = !post.likedByMe,
                        likes = if (post.likedByMe) post.likes - 1 else post.likes + 1,
                    ) else it
                },
            )
        }
        runCatching {
            if (post.likedByMe) api.unlikePost(post.id) else api.likePost(post.id)
        }.onFailure { loadCommunity(CommunityFeedType.ForYou) }
    }

    fun joinEvent(event: ApiEvent, onResult: (String?) -> Unit) = viewModelScope.launch {
        runCatching { api.joinEvent(event.id) }
            .onSuccess { response ->
                onResult(response.message)
                loadEvents()
            }
            .onFailure { onResult(it.message ?: "No se pudo apuntarte") }
    }

    fun submitSellRequest(
        cardName: String?,
        collection: String?,
        condition: String?,
        bulkList: String?,
        contactPhone: String?,
        contactEmail: String?,
        preferredContact: String,
        appointmentDate: String,
        appointmentSlotIndex: Int,
        onResult: (String?) -> Unit,
    ) = viewModelScope.launch {
        runCatching {
            api.submitSellRequest(
                cardName,
                collection,
                condition,
                bulkList,
                contactPhone,
                contactEmail,
                preferredContact,
                appointmentDate,
                appointmentSlotIndex,
            )
        }.onSuccess { onResult(null) }
            .onFailure { onResult(it.message ?: "No se pudo enviar la solicitud") }
    }

    private suspend fun loadProductsInternal() = runCatching { api.fetchProducts() }
    private suspend fun loadEventsInternal() = runCatching { api.fetchEvents() }
    private suspend fun loadRankingInternal() = runCatching { api.fetchRanking() }
}

@Serializable
data class CartItem(val product: ApiProduct, val quantity: Int) {
    val id: Int get() = product.id
    val lineTotal: Double get() = product.price * quantity
}

class CartStore(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("ultima_tirada_cart", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }
    private val _items = MutableStateFlow(load())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    val itemCount: Int get() = _items.value.sumOf { it.quantity }
    val subtotal: Double get() = _items.value.sumOf { it.lineTotal }
    val subtotalFormatted: String get() = "%.2f€".format(subtotal)
    val isEmpty: Boolean get() = _items.value.isEmpty()

    fun add(product: ApiProduct, quantity: Int = 1) {
        if (!product.isInStock) return
        val next = _items.value.toMutableList()
        val index = next.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val item = next[index]
            next[index] = item.copy(quantity = (item.quantity + quantity).coerceIn(1, product.stock.coerceAtLeast(1)))
        } else {
            next += CartItem(product, quantity.coerceIn(1, product.stock.coerceAtLeast(1)))
        }
        save(next)
    }

    fun remove(productId: Int) = save(_items.value.filterNot { it.product.id == productId })

    fun updateQuantity(productId: Int, quantity: Int) {
        val next = _items.value.mapNotNull { item ->
            if (item.product.id != productId) {
                item
            } else if (quantity <= 0) {
                null
            } else {
                item.copy(quantity = quantity.coerceIn(1, item.product.stock.coerceAtLeast(1)))
            }
        }
        save(next)
    }

    fun clear() = save(emptyList())

    fun emailIntent(): Intent {
        val lines = buildList {
            add("Hola, me gustaría reservar los siguientes productos:")
            add("")
            _items.value.forEach { add("• ${it.product.name} x${it.quantity} — %.2f€".format(it.lineTotal)) }
            add("")
            add("TOTAL: $subtotalFormatted")
            add("")
            add("Gracias.")
        }.joinToString("\n")
        return Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:contacto@ultimatirada.com")
            putExtra(Intent.EXTRA_SUBJECT, "Reserva de productos - Última Tirada")
            putExtra(Intent.EXTRA_TEXT, lines)
        }
    }

    private fun save(next: List<CartItem>) {
        _items.value = next
        prefs.edit().putString("cart.v1", json.encodeToString(ListSerializer(CartItem.serializer()), next)).apply()
    }

    private fun load(): List<CartItem> {
        val raw = prefs.getString("cart.v1", null) ?: return emptyList()
        return runCatching { json.decodeFromString(ListSerializer(CartItem.serializer()), raw) }.getOrDefault(emptyList())
    }
}
