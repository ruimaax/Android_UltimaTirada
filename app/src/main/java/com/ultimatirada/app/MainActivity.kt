package com.ultimatirada.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.ads.MobileAds
import com.ultimatirada.app.ui.auth.LoginScreen
import com.ultimatirada.app.ui.auth.SplashScreen
import com.ultimatirada.app.ui.cart.CartDialog
import com.ultimatirada.app.ui.community.CommunityScreen
import com.ultimatirada.app.ui.events.EventsScreen
import com.ultimatirada.app.ui.home.HomeScreen
import com.ultimatirada.app.ui.profile.ProfileMenuSheet
import com.ultimatirada.app.ui.profile.ProfileScreen
import com.ultimatirada.app.ui.profile.UserEventsSheet
import com.ultimatirada.app.ui.profile.UserProfileSheet
import com.ultimatirada.app.ui.profile.UserRankingSheet
import com.ultimatirada.app.ui.shared.AppBottomBar
import com.ultimatirada.app.ui.shared.AppHeader
import com.ultimatirada.app.ui.shared.AppTab
import com.ultimatirada.app.ui.shared.Background
import com.ultimatirada.app.ui.shared.Blue
import com.ultimatirada.app.ui.shared.BrandBackground
import com.ultimatirada.app.ui.shared.Gold
import com.ultimatirada.app.ui.shared.MonetizationConfig
import com.ultimatirada.app.ui.shared.Panel
import com.ultimatirada.app.ui.shared.Purple
import com.ultimatirada.app.ui.store.StoreScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
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
    val extraBoldStyle = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
    val labelStyle = TextStyle(letterSpacing = 0.08.em)
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Blue,
            secondary = Purple,
            tertiary = Gold,
            background = Background,
            surface = Panel,
            onSurface = Color.White,
        ),
        typography = Typography(
            displayLarge = extraBoldStyle,
            headlineLarge = extraBoldStyle,
            labelLarge = labelStyle,
            labelMedium = labelStyle,
            labelSmall = labelStyle,
        ),
        content = content,
    )
}

@Composable
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
