package com.ultimatirada.app

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.app.Activity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Background = Color(0xFF090911)
private val Panel = Color(0xFF17162B)
private val CardColor = Color(0xFF0E0E16)
private val Border = Color(0xFF523895)
private val Active = Color(0xFF291F57)
private val Muted = Color(0xFFB8BCE5)
private val Blue = Color(0xFF4EA8FF)
private val Purple = Color(0xFF9B6CFF)
private val DeepPurple = Color(0xFF6E43C6)

private object MonetizationConfig {
    const val REMOVE_ADS_PRODUCT_ID = "ultima_tirada_remove_ads_lifetime_999"
    const val ADS_REMOVED_KEY = "monetization.adsRemoved"
    // Reemplaza con tus IDs reales de Android en AdMob
    const val NATIVE_COMMUNITY_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110" // test
    const val NATIVE_STORE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110" // test
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917" // test
}
private val Gold = Color(0xFFFFC857)

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        setContent {
            UltimaTiradaTheme {
                val cart = remember { CartStore(this) }
                val prefs = remember { getSharedPreferences("ultima_tirada", MODE_PRIVATE) }
                val adsRemoved = remember { mutableStateOf(prefs.getBoolean(MonetizationConfig.ADS_REMOVED_KEY, false)) }
                UltimaTiradaApp(vm, cart, adsRemoved.value) { adsRemoved.value = true; prefs.edit().putBoolean(MonetizationConfig.ADS_REMOVED_KEY, true).apply() }
            }
        }
    }
}

@Composable
private fun UltimaTiradaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Blue,
            secondary = Purple,
            tertiary = Gold,
            background = Background,
            surface = Panel,
            onSurface = Color.White,
        ),
        content = content,
    )
}

private enum class AppTab(val label: String) {
    Home("Inicio"),
    Store("Tienda"),
    Events("Eventos"),
    Ally("Aliado"),
    Community("Comunidad"),
    Profile("Perfil"),
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun UltimaTiradaApp(vm: MainViewModel, cart: CartStore, adsRemoved: Boolean, onAdsRemoved: () -> Unit) {
    val state by vm.uiState.collectAsState()
    val cartItems by cart.items.collectAsState()
    var splashDone by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(AppTab.Home) }
    var showCart by remember { mutableStateOf(false) }
    var showProfileMenu by remember { mutableStateOf(false) }
    var showProfileDetail by remember { mutableStateOf(false) }
    var showEventsSheet by remember { mutableStateOf(false) }
    var showRankingSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1400)
        splashDone = true
    }
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            vm.loadAll()
            while (true) {
                delay(12_000)
                vm.refreshHomeData()
            }
        }
    }

    Surface(Modifier.fillMaxSize(), color = Background) {
        when {
            state.isCheckingAuth || !splashDone -> SplashScreen()
            !state.isLoggedIn -> LoginScreen(vm)
            else -> BrandBackground {
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        AppHeader(
                            user = state.currentUser,
                            cartCount = cartItems.sumOf { it.quantity },
                            onCartTap = { showCart = true },
                            onAvatarTap = { if (state.currentUser != null) showProfileMenu = true },
                        )
                    },
                    bottomBar = {
                        AppBottomBar(selectedTab) { selectedTab = it }
                    },
                ) { padding ->
                    Box(Modifier.padding(padding)) {
                        when (selectedTab) {
                            AppTab.Home -> HomeScreen(state, onTab = { selectedTab = it }, vm = vm, adsRemoved = adsRemoved)
                            AppTab.Store -> StoreScreen(state.products, state.isLoading, cart, vm, adsRemoved = adsRemoved)
                            AppTab.Events -> EventsScreen(state, vm)
                            AppTab.Ally -> AllyMemberScreen(state, vm)
                            AppTab.Community -> CommunityScreen(state, vm, adsRemoved = adsRemoved)
                            AppTab.Profile -> ProfileScreen(state, vm, adsRemoved = adsRemoved, onAdsRemoved = onAdsRemoved)
                        }
                    }
                }
            }
        }
    }

    if (showCart) {
        CartDialog(cart = cart, vm = vm, onDismiss = { showCart = false })
    }
    val user = state.currentUser
    if (showProfileMenu && user != null) {
        ProfileMenuSheet(
            user = user,
            onMiPerfil = { showProfileMenu = false; showProfileDetail = true },
            onMisEventos = { showProfileMenu = false; showEventsSheet = true },
            onMiRanking = { showProfileMenu = false; showRankingSheet = true },
            onDismiss = { showProfileMenu = false },
        )
    }
    if (showProfileDetail && user != null) {
        UserProfileSheet(user, onDismiss = { showProfileDetail = false })
    }
    if (showEventsSheet && user != null) {
        UserEventsSheet(state, vm, onDismiss = { showEventsSheet = false })
    }
    if (showRankingSheet && user != null) {
        UserRankingSheet(user, state, vm, onDismiss = { showRankingSheet = false })
    }
}

@Composable
private fun SplashScreen() {
    BrandBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.logosff),
                contentDescription = null,
                modifier = Modifier.size(210.dp),
                contentScale = ContentScale.Fit,
            )
            Text(
                "ÚLTIMA TIRADA",
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(Blue, Purple)),
                    fontFamily = FontFamily.Serif,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                ),
                maxLines = 1,
            )
            Spacer(Modifier.height(18.dp))
            Text(
                "Magic: The Gathering en Ceuta",
                color = Color(0xFFB7B1CC),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
        }
    }
}

@Composable
private fun LoginScreen(vm: MainViewModel) {
    BrandBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(top = 76.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.logosff),
                contentDescription = null,
                modifier = Modifier.size(118.dp),
                contentScale = ContentScale.Fit,
            )
            Text(
                "ÚLTIMA TIRADA",
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(Blue, Purple)),
                    fontFamily = FontFamily.Serif,
                    fontSize = 39.sp,
                    fontWeight = FontWeight.Black,
                ),
                maxLines = 1,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Magic: The Gathering en Ceuta",
                color = Color(0xFFB7B1CC),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(48.dp))
            LoginPanel(vm)
        }
    }
}

@Composable
private fun LoginPanel(vm: MainViewModel) {
    var credential by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xEE19172F))
            .border(1.5.dp, DeepPurple, RoundedCornerShape(18.dp))
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Correo o @nick", color = Color(0xFFC5BED8), fontWeight = FontWeight.Black, fontSize = 16.sp)
        OutlinedTextField(
            value = credential,
            onValueChange = { credential = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
            colors = loginFieldColors(),
            shape = RoundedCornerShape(11.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text("Contraseña", color = Color(0xFFC5BED8), fontWeight = FontWeight.Black, fontSize = 16.sp)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
            colors = loginFieldColors(),
            shape = RoundedCornerShape(11.dp),
            visualTransformation = PasswordVisualTransformation(),
        )
        error?.let { Text(it, color = Color(0xFFFF8A80)) }
        Button(
            onClick = {
                loading = true
                vm.login(credential, password) {
                    loading = false
                    error = it
                }
            },
            enabled = !loading && credential.isNotBlank() && password.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(36.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4D4A60),
                contentColor = Color(0xFFEDEBFF),
                disabledContainerColor = Color(0xFF494656),
                disabledContentColor = Color(0xFF8B8798),
            ),
        ) {
            if (loading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
            } else {
                Icon(Icons.Default.ArrowCircleRight, null, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text("Iniciar sesión", fontWeight = FontWeight.Black, fontSize = 20.sp)
            }
        }
    }
}

@Composable
private fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Purple,
    focusedBorderColor = DeepPurple,
    unfocusedBorderColor = DeepPurple,
    focusedContainerColor = Color(0xFF1A1830),
    unfocusedContainerColor = Color(0xFF1A1830),
)

@Composable
private fun BrandBackground(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF40106A).copy(alpha = 0.48f),
                radius = size.minDimension * 0.42f,
                center = Offset(size.width * 0.12f, size.height * 0.02f),
            )
            drawCircle(
                color = Color(0xFF0B2B5C).copy(alpha = 0.28f),
                radius = size.minDimension * 0.48f,
                center = Offset(size.width * 0.95f, size.height * 0.92f),
            )
            val stars = listOf(
                0.11f to 0.68f, 0.21f to 0.20f, 0.32f to 0.10f, 0.43f to 0.32f,
                0.54f to 0.26f, 0.62f to 0.50f, 0.77f to 0.08f, 0.89f to 0.61f,
                0.18f to 0.92f, 0.71f to 0.76f, 0.93f to 0.82f, 0.05f to 0.74f,
            )
            stars.forEachIndexed { index, point ->
                drawCircle(
                    color = if (index % 3 == 0) Purple.copy(alpha = 0.58f) else Blue.copy(alpha = 0.45f),
                    radius = if (index % 4 == 0) 3.1f else 2.0f,
                    center = Offset(size.width * point.first, size.height * point.second),
                )
            }
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC00030A)))),
        )
        content()
    }
}

@Composable
private fun AppHeader(user: ApiUser?, cartCount: Int, onCartTap: () -> Unit, onAvatarTap: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color(0xF7000005))
            .border(1.dp, DeepPurple.copy(alpha = 0.85f))
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Image(painterResource(R.drawable.logosff), null, Modifier.size(44.dp), contentScale = ContentScale.Fit)
        Column(Modifier.weight(1f)) {
            Text("Última Tirada", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp, maxLines = 1)
            Text("Magic: The Gathering en Ceuta", color = Color(0xFFB7B1CC), fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        }
        IconButton(onClick = onCartTap) {
            BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF211B3F)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = Color(0xFFBDB6CF), modifier = Modifier.size(22.dp))
                }
            }
        }
        Box(
            Modifier
                .clip(CircleShape)
                .clickable(onClick = onAvatarTap)
                .border(1.5.dp, if (user != null) Purple.copy(alpha = 0.6f) else Color.Transparent, CircleShape),
        ) {
            Avatar(user?.avatarUrl, user?.avatarColor, user?.initial ?: "UT", 40.dp)
        }
    }
}

@Composable
private fun AppBottomBar(selected: AppTab, onSelected: (AppTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFA05050D))
            .border(1.dp, DeepPurple.copy(alpha = 0.75f))
            .navigationBarsPadding()
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppTab.entries.forEach { tab ->
            val isSelected = selected == tab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(if (isSelected) Color(0xFF2D225A) else Color.Transparent)
                    .clickable { onSelected(tab) }
                    .padding(horizontal = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = when (tab) {
                        AppTab.Home -> Icons.Default.Home
                        AppTab.Store -> Icons.Default.Storefront
                        AppTab.Events -> Icons.Default.CalendarMonth
                        AppTab.Ally -> Icons.Default.AutoAwesome
                        AppTab.Community -> Icons.Default.Groups
                        AppTab.Profile -> Icons.Default.Person
                    },
                    contentDescription = tab.label,
                    tint = if (isSelected) Color.White else Color(0xFFB8B1CC),
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    tab.label,
                    color = if (isSelected) Color.White else Color(0xFFB8B1CC),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(state: UiState, onTab: (AppTab) -> Unit, vm: MainViewModel, adsRemoved: Boolean = false) {
    var detailEvent by remember { mutableStateOf<ApiEvent?>(null) }
    var selectedPublicUserId by remember { mutableStateOf<Int?>(null) }

    LazyColumn(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item {
            HeroCard(onEvents = { onTab(AppTab.Events) }, onStore = { onTab(AppTab.Store) })
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("${state.statsPlayers}", "Jugadores", Modifier.weight(1f))
                StatCard("${state.statsEvents}", "Eventos", Modifier.weight(1f))
                StatCard("${state.statsProducts}", "Productos", Modifier.weight(1f))
            }
        }
        state.errorMessage?.let { item { ErrorBanner(it) { vm.loadAll() } } }
        item {
            HomeSection(title = "Próximos eventos", icon = { Icon(Icons.Default.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(16.dp)) }) {
                if (state.upcomingEvents.isEmpty()) {
                    EmptyEventCard()
                } else {
                    HomeEventCard(state.upcomingEvents.first(), onClick = { detailEvent = state.upcomingEvents.first() })
                }
            }
        }
        item {
            HomeSection(title = "Productos destacados", icon = { Icon(Icons.Default.Whatshot, null, tint = Color.White, modifier = Modifier.size(16.dp)) }) {
                FeaturedProductsCarousel(products = state.featuredProducts) { onTab(AppTab.Store) }
            }
        }
        item {
            HomeSection(title = "Nuestro canal", icon = { Icon(Icons.Default.PlayCircle, null, tint = Color.White, modifier = Modifier.size(16.dp)) }) {
                YouTubeCarousel(state.youtubeVideos)
            }
        }
        item {
            HomeSection(title = "Top jugadores de la comunidad", icon = { Icon(Icons.Default.EmojiEvents, null, tint = Color.White, modifier = Modifier.size(16.dp)) }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.topThree.forEachIndexed { index, player ->
                        TopPlayerCard(index + 1, player) { selectedPublicUserId = player.id }
                    }
                }
            }
        }
        val recentPosts = state.communityPosts.take(3)
        if (recentPosts.isNotEmpty()) {
            item {
                HomeSection(title = "Últimas actualizaciones", icon = { Icon(Icons.Default.ChatBubble, null, tint = Color.White, modifier = Modifier.size(16.dp)) }) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        recentPosts.forEach { post ->
                            Row(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0E0C1E)).padding(12.dp)
                                    .clickable { onTab(AppTab.Community) },
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Avatar(post.avatarUrl, post.avatarColor, post.initials, 36.dp)
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(post.userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        if (post.userRole == "admin") {
                                            Text("STAFF", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black,
                                                modifier = Modifier.clip(RoundedCornerShape(50)).background(Gold).padding(horizontal = 5.dp, vertical = 1.dp))
                                        }
                                    }
                                    Text(post.content, color = Muted, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 18.sp)
                                }
                                Text(post.createdAt.relativeLabel(), color = Muted, fontSize = 11.sp)
                            }
                        }
                        TextButton(onClick = { onTab(AppTab.Community) }, modifier = Modifier.align(Alignment.End)) {
                            Text("Ver todo →", color = Purple, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        if (!adsRemoved) {
            item { NativeAdCard(MonetizationConfig.NATIVE_COMMUNITY_AD_UNIT_ID) }
        }
    }

    detailEvent?.let { event ->
        EventDetailDialog(
            initialEvent = event,
            vm = vm,
            onDismiss = { detailEvent = null },
        )
    }
    selectedPublicUserId?.let { id ->
        PublicProfileSheet(userId = id, vm = vm, onDismiss = { selectedPublicUserId = null })
    }
}

@Composable
private fun HeroCard(onEvents: () -> Unit, onStore: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.linearGradient(
                listOf(Active.copy(alpha = 0.88f), Background.copy(alpha = 0.94f), Color.Black.copy(alpha = 0.92f)),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
            ))
            .border(1.3.dp, Border.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
            .padding(horizontal = 22.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            "BIENVENIDO A",
            color = Muted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 5.sp,
        )
        Text(
            "ÚLTIMA TIRADA",
            style = TextStyle(
                brush = Brush.linearGradient(listOf(Blue, Purple)),
                fontFamily = FontFamily.Serif,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "Tu comunidad oficial de Magic: The Gathering en Ceuta.\nTorneos, eventos, ligas, ranking y tienda online.",
            color = Muted,
            lineHeight = 22.sp,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onEvents,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple),
        ) {
            Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Próximos Eventos", fontSize = 15.sp, fontWeight = FontWeight.Black)
        }
        OutlinedButton(
            onClick = onStore,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(40.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Gold.copy(alpha = 0.75f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold),
        ) {
            Icon(Icons.Default.Inventory2, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Ir a la Tienda", fontSize = 15.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun HomeSection(title: String, icon: @Composable () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    listOf(Panel, Active.copy(alpha = 0.22f), Panel),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                ),
            )
            .border(1.dp, Border.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            icon()
            Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 0.sp)
        }
        content()
    }
}

@Composable
private fun HomeEventCard(event: ApiEvent, onClick: () -> Unit) {
    Column(
        Modifier.clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF5F3BB6), Color(0xFF001025)))),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Groups, null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(72.dp))
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)))),
            )
            Column(
                Modifier.align(Alignment.BottomStart).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    "${event.dayOfMonth} ${event.monthShort.lowercase().replaceFirstChar { it.uppercase() }} · ${event.time.take(5)}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                )
                Text(event.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        if (event.detailLine.isNotBlank()) {
            Text(event.detailLine, color = Muted, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun EmptyEventCard() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF5F3BB6), Color(0xFF001025)))),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Default.Groups, null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(72.dp))
        Text("Sin eventos próximos", color = Muted, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp))
    }
}

@Composable
private fun EventDetailDialog(initialEvent: ApiEvent, vm: MainViewModel, onDismiss: () -> Unit) {
    var event by remember(initialEvent.id) { mutableStateOf(initialEvent) }
    var actionMessage by remember { mutableStateOf<String?>(null) }
    var selectedPlayerId by remember { mutableStateOf<Int?>(null) }
    var showPaymentForm by remember { mutableStateOf(false) }
    val requiresPayment = (event.requiresPayment ?: 0) > 0 && (event.price ?: 0.0) > 0.0

    LaunchedEffect(initialEvent.id) {
        while (true) {
            vm.fetchEventDetail(initialEvent.id) { updated ->
                if (updated != null) event = updated
            }
            delay(5_000)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF070711)),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                contentPadding = PaddingValues(bottom = 110.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF211F32), contentColor = Purple),
                        ) {
                            Text("Cerrar", fontSize = 18.sp)
                        }
                    }
                }
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF5F2DAE), Color(0xFF050509)))),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (!event.image.isNullOrBlank()) {
                            AsyncImage(event.image, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)))
                        }
                        Icon(Icons.Default.Groups, null, tint = Color.White, modifier = Modifier.size(86.dp))
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        EventTag(event.format ?: "Evento", Purple)
                        EventTag(event.type?.replaceFirstChar { it.uppercase() } ?: "Torneo", Blue)
                        if (requiresPayment) EventTag("%.2f€".format(event.price), Gold)
                    }
                }
                item {
                    Text(event.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                }
                item {
                    EventInfoPanel(event)
                }
                item {
                    EventPlayersPanel(event) { playerId -> selectedPlayerId = playerId }
                }
                actionMessage?.let {
                    item { Text(it, color = Gold, fontWeight = FontWeight.Bold) }
                }
            }
            Button(
                onClick = {
                    if (requiresPayment) {
                        showPaymentForm = true
                    } else {
                        vm.joinEvent(event) { message ->
                            actionMessage = message ?: "Solicitud enviada."
                            vm.fetchEventDetail(event.id) { updated ->
                                if (updated != null) event = updated
                            }
                        }
                    }
                },
                enabled = event.isJoined != true && event.paymentPending != true,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 18.dp)
                    .height(62.dp),
                shape = RoundedCornerShape(34.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple,
                    disabledContainerColor = Color(0xFF3A3847),
                    disabledContentColor = Color(0xFF85818F),
                ),
            ) {
                Text(
                    when {
                        event.paymentPending == true -> "Pago pendiente"
                        event.isJoined == true -> "Ya estás apuntado"
                        else -> "Apuntarme"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }

    if (showPaymentForm) {
        PaymentFormSheet(
            title = "Inscripción al evento",
            subtitle = "Completa tu inscripción a ${event.title}.",
            summaryTitle = event.title,
            summarySubtitle = "${event.format ?: "Evento"} · ${event.dayOfMonth} ${event.monthShort}",
            subtotal = event.price ?: 0.0,
            chequetiendaBalance = vm.uiState.value.currentUser?.chequetiendaBalance ?: 0.0,
            onDismiss = { showPaymentForm = false },
            onConfirm = { method, phone, email, note, discount, useCheque ->
                vm.joinEventWithPayment(event, method, phone, email, note, discount, useCheque) { error ->
                    showPaymentForm = false
                    actionMessage = error ?: "¡Inscripción confirmada!"
                    vm.fetchEventDetail(event.id) { updated -> if (updated != null) event = updated }
                }
            },
        )
    }

    selectedPlayerId?.let { id ->
        PublicProfileSheet(userId = id, vm = vm, onDismiss = { selectedPlayerId = null })
    }
}

@Composable
private fun EventTag(text: String, color: Color) {
    Text(
        text,
        color = Color.White,
        fontWeight = FontWeight.Black,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(color)
            .padding(horizontal = 14.dp, vertical = 7.dp),
    )
}

@Composable
private fun ProductStockTag(stock: Int) {
    Text(
        "Stock: $stock",
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 15.sp,
        maxLines = 1,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (stock > 0) Color(0xFF22B94F) else Color(0xFFFF555D))
            .padding(horizontal = 14.dp, vertical = 7.dp),
    )
}

@Composable
private fun EventInfoPanel(event: ApiEvent) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF19162C))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        EventInfoRow(Icons.Default.CalendarMonth, "Fecha", "${event.dayOfMonth} ${event.monthShort.lowercase().replaceFirstChar { it.uppercase() }}, ${event.time.take(5)}")
        EventInfoRow(Icons.Default.Storefront, "Ubicación", event.location ?: "Sede Última Tirada")
        EventInfoRow(Icons.Default.Groups, "Jugadores", "${event.currentPlayers ?: event.players?.size ?: 0} / ${event.maxPlayers ?: 0}")
        EventInfoRow(Icons.Default.ArrowCircleRight, "Precio", "%.2f€".format(event.price ?: 0.0))
    }
}

@Composable
private fun EventInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, null, tint = Purple, modifier = Modifier.size(18.dp))
        Column {
            Text(label, color = Color(0xFFB8B1CC), fontWeight = FontWeight.Black, fontSize = 12.sp)
            Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
        }
    }
}

@Composable
private fun EventPlayersPanel(event: ApiEvent, onPlayerTap: (Int) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF19162C))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Groups, null, tint = Color.White, modifier = Modifier.size(16.dp))
            Text("Jugadores apuntados", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
        val players = event.players.orEmpty()
        if (players.isEmpty()) {
            Text("Aún no hay jugadores apuntados.", color = Color(0xFFB8B1CC), fontWeight = FontWeight.Bold)
        } else {
            players.forEach { player ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onPlayerTap(player.id) }
                        .border(1.dp, DeepPurple, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Avatar(player.avatarUrl, player.avatarColor, player.initial, 46.dp)
                    Column(Modifier.weight(1f)) {
                        Text(player.nick, color = Color.White, fontWeight = FontWeight.Black, fontSize = 19.sp)
                        Text(player.name, color = Color(0xFFB8B1CC), fontSize = 15.sp, maxLines = 1)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = Muted, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun FeaturedProductsCarousel(products: List<ApiProduct>, onOpenStore: () -> Unit) {
    if (products.isEmpty()) {
        EmptyHint("Aún no hay productos destacados.")
    } else {
        var currentIndex by remember { mutableStateOf(0) }
        val listState = rememberLazyListState()

        LaunchedEffect(products.size) {
            if (products.size > 1) {
                while (true) {
                    delay(3_500)
                    currentIndex = (currentIndex + 1) % products.size
                    listState.animateScrollToItem(currentIndex)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box {
                LazyRow(
                    state = listState,
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    userScrollEnabled = false,
                ) {
                    items(products, key = { it.id }) { product ->
                        FeaturedProductHomeCard(product, onOpenStore)
                    }
                }
                // Nav arrows overlaid on image area
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    VideoArrow(Icons.Default.ChevronLeft) {
                        currentIndex = if (currentIndex == 0) products.lastIndex else currentIndex - 1
                    }
                    VideoArrow(Icons.Default.ChevronRight) {
                        currentIndex = (currentIndex + 1) % products.size
                    }
                }
                LaunchedEffect(currentIndex) {
                    listState.animateScrollToItem(currentIndex)
                }
            }
            // Pagination dots
            if (products.size > 1) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    products.forEachIndexed { index, _ ->
                        Box(
                            Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (index == currentIndex) 8.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (index == currentIndex) Purple else Color(0xFF4A4560)),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedProductHomeCard(product: ApiProduct, onOpenStore: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xF20A0913))
            .border(1.dp, DeepPurple.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF3B216F), Color(0xFF060813)))),
        ) {
            ProductImage(product.image, Modifier.fillMaxSize().padding(16.dp))
            Text(
                "Destacado",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Purple)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(product.name.uppercase(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Text(product.priceFormatted, color = Blue, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
        Text(
            "${product.brand ?: "ULTIMA TIRADA"} · ${product.collection ?: product.category ?: "TIENDA"}".uppercase(),
            color = Color(0xFFB8B1CC),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier.clickable(onClick = onOpenStore),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(Icons.Default.ArrowCircleRight, null, tint = Color.White, modifier = Modifier.size(16.dp))
                Text("Ver en tienda", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.weight(1f))
            Text(
                "Stock: ${product.stock}",
                color = if (product.isInStock) Color(0xFF56E45D) else Color(0xFFFF555D),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

@Composable
private fun YouTubeCarousel(videos: List<ApiYouTubeVideo>) {
    val context = LocalContext.current
    if (videos.isEmpty()) {
        YouTubeCardStandalone(
            title = "Última Tirada",
            thumbnail = null,
            onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@ultimatirada")))
            },
        )
    } else {
        var selectedIndex by remember(videos.size) { mutableStateOf(0) }
        val listState = rememberLazyListState()
        Box {
            LazyRow(
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                userScrollEnabled = false,
            ) {
                items(videos, key = { it.id }) { video ->
                    YouTubeCard(
                        title = video.title,
                        thumbnail = video.thumbnailUrl,
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(video.watchUrl)))
                        },
                    )
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                VideoArrow(Icons.Default.ChevronLeft) {
                    selectedIndex = if (selectedIndex == 0) videos.lastIndex else selectedIndex - 1
                }
                VideoArrow(Icons.Default.ChevronRight) {
                    selectedIndex = (selectedIndex + 1) % videos.size
                }
            }
            LaunchedEffect(selectedIndex) {
                listState.animateScrollToItem(selectedIndex)
            }
        }
    }
}

@Composable
private fun VideoArrow(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.68f))
            .border(1.dp, DeepPurple, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun YouTubeCard(title: String, thumbnail: String?, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, DeepPurple, RoundedCornerShape(8.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF130B22), Color(0xFF000000))))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (thumbnail != null) {
            AsyncImage(
                model = thumbnail,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.30f)))
        }
        Text(
            title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.TopStart).padding(14.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Box(Modifier.size(52.dp).clip(CircleShape).background(Color(0xFFFF5656)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.PlayCircle, null, tint = Color.White, modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
private fun YouTubeCardStandalone(title: String, thumbnail: String?, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, DeepPurple, RoundedCornerShape(8.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF130B22), Color(0xFF000000))))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (thumbnail != null) {
            AsyncImage(model = thumbnail, contentDescription = title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.30f)))
        }
        Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black, modifier = Modifier.align(Alignment.TopStart).padding(14.dp), maxLines = 2, overflow = TextOverflow.Ellipsis)
        Box(Modifier.size(52.dp).clip(CircleShape).background(Color(0xFFFF5656)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.PlayCircle, null, tint = Color.White, modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
private fun TopPlayerCard(position: Int, player: ApiRankingEntry, onClick: () -> Unit) {
    val accent = when (position) {
        1 -> Gold
        2 -> Blue
        else -> Purple
    }
    val isFirst = position == 1
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        accent.copy(alpha = if (isFirst) 0.28f else 0.16f),
                        CardColor,
                        CardColor.copy(alpha = 0.92f),
                    ),
                ),
            )
            .border(if (isFirst) 1.5.dp else 1.dp, accent.copy(alpha = if (isFirst) 1f else 0.55f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = if (isFirst) 16.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Avatar(player.avatarUrl, player.avatarColor, player.initial, if (isFirst) 52.dp else 42.dp)
            Text(
                "#$position",
                color = if (isFirst) Color.Black else Color.White,
                fontWeight = FontWeight.Black,
                fontSize = if (isFirst) 11.sp else 9.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(accent)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                "@${player.nick}",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = if (isFirst) 16.sp else 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(player.name, color = Muted, fontWeight = FontWeight.Medium, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                ScoreChip("${player.points} pts", Blue)
                ScoreChip("${player.wins}V", Color(0xFF4BE66D))
                ScoreChip("${player.draws}E", Gold)
                ScoreChip("${player.losses}D", Color(0xFFFF555D))
            }
        }
    }
}

@Composable
private fun ScoreChip(text: String, color: Color) {
    Text(text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Black)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoreScreen(products: List<ApiProduct>, loading: Boolean, cart: CartStore, vm: MainViewModel, adsRemoved: Boolean = false) {
    var search by remember { mutableStateOf("") }
    var showSell by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<ApiProduct?>(null) }
    var selectedBrands by remember { mutableStateOf(setOf<String>()) }
    var selectedCollections by remember { mutableStateOf(setOf<String>()) }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var stockFilter by remember { mutableStateOf("Todos") }
    var sortOption by remember { mutableStateOf("Destacados") }
    val brands = products.mapNotNull { it.brand?.takeIf(String::isNotBlank) }.distinct().sorted()
    val collections = products.mapNotNull { it.collection?.takeIf(String::isNotBlank) }.distinct().sorted()
    val categories = products.mapNotNull { it.category?.takeIf(String::isNotBlank) }.distinct().sorted()
    val min = minPrice.replace(",", ".").toDoubleOrNull()
    val max = maxPrice.replace(",", ".").toDoubleOrNull()
    val filtered = products.filter { product ->
        val matchesSearch = search.isBlank() ||
            product.name.contains(search, true) ||
            product.brand.orEmpty().contains(search, true) ||
            product.collection.orEmpty().contains(search, true) ||
            product.category.orEmpty().contains(search, true)
        val matchesBrand = selectedBrands.isEmpty() || selectedBrands.contains(product.brand.orEmpty())
        val matchesCollection = selectedCollections.isEmpty() || selectedCollections.contains(product.collection.orEmpty())
        val matchesCategory = selectedCategories.isEmpty() || selectedCategories.contains(product.category.orEmpty())
        val matchesPrice = (min == null || product.price >= min) && (max == null || product.price <= max)
        val matchesStock = when (stockFilter) {
            "En stock" -> product.stock > 0
            "Sin stock" -> product.stock <= 0
            else -> true
        }
        matchesSearch && matchesBrand && matchesCollection && matchesCategory && matchesPrice && matchesStock
    }.let { list ->
        when (sortOption) {
            "Precio ↑" -> list.sortedBy { it.price }
            "Precio ↓" -> list.sortedByDescending { it.price }
            "Nombre" -> list.sortedBy { it.name.lowercase() }
            else -> list.sortedWith(compareByDescending<ApiProduct> { it.featured }.thenBy { it.name })
        }
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item { StoreTitle() }
        item { SellCardsBanner { showSell = true } }
        item {
            StoreFiltersPanel(
                brands = brands,
                collections = collections,
                categories = categories,
                selectedBrands = selectedBrands,
                selectedCollections = selectedCollections,
                selectedCategories = selectedCategories,
                minPrice = minPrice,
                maxPrice = maxPrice,
                stockFilter = stockFilter,
                sortOption = sortOption,
                onToggleBrand = { selectedBrands = selectedBrands.toggle(it) },
                onToggleCollection = { selectedCollections = selectedCollections.toggle(it) },
                onToggleCategory = { selectedCategories = selectedCategories.toggle(it) },
                onMinPrice = { minPrice = it },
                onMaxPrice = { maxPrice = it },
                onStockFilter = { stockFilter = it },
                onSortOption = { sortOption = it },
                onClear = {
                    selectedBrands = emptySet()
                    selectedCollections = emptySet()
                    selectedCategories = emptySet()
                    minPrice = ""
                    maxPrice = ""
                    stockFilter = "Todos"
                    sortOption = "Destacados"
                    search = ""
                },
            )
        }
        item {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Buscar producto") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = loginFieldColors(),
            )
        }
        if (loading && products.isEmpty()) {
            item { Box(Modifier.fillMaxWidth().height(260.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Purple) } }
        } else {
            item {
                Text(
                    "${filtered.size} productos",
                    color = Color(0xFFB8B1CC),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                )
            }
            val chunked = filtered.chunked(2)
            itemsIndexed(chunked, key = { _, row -> row.joinToString("-") { it.id.toString() } }) { index, row ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        row.forEach { product ->
                            Box(Modifier.weight(1f)) {
                                ProductCard(product, onTap = { selectedProduct = product })
                            }
                        }
                        if (row.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                    if (!adsRemoved && (index + 1) % 5 == 0) {
                        NativeAdCard(MonetizationConfig.NATIVE_STORE_AD_UNIT_ID)
                    }
                }
            }
        }
    }

    if (showSell) SellDialog(vm = vm, onDismiss = { showSell = false })
    selectedProduct?.let { product ->
        ProductDetailDialog(
            product = product,
            cart = cart,
            onDismiss = { selectedProduct = null },
        )
    }
}

private fun Set<String>.toggle(value: String): Set<String> = if (contains(value)) this - value else this + value

@Composable
private fun StoreTitle() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Default.Inventory2, null, tint = Color.White, modifier = Modifier.size(24.dp))
            Text("Tienda", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
        }
        Text(
            "Accesorios, fundas, binders y material para tus partidas.",
            color = Color(0xFFB8B1CC),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun SellCardsBanner(onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xAA11101C))
            .border(1.dp, Gold.copy(alpha = 0.30f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(Modifier.size(44.dp).clip(CircleShape).background(Gold.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Euro, null, tint = Gold, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f)) {
            Text("¿Quieres vender tus cartas?", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Text("Pide una valoración gratuita", color = Color(0xFFB8B1CC), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFB8B1CC), modifier = Modifier.size(18.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoreFiltersPanel(
    brands: List<String>,
    collections: List<String>,
    categories: List<String>,
    selectedBrands: Set<String>,
    selectedCollections: Set<String>,
    selectedCategories: Set<String>,
    minPrice: String,
    maxPrice: String,
    stockFilter: String,
    sortOption: String,
    onToggleBrand: (String) -> Unit,
    onToggleCollection: (String) -> Unit,
    onToggleCategory: (String) -> Unit,
    onMinPrice: (String) -> Unit,
    onMaxPrice: (String) -> Unit,
    onStockFilter: (String) -> Unit,
    onSortOption: (String) -> Unit,
    onClear: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xEE0D0A1C))
            .border(1.2.dp, DeepPurple.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.FilterList, null, tint = Color.White, modifier = Modifier.size(18.dp))
            Text("Filtros", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
        FilterRow("Marca", brands, selectedBrands, onToggleBrand)
        FilterRow("Colección", collections, selectedCollections, onToggleCollection)
        FilterRow("Categoría", categories, selectedCategories, onToggleCategory)
        Text("Rango de precio", color = Color(0xFFB8B1CC), fontSize = 12.sp, fontWeight = FontWeight.Black)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StoreSmallField("Mín €", minPrice, onMinPrice, Modifier.weight(1f))
            StoreSmallField("Máx €", maxPrice, onMaxPrice, Modifier.weight(1f))
        }
        Text("Stock", color = Color(0xFFB8B1CC), fontSize = 12.sp, fontWeight = FontWeight.Black)
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF343240)),
        ) {
            listOf("Todos", "En stock", "Sin stock").forEach { option ->
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(28.dp))
                        .background(if (stockFilter == option) Color(0xFF777480) else Color.Transparent)
                        .clickable { onStockFilter(option) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(option, color = Color.White, fontSize = 16.sp)
                }
            }
        }
        Text("Ordenar", color = Color(0xFFB8B1CC), fontSize = 12.sp, fontWeight = FontWeight.Black)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("Destacados", "Precio ↑", "Precio ↓", "Nombre").forEach { option ->
                FilterChipPill(option, sortOption == option) { onSortOption(option) }
            }
        }
        Button(
            onClick = onClear,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF211B3F), contentColor = Color.White),
        ) {
            Text("Limpiar filtros", fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun FilterRow(title: String, options: List<String>, selected: Set<String>, onToggle: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, color = Color(0xFFB8B1CC), fontSize = 16.sp, fontWeight = FontWeight.Black)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(options) { option -> FilterChipPill(option.uppercase(), selected.contains(option)) { onToggle(option) } }
        }
    }
}

@Composable
private fun FilterChipPill(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text,
        color = if (selected) Color.White else Color(0xFFB8B1CC),
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier
            .clip(RoundedCornerShape(26.dp))
            .background(if (selected) DeepPurple.copy(alpha = 0.55f) else Color.Transparent)
            .border(1.dp, DeepPurple.copy(alpha = 0.75f), RoundedCornerShape(26.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
    )
}

@Composable
private fun StoreSmallField(label: String, value: String, onValue: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        placeholder = { Text(label) },
        singleLine = true,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = loginFieldColors(),
    )
}

@Composable
private fun ProductCarousel(products: List<ApiProduct>) {
    if (products.isEmpty()) {
        EmptyHint("Aún no hay productos destacados.")
    } else {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(products, key = { it.id }) { product ->
                ProductMiniCard(product)
            }
        }
    }
}

@Composable
private fun ProductMiniCard(product: ApiProduct) {
    Card(colors = CardDefaults.cardColors(containerColor = CardColor), modifier = Modifier.width(170.dp)) {
        ProductImage(product.image, Modifier.fillMaxWidth().height(112.dp))
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(product.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(product.priceFormatted, color = Gold, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun ProductCard(product: ApiProduct, onTap: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xEE0D0A1C)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.clickable(onClick = onTap),
    ) {
        ProductImage(product.image, Modifier.fillMaxWidth().aspectRatio(1.1f).padding(8.dp))
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(product.name.uppercase(), color = Color.White, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(product.brand ?: product.collection ?: "Última Tirada", color = Color(0xFFB8B1CC), fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(product.priceFormatted, color = Blue, fontWeight = FontWeight.Black, fontSize = 18.sp, modifier = Modifier.weight(1f))
                Text("Stock: ${product.stock}", color = if (product.isInStock) Color(0xFF56E45D) else Color(0xFFFF555D), fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun ProductDetailDialog(product: ApiProduct, cart: CartStore, onDismiss: () -> Unit) {
    var quantity by remember(product.id) { mutableStateOf(1) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        Box(Modifier.fillMaxSize().background(Color(0xFF070711))) {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 26.dp, vertical = 18.dp)
                    .padding(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF211F32), contentColor = Purple),
                        ) { Text("Cerrar", fontSize = 18.sp) }
                    }
                }
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(310.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF4B2388), Color(0xFF050509)))),
                        contentAlignment = Alignment.Center,
                    ) {
                        ProductImage(product.image, Modifier.fillMaxSize().padding(28.dp))
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text((product.brand ?: "ULTIMA TIRADA").uppercase(), color = Color(0xFFB8B1CC), fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                        Text(product.name.uppercase(), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black, lineHeight = 26.sp)
                        Text(product.priceFormatted, color = Purple, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            EventTag(product.collection ?: "TIENDA", Purple)
                            EventTag(product.category ?: "PRODUCTO", Blue)
                            ProductStockTag(product.stock)
                        }
                    }
                }
                item {
                    Column(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF19162C)).padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Descripción", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Text(product.description ?: "Sin descripción disponible.", color = Color(0xFFB8B1CC), fontSize = 14.sp)
                    }
                }
                item {
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF19162C)).padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Cantidad", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                        Row(Modifier.clip(RoundedCornerShape(28.dp)).background(Color(0xFF343240)), verticalAlignment = Alignment.CenterVertically) {
                            IconButton({ quantity = (quantity - 1).coerceAtLeast(1) }) { Icon(Icons.Default.Remove, null, tint = Color.White) }
                            Text("$quantity", color = Color.White, fontWeight = FontWeight.Black)
                            IconButton({ quantity = (quantity + 1).coerceAtMost(product.stock.coerceAtLeast(1)) }) { Icon(Icons.Default.Add, null, tint = Color.White) }
                        }
                    }
                }
            }
            Button(
                onClick = {
                    cart.add(product, quantity)
                    onDismiss()
                },
                enabled = product.isInStock,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp)
                    .padding(bottom = 54.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
            ) {
                Icon(Icons.Default.ShoppingCart, null)
                Spacer(Modifier.width(8.dp))
                Text("Añadir al carrito", fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun ProductImage(url: String?, modifier: Modifier) {
    if (url.isNullOrBlank()) {
        Box(modifier.background(Color(0xFF24264A)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.LocalMall, null, tint = Muted, modifier = Modifier.size(40.dp))
        }
    } else {
        AsyncImage(model = url, contentDescription = null, modifier = modifier.background(Color(0xFF24264A)), contentScale = ContentScale.Crop)
    }
}

@Composable
private fun EventsScreen(state: UiState, vm: MainViewModel) {
    var visibleMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEvent by remember { mutableStateOf<ApiEvent?>(null) }
    val listState = rememberLazyListState()
    val eventsByDate = remember(state.events) {
        state.events.mapNotNull { event -> event.startDate?.toLocalDate()?.let { it to event } }.groupBy({ it.first }, { it.second })
    }
    val upcoming = remember(state.upcomingEvents, selectedDate) {
        val selected = selectedDate
        if (selected == null) {
            state.upcomingEvents
        } else {
            state.upcomingEvents.sortedWith(
                compareByDescending<ApiEvent> { it.startDate?.toLocalDate() == selected }
                    .thenBy { it.startDate },
            )
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(24.dp))
                Column {
                    Text("Eventos", color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
                    Text("Torneos, ligas y quedadas de la comunidad.", color = Muted, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        item {
            EventsCalendarCard(
                month = visibleMonth,
                eventsByDate = eventsByDate,
                selectedDate = selectedDate,
                onPrevious = { visibleMonth = visibleMonth.minusMonths(1) },
                onNext = { visibleMonth = visibleMonth.plusMonths(1) },
                onDateSelected = { date ->
                    selectedDate = date
                    eventsByDate[date]?.firstOrNull()?.let {
                        selectedEvent = it
                    }
                },
            )
        }
        item {
            EventsListSection(
                events = upcoming,
                selectedDate = selectedDate,
                onEventClick = { selectedEvent = it },
            )
        }
    }

    selectedEvent?.let { event ->
        EventDetailDialog(
            initialEvent = event,
            vm = vm,
            onDismiss = { selectedEvent = null },
        )
    }
}

@Composable
private fun EventsCalendarCard(
    month: YearMonth,
    eventsByDate: Map<LocalDate, List<ApiEvent>>,
    selectedDate: LocalDate?,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xEE0C0A1A))
            .border(1.2.dp, DeepPurple.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
            .padding(horizontal = 18.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(16.dp))
            Text("Calendario de eventos", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            CircleIconButton(Icons.Default.ChevronLeft, onPrevious)
            Text(
                month.month.getDisplayName(java.time.format.TextStyle.FULL, Locale("es", "ES"))
                    .replaceFirstChar { it.uppercase() } + " ${month.year}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            CircleIconButton(Icons.Default.ChevronRight, onNext)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("L", "M", "X", "J", "V", "S", "D").forEach {
                Text(it, color = Muted, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            }
        }
        val firstOfMonth = month.atDay(1)
        val gridStart = firstOfMonth.minusDays((firstOfMonth.dayOfWeek.value - 1).toLong())
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(6) { week ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    repeat(7) { day ->
                        val date = gridStart.plusDays((week * 7 + day).toLong())
                        val events = eventsByDate[date].orEmpty()
                        CalendarDayCell(
                            date = date,
                            inMonth = date.month == month.month,
                            hasEvent = events.isNotEmpty(),
                            color = events.firstOrNull()?.eventColor() ?: Purple,
                            selected = selectedDate == date,
                            onClick = { if (events.isNotEmpty()) onDateSelected(date) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            CalendarLegend("Torneo", Purple)
            Spacer(Modifier.width(18.dp))
            CalendarLegend("Casual", Color(0xFF2DD66F))
            Spacer(Modifier.width(18.dp))
            CalendarLegend("Especial", Color(0xFFFFA13A))
        }
    }
}

@Composable
private fun CircleIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFF090914)),
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    inMonth: Boolean,
    hasEvent: Boolean,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (hasEvent) Modifier.border(1.3.dp, Purple, RoundedCornerShape(10.dp)) else Modifier,
            )
            .background(if (selected) DeepPurple.copy(alpha = 0.35f) else Color.Transparent)
            .clickable(enabled = hasEvent, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            date.dayOfMonth.toString(),
            color = when {
                !inMonth -> Color(0xFF464355)
                hasEvent -> Color.White
                else -> Color(0xFF6E6A80)
            },
            fontSize = 14.sp,
            fontWeight = if (hasEvent) FontWeight.Black else FontWeight.Normal,
        )
        if (hasEvent) {
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 3.dp)
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

@Composable
private fun CalendarLegend(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Text(label, color = Muted, fontWeight = FontWeight.Black, fontSize = 15.sp)
    }
}

@Composable
private fun EventsListSection(events: List<ApiEvent>, selectedDate: LocalDate?, onEventClick: (ApiEvent) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xEE0C0A1A))
            .border(1.2.dp, DeepPurple.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(16.dp))
            Text("Próximos eventos", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
        if (selectedDate != null) {
            val count = events.count { it.startDate?.toLocalDate() == selectedDate }
            Text(
                if (count > 0) "Mostrando primero los eventos del ${selectedDate.toString().isoDateLabel()}." else "No hay eventos ese día.",
                color = Muted,
                fontWeight = FontWeight.Bold,
            )
        }
        if (events.isEmpty()) {
            EmptyHint("Sin eventos próximos por ahora.")
        } else {
            events.forEach { event ->
                EventListCard(event, highlighted = selectedDate == event.startDate?.toLocalDate(), onClick = { onEventClick(event) })
            }
        }
    }
}

@Composable
private fun EventListCard(event: ApiEvent, highlighted: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF19162C))
            .border(1.4.dp, if (highlighted) Gold else DeepPurple.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            Modifier
                .width(52.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2D2164)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(event.dayOfMonth, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(event.monthShort, color = Purple, fontWeight = FontWeight.Black, fontSize = 11.sp)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(event.title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp, lineHeight = 19.sp)
            Text(event.detailLine, color = Muted, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("${event.currentPlayers ?: event.players?.size ?: 0}/${event.maxPlayers ?: 0} jugadores", color = Blue, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
    }
}

private fun ApiEvent.eventColor(): Color {
    val typeText = listOfNotNull(type, format, title).joinToString(" ").lowercase()
    return when {
        "casual" in typeText || "liga" in typeText -> Color(0xFF2DD66F)
        "especial" in typeText || "presentación" in typeText || "presentacion" in typeText -> Color(0xFFFFA13A)
        else -> Purple
    }
}

@Composable
private fun CommunityScreen(state: UiState, vm: MainViewModel, adsRemoved: Boolean = false) {
    var postText by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }
    var feedType by remember { mutableStateOf(CommunityFeedType.ForYou) }
    var posting by remember { mutableStateOf(false) }
    var postError by remember { mutableStateOf<String?>(null) }
    var selectedPublicUserId by remember { mutableStateOf<Int?>(null) }
    val filteredPosts = remember(state.communityPosts, searchText) {
        val query = searchText.trim().removePrefix("@")
        if (query.isBlank()) {
            state.communityPosts
        } else {
            state.communityPosts.filter {
                it.content.contains(query, ignoreCase = true) ||
                    it.userName.contains(query, ignoreCase = true) ||
                    it.userNick.contains(query, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(feedType) {
        vm.loadCommunity(feedType)
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item { CommunityHero() }
        item {
            CommunitySection(title = "Publicar", icon = Icons.Default.ModeEdit) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                    state.currentUser?.let { Avatar(it.avatarUrl, it.avatarColor, it.initial, 48.dp) }
                    OutlinedTextField(
                        postText,
                        { if (it.length <= 280) postText = it },
                        placeholder = { Text("¿Qué se mueve en el meta?") },
                        modifier = Modifier.weight(1f).height(116.dp),
                        colors = loginFieldColors(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 3,
                    )
                }
                postError?.let { Text(it, color = Color(0xFFFF8A80), fontWeight = FontWeight.Bold) }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("${postText.length}/280", color = if (postText.length > 280) Color(0xFFFF8A80) else Muted, fontWeight = FontWeight.Black)
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = {
                            posting = true
                            postError = null
                            vm.publishPost(postText) { error ->
                                posting = false
                                if (error == null) {
                                    postText = ""
                                    vm.loadCommunity(feedType)
                                } else {
                                    postError = error
                                }
                            }
                        },
                        enabled = !posting && postText.trim().isNotBlank() && postText.length <= 280,
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple),
                    ) {
                        Icon(Icons.Default.Send, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (posting) "Publicando..." else "Publicar", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
        item {
            CommunitySection(title = "Buscar", icon = Icons.Default.Search) {
                OutlinedTextField(
                    searchText,
                    { searchText = it },
                    placeholder = { Text("Busca @nick, jugadores o contenido") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = loginFieldColors(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                )
            }
        }
        item {
            SegmentedControl(
                options = listOf(CommunityFeedType.ForYou.label, CommunityFeedType.Following.label),
                selected = feedType.label,
            ) { label ->
                feedType = CommunityFeedType.entries.first { it.label == label }
            }
        }
        if (!adsRemoved) {
            item { RewardedAdCard(vm) }
        }
        item {
            CommunitySection(title = "Timeline", icon = Icons.Default.ChatBubble) {
                if (filteredPosts.isEmpty()) {
                    EmptyHint(if (feedType == CommunityFeedType.Following) "Aún no hay posts de jugadores seguidos." else "No hay posts que coincidan con la búsqueda.")
                } else {
                    filteredPosts.forEachIndexed { index, post ->
                        CommunityPostCard(post, onLike = { vm.toggleLike(post) }, onAuthorTap = { selectedPublicUserId = post.userId })
                        // Native ad every 10 posts
                        if (!adsRemoved && (index + 1) % 10 == 0) {
                            Spacer(Modifier.height(8.dp))
                            NativeAdCard(MonetizationConfig.NATIVE_COMMUNITY_AD_UNIT_ID)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
        if (state.sortedRanking.isNotEmpty()) {
            item {
                Row(
                    Modifier.fillMaxWidth().padding(top = 6.dp, start = 4.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(Icons.Default.EmojiEvents, null, tint = Gold, modifier = Modifier.size(24.dp))
                    Text("RANKING DE LA COMUNIDAD", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                }
            }
            items(state.sortedRanking, key = { it.id }) { player ->
                CommunityRankingRow(state.sortedRanking.indexOf(player) + 1, player) { selectedPublicUserId = player.id }
            }
        }
    }
    selectedPublicUserId?.let { id ->
        PublicProfileSheet(userId = id, vm = vm, onDismiss = { selectedPublicUserId = null })
    }
}

@Composable
private fun CommunityPostCard(post: ApiCommunityPost, onLike: () -> Unit, onAuthorTap: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    if (post.isPinned) listOf(Color(0xFF2C2318), Color(0xFF19162C), Color(0xFF11101D))
                    else listOf(Color(0xFF15132A), Color(0xFF20163A)),
                ),
            )
            .border(1.2.dp, if (post.isPinned) Gold.copy(alpha = 0.55f) else DeepPurple.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (post.isPinned) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(Gold.copy(alpha = 0.14f))
                    .border(1.dp, Gold.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Default.PushPin, null, tint = Gold, modifier = Modifier.size(16.dp))
                Text("Fijado", color = Gold, fontWeight = FontWeight.Black)
            }
        }
        Row(
            Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onAuthorTap).padding(4.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Avatar(post.avatarUrl, post.avatarColor, post.initials, 44.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(post.userName, color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (post.userRole == "admin") {
                        Text(
                            "STAFF",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(Gold).padding(horizontal = 7.dp, vertical = 3.dp),
                        )
                    }
                    post.latestMedalUrl?.takeIf { it.isNotBlank() }?.let {
                        AsyncImage(it, null, Modifier.size(22.dp), contentScale = ContentScale.Fit)
                    }
                }
                Text("@${post.userNick} · ${post.createdAt.relativeLabel()}", color = Muted, fontSize = 15.sp, maxLines = 1)
            }
        }
        Text(post.content, color = Color.White, fontSize = 15.sp, lineHeight = 21.sp)
        post.imageUrl?.takeIf { it.isNotBlank() }?.let {
            AsyncImage(it, null, Modifier.fillMaxWidth().height(210.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CommunityActionPill(Icons.Default.Favorite, "${post.likes}", if (post.likedByMe) Color(0xFFFF5C9A) else Muted, onLike)
            CommunityActionPill(Icons.Default.ChatBubble, if (post.commentsCount > 0) "${post.commentsCount}" else "Responder", Blue) {}
        }
    }
}

@Composable
private fun CommunityHero() {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF20173D), Color(0xFF11244B), Color(0xFF10101D))))
            .border(1.2.dp, DeepPurple.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Default.Groups, null, tint = Purple, modifier = Modifier.size(24.dp))
            Text(
                "Comunidad",
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(Blue, Purple)),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                ),
            )
        }
        Text(
            "Publica micro-posts, sigue a otros jugadores y mueve el meta de Última Tirada.",
            color = Muted,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun CommunitySection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xEE0C0A1A))
            .border(1.2.dp, DeepPurple.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(16.dp))
            Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
        content()
    }
}

@Composable
private fun CommunityActionPill(icon: ImageVector, text: String, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
    ) {
        Row(
            Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Text(text, color = color, fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
    }
}

@Composable
private fun CommunityRankingRow(position: Int, player: ApiRankingEntry, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(Color(0xFF0F0E18))
            .border(1.1.dp, if (position <= 3) Gold.copy(alpha = 0.45f) else DeepPurple.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("#$position", color = if (position <= 3) Gold else Purple, fontSize = 14.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(32.dp))
        Avatar(player.avatarUrl, player.avatarColor, player.initial, 38.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("@${player.nick}", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${player.wins}V · ${player.losses}D · ${player.draws}E", color = Muted, fontSize = 12.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("${player.points}", color = Blue, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text("PTS", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
    }
}

private fun String.relativeLabel(): String = runCatching {
    val normalized = replace("Z", "").replace("T", " ").take(19)
    val created = LocalDateTime.parse(normalized, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val minutes = ChronoUnit.MINUTES.between(created, LocalDateTime.now()).coerceAtLeast(0)
    when {
        minutes < 1 -> "ahora"
        minutes < 60 -> "hace ${minutes} min"
        minutes < 24 * 60 -> "hace ${minutes / 60} h"
        minutes < 7 * 24 * 60 -> "hace ${minutes / (24 * 60)} d"
        else -> take(10).isoDateLabel()
    }
}.getOrElse { take(10).isoDateLabel() }

@Composable
private fun ProfileScreen(state: UiState, vm: MainViewModel, adsRemoved: Boolean = false, onAdsRemoved: () -> Unit = {}) {
    val user = state.currentUser
    var selectedTab by remember { mutableIntStateOf(0) }
    val profileTabs = listOf("Ranking", "Eventos", "Historial")

    LaunchedEffect(Unit) {
        vm.reloadProfile()
        vm.loadMatchHistory()
    }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { PageTitle("Perfil", "Tu cuenta y ranking") }
        if (user != null) {
            item { ProfileHeroCard(user) }
            item { ProfileStatsSection(user) }
            if (user.followers != null || user.following != null) {
                item { ProfileSocialSection(user) }
            }
            if (!user.medals.isNullOrEmpty()) {
                item { ProfileMedalsSection(user.medals!!) }
            }
            item { ProfileInfoSection(user) }

            // Tabs: Ranking / Eventos / Historial
            item {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Color(0xFF1A1530)),
                ) {
                    profileTabs.forEachIndexed { index, label ->
                        Box(
                            Modifier.weight(1f).clip(RoundedCornerShape(28.dp))
                                .background(if (selectedTab == index) DeepPurple else Color.Transparent)
                                .clickable { selectedTab = index }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }
            }

            when (selectedTab) {
                0 -> {
                    // Ranking tab
                    val userRanking = state.sortedRanking.indexOfFirst { it.id == user.id }
                    if (userRanking >= 0) {
                        item {
                            Column(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                    .background(Panel).padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.EmojiEvents, null, tint = Gold, modifier = Modifier.size(18.dp))
                                    Text("Tu posición en el ranking", color = Color.White, fontWeight = FontWeight.Black)
                                }
                                Row(Modifier.fillMaxWidth()) {
                                    ProfileStatPill("#${userRanking + 1}", "Posición", Gold, Modifier.weight(1f))
                                    Box(Modifier.width(1.dp).height(44.dp).background(Border))
                                    ProfileStatPill("${user.points ?: 0}", "Puntos", Blue, Modifier.weight(1f))
                                    Box(Modifier.width(1.dp).height(44.dp).background(Border))
                                    ProfileStatPill("${user.wins ?: 0}/${user.losses ?: 0}/${user.draws ?: 0}", "V/D/E", Muted, Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    val topRanking = state.sortedRanking.take(10)
                    if (topRanking.isNotEmpty()) {
                        item {
                            Column(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                    .background(Panel).padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Text("Top 10 jugadores", color = Color.White, fontWeight = FontWeight.Black, modifier = Modifier.padding(bottom = 6.dp))
                                topRanking.forEachIndexed { idx, player ->
                                    RankingRow(idx + 1, player)
                                    if (idx < topRanking.lastIndex) Box(Modifier.fillMaxWidth().height(1.dp).background(Border.copy(alpha = 0.3f)))
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Eventos tab — reutiliza UserEventsSheet content inline
                    val joined = state.events.filter { it.isJoined == true || it.paymentPending == true }
                    if (joined.isEmpty()) {
                        item { EmptyHint("No te has apuntado a ningún evento aún.") }
                    } else {
                        items(joined) { event -> UserEventRow(event) }
                    }
                }
                2 -> {
                    // Historial tab
                    if (state.matchHistory.isEmpty()) {
                        item { EmptyHint("Sin historial de partidas aún.") }
                    } else {
                        items(state.matchHistory) { match -> MatchHistoryRow(match) }
                    }
                }
            }

            // Monetización
            if (!adsRemoved) {
                item { RewardedAdCard(vm) }
            }
            item { RemoveAdsCard(adsRemoved = adsRemoved, onAdsRemoved = onAdsRemoved) }

            item {
                Button(
                    onClick = { vm.logout() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC8B1A2F)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(Icons.Default.ExitToApp, null, modifier = Modifier.padding(end = 8.dp))
                    Text("Cerrar sesión", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(8.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Modo invitado", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text("Puedes ver el inicio, tienda, eventos y ranking. Inicia sesión para publicar, apuntarte o gestionar tu perfil.", color = Muted)
                    }
                }
            }
            item { LoginPanel(vm) }
        }
    }
}

@Composable
private fun RankingRow(position: Int, player: ApiRankingEntry) {
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("#$position", color = Gold, fontWeight = FontWeight.Black, modifier = Modifier.width(42.dp))
        Avatar(player.avatarUrl, player.avatarColor, player.initial, 36.dp)
        Column(Modifier.weight(1f)) {
            Text(player.nick, color = Color.White, fontWeight = FontWeight.Bold)
            Text(player.favoriteFormat ?: player.name, color = Muted, fontSize = 12.sp)
        }
        Text("${player.points} pts", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

// ─── PROFILE COMPOSABLES ─────────────────────────────────────────────────────

@Composable
private fun ProfileHeroCard(user: ApiUser) {
    val avatarColor = user.avatarColor?.toColorOrNull() ?: Purple
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Border.copy(alpha = 0.75f), RoundedCornerShape(10.dp))
            .background(Brush.linearGradient(listOf(Panel, avatarColor.copy(alpha = 0.25f), Background))),
    ) {
        Row(
            Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(user.avatarUrl, user.avatarColor, user.initials, 80.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(user.name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    if (user.role == "admin") {
                        Text(
                            "STAFF",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.clip(RoundedCornerShape(50)).background(Gold).padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
                Text("@${user.nick}", color = Muted, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                user.bio?.let { bio ->
                    if (bio.isNotBlank()) Text(bio, color = Muted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun ProfileStatsSection(user: ApiUser) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.EmojiEvents, null, tint = Blue, modifier = Modifier.size(18.dp))
                Text("Estadísticas", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
            Row(Modifier.fillMaxWidth()) {
                ProfileStatPill("${user.points ?: 0}", "Puntos", Blue, Modifier.weight(1f))
                Box(Modifier.width(1.dp).height(44.dp).background(Border))
                ProfileStatPill("${user.wins ?: 0}", "Victorias", Color(0xFF4CAF50), Modifier.weight(1f))
                Box(Modifier.width(1.dp).height(44.dp).background(Border))
                ProfileStatPill("${user.draws ?: 0}", "Empates", Gold, Modifier.weight(1f))
                Box(Modifier.width(1.dp).height(44.dp).background(Border))
                ProfileStatPill("${user.losses ?: 0}", "Derrotas", Color(0xCCF44336), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ProfileSocialSection(user: ApiUser) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Groups, null, tint = Purple, modifier = Modifier.size(18.dp))
                Text("Comunidad", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
            Row(Modifier.fillMaxWidth()) {
                ProfileStatPill("${user.followers ?: 0}", "Seguidores", Purple, Modifier.weight(1f))
                Box(Modifier.width(1.dp).height(44.dp).background(Border))
                ProfileStatPill("${user.following ?: 0}", "Siguiendo", Blue, Modifier.weight(1f))
                user.posts?.let { posts ->
                    Box(Modifier.width(1.dp).height(44.dp).background(Border))
                    ProfileStatPill("$posts", "Posts", Muted, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ProfileMedalsSection(medals: List<ApiUserMedal>) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.EmojiEvents, null, tint = Gold, modifier = Modifier.size(18.dp))
                Text("Medallas", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(medals) { medal -> MedalItem(medal) }
            }
        }
    }
}

@Composable
private fun MedalItem(medal: ApiUserMedal) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.width(70.dp),
    ) {
        if (!medal.imageUrl.isNullOrBlank()) {
            AsyncImage(model = medal.imageUrl, contentDescription = medal.name, modifier = Modifier.size(46.dp).clip(CircleShape), contentScale = ContentScale.Crop)
        } else {
            Box(Modifier.size(46.dp).clip(CircleShape).background(Gold.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.EmojiEvents, null, tint = Gold, modifier = Modifier.size(28.dp))
            }
        }
        Text(medal.name, color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 2, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ProfileInfoSection(user: ApiUser) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Info, null, tint = Blue, modifier = Modifier.size(18.dp))
                Text("Información", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
            user.favoriteFormat?.let { fmt -> if (fmt.isNotBlank()) ProfileInfoRow(Icons.Default.Style, "Formato favorito", fmt.replaceFirstChar { it.uppercase() }) }
            user.location?.let { loc -> if (loc.isNotBlank()) ProfileInfoRow(Icons.Default.LocationOn, "Ubicación", loc) }
            ProfileInfoRow(Icons.Default.Email, "Email", user.email)
            user.phone?.let { phone -> if (phone.isNotBlank()) ProfileInfoRow(Icons.Default.Info, "Teléfono", phone) }
            user.chequetiendaBalance?.let { balance ->
                if (balance > 0) ProfileInfoRow(Icons.Default.AccountBalanceWallet, "ChequeTienda", "%.2f€".format(balance))
            }
            user.socioTier?.let { tier ->
                if (tier.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Default.Shield, null, tint = Gold, modifier = Modifier.size(20.dp))
                        Column {
                            Text("Socio", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(tier.replaceFirstChar { it.uppercase() }, color = Gold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                user.socioStatus?.let { status ->
                                    Text(
                                        status.replaceFirstChar { it.uppercase() },
                                        color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black,
                                        modifier = Modifier.clip(RoundedCornerShape(30.dp)).background(Gold).padding(horizontal = 6.dp, vertical = 2.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, null, tint = Purple, modifier = Modifier.size(20.dp))
        Column {
            Text(label, color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Color.White, fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ProfileStatPill(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Text(label, color = Muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
private fun ProfileQuickActions(onMiPerfil: () -> Unit, onMisEventos: () -> Unit, onMiRanking: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.FilterList, null, tint = Muted, modifier = Modifier.size(18.dp))
                Text("Opciones", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
            ProfileActionButton(Icons.Default.Person, "Mi Perfil", "Ver perfil detallado y rango", onMiPerfil)
            ProfileActionButton(Icons.Default.CalendarMonth, "Mis Eventos", "Eventos en los que participas", onMisEventos)
            ProfileActionButton(Icons.Default.EmojiEvents, "Mi Ranking", "Tu posición y puntos", onMiRanking)
        }
    }
}

@Composable
private fun ProfileActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(CardColor)
            .border(1.dp, Border, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, null, tint = Purple, modifier = Modifier.size(22.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, color = Muted, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Muted)
    }
}

// ─── PROFILE MENU SHEET ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileMenuSheet(
    user: ApiUser,
    onMiPerfil: () -> Unit,
    onMisEventos: () -> Unit,
    onMiRanking: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Panel) {
        Column(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            Row(
                Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Avatar(user.avatarUrl, user.avatarColor, user.initials, 52.dp)
                Column {
                    Text(user.name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text("@${user.nick}", color = Muted, fontSize = 14.sp)
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(Border))
            Spacer(Modifier.height(8.dp))
            ProfileMenuOption(Icons.Default.Person, "Mi Perfil", "Ver perfil detallado y rango", onMiPerfil)
            ProfileMenuOption(Icons.Default.CalendarMonth, "Mis Eventos", "Eventos en los que participas", onMisEventos)
            ProfileMenuOption(Icons.Default.EmojiEvents, "Mi Ranking", "Tu posición y puntos", onMiRanking)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileMenuOption(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Purple.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Purple, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(subtitle, color = Muted, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Muted, modifier = Modifier.size(20.dp))
    }
}

// ─── PROFILE SHEET (shared + public) ─────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserProfileSheet(user: ApiUser, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Panel) {
        ProfileSheetContent(user = user, title = "Mi Perfil")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PublicProfileSheet(userId: Int, vm: MainViewModel, onDismiss: () -> Unit) {
    var user by remember { mutableStateOf<ApiUser?>(null) }
    var loading by remember { mutableStateOf(true) }
    LaunchedEffect(userId) {
        vm.fetchPublicProfile(userId) { loaded ->
            user = loaded
            loading = false
        }
    }
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Panel) {
        when {
            loading -> Box(Modifier.fillMaxWidth().height(220.dp).navigationBarsPadding(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Purple)
            }
            user == null -> Box(Modifier.fillMaxWidth().padding(32.dp).navigationBarsPadding(), contentAlignment = Alignment.Center) {
                Text("No se pudo cargar el perfil.", color = Muted)
            }
            else -> ProfileSheetContent(user = user!!, title = "@${user!!.nick}")
        }
    }
}

@Composable
private fun ProfileSheetContent(user: ApiUser, title: String) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        item { Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp) }

            item { RankBadgeCard(user) }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatPillCard("${user.points ?: 0}", "Puntos", Blue, Modifier.weight(1f))
                    StatPillCard("${user.wins ?: 0}", "Victorias", Color(0xFF4CAF50), Modifier.weight(1f))
                    StatPillCard("${user.draws ?: 0}", "Empates", Gold, Modifier.weight(1f))
                    StatPillCard("${user.losses ?: 0}", "Derrotas", Color(0xCCF44336), Modifier.weight(1f))
                }
            }

            if (user.followers != null || user.following != null) {
                item {
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Panel).border(1.dp, Border, RoundedCornerShape(10.dp)),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        ProfileStatPill("${user.followers ?: 0}", "Seguidores", Purple, Modifier.weight(1f))
                        Box(Modifier.width(1.dp).height(44.dp).background(Border))
                        ProfileStatPill("${user.following ?: 0}", "Siguiendo", Blue, Modifier.weight(1f))
                        user.posts?.let { posts ->
                            Box(Modifier.width(1.dp).height(44.dp).background(Border))
                            ProfileStatPill("$posts", "Posts", Muted, Modifier.weight(1f))
                        }
                    }
                }
            }

            val balance = user.chequetiendaBalance
            if (balance != null && balance > 0) {
                item {
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Panel).border(1.dp, Border, RoundedCornerShape(10.dp)).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, null, tint = Color.White, modifier = Modifier.padding(end = 8.dp).size(20.dp))
                        Text("Cheque Tienda", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Text("%.2f€".format(balance), color = Color(0xFF4CAF50), fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                }
            }

            val tier = user.socioTier
            if (!tier.isNullOrBlank()) {
                item {
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Panel).border(1.dp, Gold.copy(alpha = 0.4f), RoundedCornerShape(10.dp)).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Star, null, tint = Gold, modifier = Modifier.padding(end = 8.dp).size(20.dp))
                        Text("Socio ${tier.replaceFirstChar { it.uppercase() }}", color = Gold, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        user.socioStatus?.let { status ->
                            Text(
                                status.replaceFirstChar { it.uppercase() },
                                color = Gold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.clip(RoundedCornerShape(50)).background(Gold.copy(alpha = 0.15f)).border(1.dp, Gold.copy(alpha = 0.45f), RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }

            val medals = user.medals
            if (!medals.isNullOrEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Panel).border(1.dp, Border, RoundedCornerShape(10.dp)).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.EmojiEvents, null, tint = Gold, modifier = Modifier.size(18.dp))
                            Text("Medallas", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            items(medals) { medal -> MedalItem(medal) }
                        }
                    }
                }
            }

            item {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Panel).border(1.dp, Border, RoundedCornerShape(10.dp)).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Info, null, tint = Blue, modifier = Modifier.size(18.dp))
                        Text("Información", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    user.favoriteFormat?.let { fmt -> if (fmt.isNotBlank()) ProfileInfoRow(Icons.Default.Style, "Formato favorito", fmt.replaceFirstChar { it.uppercase() }) }
                    user.location?.let { loc -> if (loc.isNotBlank()) ProfileInfoRow(Icons.Default.LocationOn, "Ubicación", loc) }
                    user.bio?.let { bio -> if (bio.isNotBlank()) ProfileInfoRow(Icons.Default.ChatBubble, "Bio", bio) }
                    ProfileInfoRow(Icons.Default.Email, "Email", user.email)
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
}

@Composable
private fun RankBadgeCard(user: ApiUser) {
    val points = user.points ?: 0
    val rank = playerRank(points)
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(rank.color.copy(alpha = 0.12f)).border(1.dp, rank.color.copy(alpha = 0.4f), RoundedCornerShape(10.dp)).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(rank.icon, null, tint = rank.color, modifier = Modifier.size(36.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(rank.name, color = rank.color, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text("$points puntos · ${rank.nextLabel}", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(CardColor)) {
                Box(Modifier.fillMaxWidth(rank.progress(points)).height(6.dp).clip(RoundedCornerShape(3.dp)).background(rank.color))
            }
        }
    }
}

@Composable
private fun StatPillCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.10f)).border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp)).padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Text(label, color = Muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

private data class PlayerRankData(
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val min: Int,
    val max: Int?,
) {
    val nextLabel: String get() = max?.let { "Siguiente en $it pts" } ?: "Rango máximo"
    fun progress(points: Int): Float {
        val maxPts = max ?: return 1f
        val range = (maxPts - min).toFloat()
        if (range <= 0f) return 1f
        return ((points - min).toFloat() / range).coerceIn(0f, 1f)
    }
}

private fun playerRank(points: Int): PlayerRankData = when {
    points >= 200 -> PlayerRankData("Leyenda Local", Icons.Default.EmojiEvents, Gold, 200, null)
    points >= 100 -> PlayerRankData("Planeswalker", Icons.Default.AutoAwesome, Purple, 100, 200)
    points >= 40  -> PlayerRankData("Duelista", Icons.Default.Shield, Blue, 40, 100)
    else          -> PlayerRankData("Aprendiz", Icons.Default.School, Muted, 0, 40)
}

// ─── USER EVENTS SHEET ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserEventsSheet(state: UiState, vm: MainViewModel, onDismiss: () -> Unit) {
    var filter by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) { vm.loadEvents() }

    val joinedEvents = state.events.filter { it.isJoined == true }
    val now = LocalDateTime.now()
    val filtered = when (filter) {
        1 -> joinedEvents.filter { (it.startDate ?: LocalDateTime.MIN) >= now }
        2 -> joinedEvents.filter { (it.startDate ?: LocalDateTime.MIN) < now }
        else -> joinedEvents
    }.sortedByDescending { it.startDate }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Panel) {
        Column(Modifier.fillMaxWidth().navigationBarsPadding()) {
            Text("Mis Eventos", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp, modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Todos", "Próximos", "Pasados").forEachIndexed { i, label ->
                    FilterChip(
                        selected = filter == i,
                        onClick = { filter = i },
                        label = { Text(label, fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Purple,
                            selectedLabelColor = Color.White,
                            containerColor = CardColor,
                            labelColor = Muted,
                        ),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            if (filtered.isEmpty()) {
                EmptyHint("No hay eventos en esta categoría.")
                Spacer(Modifier.height(48.dp))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(filtered) { event -> UserEventRow(event) }
                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
private fun UserEventRow(event: ApiEvent) {
    val isPast = (event.startDate ?: LocalDateTime.MAX) < LocalDateTime.now()
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(CardColor).border(1.dp, Border, RoundedCornerShape(10.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(42.dp)) {
            Text(event.dayOfMonth, color = Blue, fontWeight = FontWeight.Black, fontSize = 22.sp)
            Text(event.monthShort, color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(event.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (event.detailLine.isNotBlank()) Text(event.detailLine, color = Muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                val statusColor = if (isPast) Muted else Color(0xFF4CAF50)
                Text(
                    if (isPast) "Finalizado" else "Próximo",
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.clip(RoundedCornerShape(50)).background(statusColor.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 3.dp),
                )
                val price = event.price
                if (price != null && price > 0) {
                    Text("%.2f€".format(price), color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text("Gratis", color = Blue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─── USER RANKING SHEET ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserRankingSheet(user: ApiUser, state: UiState, vm: MainViewModel, onDismiss: () -> Unit) {
    LaunchedEffect(Unit) { vm.loadMatchHistory() }

    val ranking = state.sortedRanking
    val totalPlayers = ranking.size
    val rankPosition = ranking.indexOfFirst { it.id == user.id }.takeIf { it >= 0 }?.plus(1)

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Panel) {
        LazyColumn(
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { Text("Mi Ranking", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp) }

            item {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Panel).border(1.dp, Border, RoundedCornerShape(12.dp)).padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(rankPosition?.let { "#$it" } ?: "—", color = Gold, fontWeight = FontWeight.Black, fontSize = 28.sp)
                        Text("Posición", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(Modifier.width(1.dp).height(44.dp).background(Border))
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("$totalPlayers", color = Blue, fontWeight = FontWeight.Black, fontSize = 28.sp)
                        Text("Jugadores", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(Modifier.width(1.dp).height(44.dp).background(Border))
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${user.points ?: 0}", color = Purple, fontWeight = FontWeight.Black, fontSize = 28.sp)
                        Text("Puntos", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Panel).border(1.dp, Border, RoundedCornerShape(12.dp)).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.FilterList, null, tint = Blue, modifier = Modifier.size(18.dp))
                        Text("Desglose de puntos", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    val wins = user.wins ?: 0
                    val draws = user.draws ?: 0
                    PointsRow(Icons.Default.EmojiEvents, "Victorias ×20", wins * 20, Color(0xFF4CAF50))
                    PointsRow(Icons.Default.Remove, "Empates ×10", draws * 10, Gold)
                    PointsRow(Icons.Default.Close, "Derrotas ×0", 0, Color(0xCCF44336))
                    Box(Modifier.fillMaxWidth().height(1.dp).background(Border))
                    PointsRow(Icons.Default.AutoAwesome, "Total", user.points ?: 0, Blue)
                }
            }

            if (state.matchHistory.isNotEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Panel).border(1.dp, Border, RoundedCornerShape(12.dp)).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                            Icon(Icons.Default.History, null, tint = Blue, modifier = Modifier.size(18.dp))
                            Text("Historial de partidas", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                        state.matchHistory.take(20).forEach { match -> MatchHistoryRow(match) }
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun PointsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Text(label, color = Muted, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text("+$value pts", color = color, fontWeight = FontWeight.Black, fontSize = 14.sp)
    }
}

@Composable
private fun MatchHistoryRow(match: ApiMatchHistory) {
    val color = when (match.result.lowercase()) {
        "win" -> Color(0xFF4CAF50)
        "draw" -> Gold
        else -> Color(0xCCF44336)
    }
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            match.resultLabel,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.clip(RoundedCornerShape(50)).background(color.copy(alpha = 0.14f)).border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 4.dp),
        )
        Column(Modifier.weight(1f)) {
            Text(match.opponent ?: "Rival desconocido", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            match.note?.let { note -> if (note.isNotBlank()) Text(note, color = Muted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        }
        Text(match.createdAt.take(10).isoDateLabel(), color = Muted, fontSize = 11.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartDialog(cart: CartStore, vm: MainViewModel, onDismiss: () -> Unit) {
    val items by cart.items.collectAsState()
    var showCheckout by remember { mutableStateOf(false) }
    var checkoutResult by remember { mutableStateOf<ApiCheckoutResponse?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF070711)),
        ) {
            Column(Modifier.fillMaxSize().statusBarsPadding()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BadgedBox(badge = { if (items.isNotEmpty()) Badge { Text("${items.sumOf { it.quantity }}") } }) {
                        Icon(Icons.Default.ShoppingCart, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Text("Carrito", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                    if (items.isNotEmpty()) {
                        TextButton(onClick = { showClearConfirm = true }) {
                            Text("Vaciar", color = Color(0xFFFF8A80), fontWeight = FontWeight.Bold)
                        }
                    }
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF211B3F), contentColor = Purple),
                    ) { Text("Cerrar", fontSize = 16.sp) }
                }

                if (checkoutResult != null) {
                    val result = checkoutResult!!
                    LazyColumn(
                        Modifier.weight(1f),
                        contentPadding = PaddingValues(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        item {
                            Column(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF0D1A0A)).border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                Icon(Icons.Default.Verified, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(52.dp))
                                Text("¡Pedido confirmado!", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                                result.orderCode?.let { code ->
                                    Text("Código: $code", color = Gold, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                }
                                result.magicPhrase?.let { phrase ->
                                    Column(
                                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF241C40)).padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Text("Tu frase mágica", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("\"$phrase\"", color = Purple, fontSize = 16.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                                    }
                                }
                                result.total?.let { total ->
                                    Text("Total: %.2f€".format(total), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    "Nos pondremos en contacto contigo para coordinar la recogida.",
                                    color = Muted, fontSize = 14.sp, textAlign = TextAlign.Center, lineHeight = 20.sp,
                                )
                                Button(
                                    onClick = onDismiss,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(30.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                                ) { Text("Cerrar", fontWeight = FontWeight.Black, fontSize = 16.sp) }
                            }
                        }
                    }
                } else if (items.isEmpty()) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.ShoppingCart, null, tint = Muted, modifier = Modifier.size(64.dp))
                            Text("Tu carrito está vacío", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            Text("Añade productos desde la tienda.", color = Muted, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    LazyColumn(
                        Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(items, key = { it.id }) { item ->
                            Row(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                    .background(Panel).padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                ProductImage(item.product.image, Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)))
                                Column(Modifier.weight(1f)) {
                                    Text(item.product.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Text("%.2f€/ud".format(item.product.price), color = Muted, fontSize = 12.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(30.dp).clip(CircleShape).background(Color(0xFF211B3F)).clickable { cart.updateQuantity(item.id, item.quantity - 1) }, contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Remove, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                    Text("${item.quantity}", color = Color.White, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 10.dp))
                                    Box(Modifier.size(30.dp).clip(CircleShape).background(Color(0xFF211B3F)).clickable { cart.updateQuantity(item.id, item.quantity + 1) }, contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Box(Modifier.size(30.dp).clip(CircleShape).background(Color(0xFF3A1B24)).clickable { cart.remove(item.id) }, contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Close, null, tint = Color(0xFFFF8A80), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                        item {
                            Row(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF211B3F)).padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Subtotal", color = Muted, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(cart.subtotalFormatted, color = Gold, fontSize = 20.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                    Box(
                        Modifier.fillMaxWidth().background(Color(0xF5070711))
                            .navigationBarsPadding().padding(horizontal = 18.dp, vertical = 14.dp),
                    ) {
                        Button(
                            onClick = { showCheckout = true },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(34.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple),
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Realizar pedido · ${cart.subtotalFormatted}", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    if (showCheckout) {
        PaymentFormSheet(
            title = "Confirmar pedido",
            subtitle = "Revisa tu pedido y elige cómo pagar.",
            summaryTitle = "${items.sumOf { it.quantity }} producto(s)",
            summarySubtitle = "${items.size} artículo(s) en tu carrito",
            subtotal = cart.subtotal,
            chequetiendaBalance = vm.uiState.value.currentUser?.chequetiendaBalance ?: 0.0,
            onDismiss = { showCheckout = false },
            onConfirm = { method, phone, email, note, discount, useCheque ->
                vm.checkoutCart(cart, method, phone, email, note, discount, useCheque) { result, error ->
                    if (error != null) {
                        // error handled inside PaymentFormSheet via callback
                    } else {
                        checkoutResult = result
                    }
                    showCheckout = false
                }
            },
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("¿Vaciar el carrito?") },
            text = { Text("Se eliminarán todos los productos.", color = Muted) },
            confirmButton = {
                Button(
                    onClick = { cart.clear(); showClearConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B1A2F)),
                ) { Text("Vaciar") }
            },
            dismissButton = { TextButton({ showClearConfirm = false }) { Text("Cancelar") } },
            containerColor = Panel,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SellDialog(vm: MainViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var mode by remember { mutableStateOf("1 carta") }
    var cardName by remember { mutableStateOf("") }
    var collection by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("Near Mint") }
    var bulkList by remember { mutableStateOf("") }
    var photoDataUrl by remember { mutableStateOf<String?>(null) }
    var cameraBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var preferredContact by remember { mutableStateOf("telefono") }
    var date by remember { mutableStateOf(nextCardSaleDate().toString()) }
    var availability by remember { mutableStateOf<ApiCardSaleAvailability?>(null) }
    var selectedSlot by remember { mutableStateOf<Int?>(null) }
    var result by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }

    val libraryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            photoUri = uri
            cameraBitmap = null
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            val bitmap = bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            photoDataUrl = bitmap?.toJpegDataUrl()
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            cameraBitmap = bitmap
            photoUri = null
            photoDataUrl = bitmap.toJpegDataUrl()
        }
    }

    LaunchedEffect(date) {
        selectedSlot = null
        vm.fetchCardSaleAvailability(date) { data ->
            availability = data
            selectedSlot = data?.slots?.firstOrNull { it.available }?.slotIndex
        }
    }
    LaunchedEffect(Unit) {
        sheetState.partialExpand()
    }

    val slots = availability?.slots ?: fallbackCardSaleSlots()
    val contactValue = if (preferredContact == "email") email else phone
    val canSend = !sending &&
        selectedSlot != null &&
        contactValue.isNotBlank() &&
        if (mode == "1 carta") cardName.isNotBlank() && collection.isNotBlank() else bulkList.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = Color(0xFF090913),
        dragHandle = {
            Box(
                Modifier
                    .padding(top = 10.dp)
                    .size(width = 56.dp, height = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF777684)),
            )
        },
    ) {
        Box(Modifier.fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp),
                contentPadding = PaddingValues(bottom = 112.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.weight(1f))
                        Text("Vender cartas", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF242234), contentColor = Color.White),
                        ) { Text("Cerrar", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    }
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Default.Euro, null, tint = Color.White, modifier = Modifier.size(24.dp))
                        Text("Vender Cartas", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    }
                    Text(
                        "Envía tus cartas y recibe una valoración para tienda o efectivo.",
                        color = Color(0xFFB8B1CC),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                item {
                    SellSection {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Inventory2, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text("¿Cuántas cartas quieres vender?", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        }
                        SegmentedControl(listOf("1 carta", "Varias cartas"), mode) { mode = it }
                    }
                }
                if (mode == "1 carta") {
                    item {
                        SellSection {
                            SellSectionTitle("Datos de la carta", Icons.Default.ShoppingCart)
                            SellTextField(cardName, { cardName = it }, "Nombre de la carta")
                            SellTextField(collection, { collection = it }, "Colección (ej: Tarkir: Dragonstorm)")
                            Text("Estado", color = Muted, fontWeight = FontWeight.Black)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Mint", "Near Mint", "Good", "Played", "Damaged").forEach { option ->
                                    FilterChipPill(option, condition == option) { condition = option }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                SellOutlineButton("Biblioteca", Icons.Default.Inventory2, Modifier.weight(1f)) { libraryLauncher.launch("image/*") }
                                SellOutlineButton("Cámara", Icons.Default.CameraAlt, Modifier.weight(1f)) { cameraLauncher.launch(null) }
                            }
                            if (photoUri != null || cameraBitmap != null) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(170.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF080812)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    cameraBitmap?.let {
                                        Image(it.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                    } ?: AsyncImage(photoUri, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                }
                            }
                        }
                    }
                } else {
                    item {
                        SellSection {
                            SellSectionTitle("Lista de cartas", Icons.Default.FilterList)
                            Text("Una carta por línea con el formato:", color = Muted, fontSize = 16.sp)
                            Text("Nombre de carta - Colección", color = Purple, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            OutlinedTextField(
                                bulkList,
                                { bulkList = it },
                                placeholder = { Text("Mox Jasper - Tarkir: Dragonstorm\nLightning Bolt - Alpha\nBlack Lotus - Limited Edition...") },
                                minLines = 8,
                                modifier = Modifier.fillMaxWidth(),
                                colors = loginFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                            )
                        }
                    }
                }
                item {
                    SellSection {
                        SellSectionTitle("Contacto y cita", Icons.Default.CalendarMonth)
                        SegmentedControl(listOf("Teléfono", "Email"), if (preferredContact == "email") "Email" else "Teléfono") {
                            preferredContact = if (it == "Email") "email" else "telefono"
                        }
                        if (preferredContact == "email") {
                            SellTextField(email, { email = it }, "Email de contacto", KeyboardType.Email)
                        } else {
                            SellTextField(phone, { phone = it }, "Teléfono de contacto", KeyboardType.Phone)
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Día de cita", color = Color.White, fontSize = 15.sp, modifier = Modifier.weight(1f))
                            Text(
                                date.isoDateLabel(),
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color(0xFF302E3F))
                                    .padding(horizontal = 16.dp, vertical = 9.dp),
                            )
                        }
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            slots.forEach { slot ->
                                val selected = selectedSlot == slot.slotIndex
                                Surface(
                                    onClick = { if (slot.available) selectedSlot = slot.slotIndex },
                                    enabled = slot.available,
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) Purple.copy(alpha = 0.35f) else Color(0xFF11101B),
                                    border = androidx.compose.foundation.BorderStroke(1.2.dp, if (selected) Purple else DeepPurple.copy(alpha = 0.75f)),
                                    modifier = Modifier.width(148.dp),
                                ) {
                                    Column(Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${slot.startTime.take(5)} - ${slot.endTime.take(5)}", color = Muted, fontWeight = FontWeight.Black)
                                        Text(if (slot.available) "Disponible" else "Ocupado", color = Muted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                result?.let {
                    item { Text(it, color = if (it.startsWith("Solicitud")) Gold else Color(0xFFFF8A80), fontWeight = FontWeight.Bold) }
                }
            }
            Button(
                onClick = {
                    sending = true
                    result = null
                    vm.submitSellRequest(
                        cardName = cardName.takeIf { mode == "1 carta" },
                        collection = collection.takeIf { mode == "1 carta" },
                        condition = condition.takeIf { mode == "1 carta" },
                        bulkList = bulkList.takeIf { mode != "1 carta" },
                        contactPhone = phone.takeIf { preferredContact != "email" },
                        contactEmail = email.takeIf { preferredContact == "email" },
                        preferredContact = preferredContact,
                        appointmentDate = date,
                        appointmentSlotIndex = selectedSlot ?: 0,
                        photoDataUrl = photoDataUrl.takeIf { mode == "1 carta" },
                    ) { error ->
                        sending = false
                        result = error ?: "Solicitud enviada correctamente."
                    }
                },
                enabled = canSend,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp, vertical = 22.dp)
                    .height(62.dp),
                shape = RoundedCornerShape(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
            ) {
                Icon(Icons.Default.Send, null)
                Spacer(Modifier.width(10.dp))
                Text(if (sending) "Enviando..." else "Enviar solicitud", fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun SellSection(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF151329))
            .border(1.2.dp, DeepPurple.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = content,
    )
}

@Composable
private fun SellSectionTitle(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(16.dp))
        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun SellTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value,
        onValueChange,
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        colors = loginFieldColors(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
    )
}

@Composable
private fun SegmentedControl(options: List<String>, selected: String, onSelected: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF302E3F))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        options.forEach { option ->
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (selected == option) Color(0xFF777684) else Color.Transparent)
                    .clickable { onSelected(option) },
                contentAlignment = Alignment.Center,
            ) {
                Text(option, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun SellOutlineButton(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, DeepPurple.copy(alpha = 0.9f)),
    ) {
        Icon(icon, null)
        Spacer(Modifier.width(10.dp))
        Text(label, fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}

private fun Bitmap.toJpegDataUrl(): String {
    val output = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 82, output)
    return "data:image/jpeg;base64,${Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)}"
}

private fun nextCardSaleDate(from: LocalDate = LocalDate.now()): LocalDate {
    var cursor = from.plusDays(1)
    while (cursor.dayOfWeek !in setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)) {
        cursor = cursor.plusDays(1)
    }
    return cursor
}

private fun fallbackCardSaleSlots(): List<ApiCardSaleSlot> = listOf(
    ApiCardSaleSlot(0, "17:30", "18:00", true),
    ApiCardSaleSlot(1, "18:00", "18:30", true),
    ApiCardSaleSlot(2, "18:30", "19:00", true),
    ApiCardSaleSlot(3, "19:00", "19:30", true),
    ApiCardSaleSlot(4, "19:30", "20:00", true),
)

@Composable
private fun FilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(30.dp),
        color = if (selected) Blue.copy(alpha = 0.22f) else CardColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) Blue else Border),
    ) {
        Text(label, color = if (selected) Color.White else Muted, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
    }
}

@Composable
private fun SectionPanel(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
            content()
        }
    }
}

@Composable
private fun PageTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
        Text(subtitle, color = Muted, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    listOf(CardColor, Active.copy(alpha = 0.42f)),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY),
                ),
            )
            .border(1.dp, Border.copy(alpha = 0.45f), RoundedCornerShape(8.dp)),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.18f)),
        )
        Column(
            Modifier.padding(vertical = 14.dp, horizontal = 8.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, color = Purple, fontWeight = FontWeight.Black, fontSize = 34.sp)
            Spacer(Modifier.height(4.dp))
            Text(label.uppercase(), color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, maxLines = 1)
        }
    }
}

@Composable
private fun EmptyHint(text: String) {
    Box(Modifier.fillMaxWidth().padding(14.dp), contentAlignment = Alignment.Center) {
        Text(text, color = Muted)
    }
}

@Composable
private fun ErrorBanner(message: String, onRetry: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF3A1B24))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(message, color = Color(0xFFFFC0CB), modifier = Modifier.weight(1f))
        TextButton(onRetry) { Text("Reintentar") }
    }
}

@Composable
private fun Avatar(url: String?, colorHex: String?, initial: String, size: androidx.compose.ui.unit.Dp) {
    val background = colorHex?.toColorOrNull() ?: Purple
    if (!url.isNullOrBlank()) {
        AsyncImage(url, null, Modifier.size(size).clip(CircleShape).background(background), contentScale = ContentScale.Crop)
    } else {
        Box(Modifier.size(size).clip(CircleShape).background(background), contentAlignment = Alignment.Center) {
            Text(initial.take(2), color = Color.White, fontWeight = FontWeight.Black)
        }
    }
}

private fun String.toColorOrNull(): Color? = runCatching {
    val clean = removePrefix("#")
    Color(android.graphics.Color.parseColor("#$clean"))
}.getOrNull()

// ─── PAYMENT FORM SHEET ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PaymentFormSheet(
    title: String,
    subtitle: String,
    summaryTitle: String,
    summarySubtitle: String,
    subtotal: Double,
    chequetiendaBalance: Double,
    onDismiss: () -> Unit,
    onConfirm: (method: String, phone: String, email: String, note: String, discount: String, useCheque: Boolean) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var method by remember { mutableStateOf("bizum") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var discountCode by remember { mutableStateOf("") }
    var useChequetienda by remember { mutableStateOf(false) }
    var sending by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val canConfirm = !sending && (phone.isNotBlank() || email.isNotBlank())

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xFF090913),
        dragHandle = {
            Box(Modifier.padding(top = 10.dp).size(width = 56.dp, height = 6.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFF777684)))
        },
    ) {
        LazyColumn(
            Modifier.fillMaxWidth().padding(horizontal = 22.dp),
            contentPadding = PaddingValues(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text(subtitle, color = Muted, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }
            item {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF15132A)).border(1.dp, Border.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(summaryTitle, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(summarySubtitle, color = Muted, fontSize = 12.sp)
                        }
                        Text("%.2f€".format(subtotal), color = Purple, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                    if (chequetiendaBalance > 0) {
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1A1530)).padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, null, tint = Gold, modifier = Modifier.size(18.dp))
                            Text("Usar ChequeTienda (%.2f€ disponibles)".format(chequetiendaBalance), color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            androidx.compose.material3.Switch(
                                checked = useChequetienda,
                                onCheckedChange = { useChequetienda = it },
                                colors = androidx.compose.material3.SwitchDefaults.colors(checkedThumbColor = Gold, checkedTrackColor = Gold.copy(alpha = 0.4f)),
                            )
                        }
                    }
                }
            }
            item {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF15132A)).border(1.dp, Border.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.AccountBalanceWallet, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Text("Método de pago", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Color(0xFF343240)),
                    ) {
                        listOf("bizum" to "Bizum", "cash" to "Efectivo").forEach { (value, label) ->
                            Box(
                                Modifier.weight(1f).clip(RoundedCornerShape(28.dp))
                                    .background(if (method == value) DeepPurple else Color.Transparent)
                                    .clickable { method = value }.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text(
                        if (method == "cash") "Pago en efectivo: nos pondremos en contacto para coordinar la recogida."
                        else "Pago por Bizum: rápido y sin comisiones. Te contactaremos para coordinar.",
                        color = Muted, fontSize = 12.sp, lineHeight = 18.sp,
                    )
                    OutlinedTextField(
                        discountCode, { discountCode = it.uppercase() },
                        label = { Text("Código de descuento (opcional)") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        colors = loginFieldColors(), shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(autoCorrect = false),
                    )
                    OutlinedTextField(
                        phone, { phone = it },
                        label = { Text("Teléfono (WhatsApp)") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        colors = loginFieldColors(), shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    )
                    OutlinedTextField(
                        email, { email = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        colors = loginFieldColors(), shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    )
                    OutlinedTextField(
                        note, { note = it },
                        label = { Text("Nota adicional (opcional)") },
                        modifier = Modifier.fillMaxWidth().height(88.dp),
                        colors = loginFieldColors(), shape = RoundedCornerShape(10.dp),
                        maxLines = 3,
                    )
                }
            }
            errorMessage?.let { err ->
                item { Text(err, color = Color(0xFFFF8A80), fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            }
            item {
                Button(
                    onClick = {
                        errorMessage = null
                        sending = true
                        onConfirm(method, phone, email, note, discountCode, useChequetienda)
                    },
                    enabled = canConfirm,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(34.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                ) {
                    if (sending) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    else {
                        Icon(Icons.Default.Verified, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Confirmar pedido", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// ─── NATIVE AD CARD ────────────────────────────────────────────────────────────

@Composable
private fun NativeAdCard(adUnitId: String) {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    DisposableEffect(adUnitId) {
        val loader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad -> nativeAd = ad }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) { nativeAd = null }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().setVideoOptions(VideoOptions.Builder().setStartMuted(true).build()).build())
            .build()
        loader.loadAd(AdRequest.Builder().build())
        onDispose { nativeAd?.destroy() }
    }

    nativeAd?.let { ad ->
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0D0B1C)).border(1.dp, Border.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        ) {
            AndroidView(
                factory = { ctx ->
                    val adView = NativeAdView(ctx)
                    val layout = FrameLayout(ctx).apply {
                        setPadding(32, 28, 32, 28)
                    }
                    val headline = TextView(ctx).apply {
                        setTextColor(android.graphics.Color.WHITE)
                        textSize = 15f
                        setTypeface(null, android.graphics.Typeface.BOLD)
                        text = ad.headline ?: "Publicidad"
                    }
                    val body = TextView(ctx).apply {
                        setTextColor(android.graphics.Color.parseColor("#B8BCE5"))
                        textSize = 12f
                        text = ad.body ?: ""
                    }
                    val adLabel = TextView(ctx).apply {
                        text = "Anuncio"
                        textSize = 10f
                        setTextColor(android.graphics.Color.parseColor("#9B6CFF"))
                        background = android.graphics.drawable.GradientDrawable().apply {
                            setColor(android.graphics.Color.parseColor("#1A1530"))
                            cornerRadius = 20f
                        }
                        setPadding(16, 4, 16, 4)
                    }
                    val innerLayout = android.widget.LinearLayout(ctx).apply {
                        orientation = android.widget.LinearLayout.VERTICAL
                        val p8 = (8 * ctx.resources.displayMetrics.density).toInt()
                        addView(adLabel)
                        addView(headline)
                        addView(body)
                        for (i in 0 until childCount) getChildAt(i).let {
                            (it.layoutParams as? android.widget.LinearLayout.LayoutParams)?.apply { topMargin = p8 }
                        }
                    }
                    layout.addView(innerLayout)
                    adView.headlineView = headline
                    adView.bodyView = body
                    adView.addView(layout)
                    adView.setNativeAd(ad)
                    adView
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ─── REWARDED AD CARD ──────────────────────────────────────────────────────────

@Composable
private fun RewardedAdCard(vm: MainViewModel) {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var rewardedAd by remember { mutableStateOf<RewardedAd?>(null) }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        RewardedAd.load(
            context,
            MonetizationConfig.REWARDED_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) { rewardedAd = ad }
                override fun onAdFailedToLoad(error: LoadAdError) { rewardedAd = null }
            },
        )
        onDispose { rewardedAd = null }
    }

    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1A1040), Color(0xFF0D0820), Color(0xFF1A1040))))
            .border(1.dp, Purple.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(18.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(38.dp).clip(CircleShape).background(Purple.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PlayCircle, null, tint = Purple, modifier = Modifier.size(22.dp))
                }
                Column {
                    Text("Ver anuncio para ganar puntos", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    Text("Mira un breve vídeo y suma puntos a tu ranking.", color = Muted, fontSize = 12.sp)
                }
            }
            resultMessage?.let { msg ->
                Text(msg, color = if (msg.startsWith("¡")) Gold else Color(0xFFFF8A80), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Button(
                onClick = {
                    val activity = context as? Activity ?: return@Button
                    val ad = rewardedAd
                    if (ad != null) {
                        loading = true
                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                loading = false
                                rewardedAd = null
                            }
                        }
                        ad.show(activity) {
                            vm.claimRewardedAdPoints { points, error ->
                                loading = false
                                resultMessage = if (error != null) error
                                else "¡+${points ?: 0} puntos ganados!"
                            }
                        }
                    } else {
                        resultMessage = "Anuncio no disponible. Inténtalo más tarde."
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepPurple),
            ) {
                if (loading) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                else {
                    Icon(Icons.Default.Star, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Ver anuncio", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

// ─── REMOVE ADS CARD ───────────────────────────────────────────────────────────

@Composable
private fun RemoveAdsCard(adsRemoved: Boolean, onAdsRemoved: () -> Unit) {
    val context = LocalContext.current
    var billingClient by remember { mutableStateOf<BillingClient?>(null) }
    var productDetails by remember { mutableStateOf<ProductDetails?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        // clientHolder permite referenciar el cliente dentro del listener
        // antes de que la variable 'client' esté completamente asignada
        var clientHolder: BillingClient? = null
        val client = BillingClient.newBuilder(context)
            .setListener { result, purchases ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    purchases.forEach { purchase ->
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                            purchase.products.contains(MonetizationConfig.REMOVE_ADS_PRODUCT_ID)
                        ) {
                            if (!purchase.isAcknowledged) {
                                val ackParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                                clientHolder?.acknowledgePurchase(ackParams) {}
                            }
                            onAdsRemoved()
                        }
                    }
                }
            }
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()
        clientHolder = client
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    val params = QueryProductDetailsParams.newBuilder()
                        .setProductList(listOf(
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(MonetizationConfig.REMOVE_ADS_PRODUCT_ID)
                                .setProductType(BillingClient.ProductType.INAPP)
                                .build(),
                        ))
                        .build()
                    client.queryProductDetailsAsync(params) { _, details ->
                        productDetails = details.firstOrNull()
                    }
                    client.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                    ) { _, purchases ->
                        if (purchases.any { it.products.contains(MonetizationConfig.REMOVE_ADS_PRODUCT_ID) && it.purchaseState == Purchase.PurchaseState.PURCHASED }) {
                            onAdsRemoved()
                        }
                    }
                }
            }
            override fun onBillingServiceDisconnected() {}
        })
        billingClient = client
        onDispose { client.endConnection() }
    }

    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(if (adsRemoved) Brush.linearGradient(listOf(Color(0xFF0D1A0A), Color(0xFF0D1A0A))) else Brush.linearGradient(listOf(Color(0xFF101030), Color(0xFF0A0A18))))
            .border(1.dp, if (adsRemoved) Color(0xFF4CAF50).copy(alpha = 0.5f) else Blue.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(18.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    Modifier.size(42.dp).clip(CircleShape)
                        .background(if (adsRemoved) Color(0xFF4CAF50).copy(alpha = 0.2f) else Blue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (adsRemoved) Icons.Default.Verified else Icons.Default.Shield,
                        null,
                        tint = if (adsRemoved) Color(0xFF4CAF50) else Blue,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        if (adsRemoved) "Anuncios eliminados" else "Quitar anuncios",
                        color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp,
                    )
                    Text(
                        if (adsRemoved) "Gracias por apoyar Última Tirada." else (productDetails?.oneTimePurchaseOfferDetails?.formattedPrice?.let { "Pago único de $it. Sin suscripción." } ?: "Pago único · Sin suscripción."),
                        color = Muted, fontSize = 12.sp,
                    )
                }
            }
            statusMessage?.let { msg -> Text(msg, color = Color(0xFFFF8A80), fontSize = 12.sp) }
            if (!adsRemoved) {
                Button(
                    onClick = {
                        val activity = context as? Activity ?: return@Button
                        val details = productDetails ?: return@Button
                        loading = true
                        val flowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(listOf(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(details)
                                    .build(),
                            ))
                            .build()
                        val result = billingClient?.launchBillingFlow(activity, flowParams)
                        if (result?.responseCode != BillingClient.BillingResponseCode.OK) {
                            loading = false
                            statusMessage = "No se pudo iniciar la compra."
                        }
                    },
                    enabled = !loading && productDetails != null,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue),
                ) {
                    if (loading) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    else {
                        Icon(Icons.Default.Shield, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Comprar sin anuncios", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
