package com.ultimatirada.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import java.time.LocalDate

private val Background = Color(0xFF070713)
private val Panel = Color(0xFF111227)
private val CardColor = Color(0xFF171832)
private val Border = Color(0xFF2E3156)
private val Muted = Color(0xFFB8BCE5)
private val Blue = Color(0xFF4EA8FF)
private val Purple = Color(0xFF9B6CFF)
private val Gold = Color(0xFFFFC857)

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UltimaTiradaTheme {
                val cart = remember { CartStore(this) }
                UltimaTiradaApp(vm, cart)
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
    Community("Comunidad"),
    Profile("Perfil"),
}

@Composable
private fun UltimaTiradaApp(vm: MainViewModel, cart: CartStore) {
    val state by vm.uiState.collectAsState()
    val cartItems by cart.items.collectAsState()
    var splashDone by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(AppTab.Home) }
    var showCart by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1400)
        splashDone = true
    }
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn && !state.hasLoadedOnce) vm.loadAll()
    }
    LaunchedEffect(state.isCheckingAuth) {
        if (!state.isCheckingAuth && !state.hasLoadedOnce) vm.loadAll()
    }

    Surface(Modifier.fillMaxSize(), color = Background) {
        when {
            state.isCheckingAuth || !splashDone -> SplashScreen()
            else -> Scaffold(
                containerColor = Background,
                topBar = {
                    AppHeader(
                        user = state.currentUser,
                        cartCount = cartItems.sumOf { it.quantity },
                        onCartTap = { showCart = true },
                    )
                },
                bottomBar = {
                    AppBottomBar(selectedTab) { selectedTab = it }
                },
            ) { padding ->
                Box(Modifier.padding(padding)) {
                    when (selectedTab) {
                        AppTab.Home -> HomeScreen(state, onTab = { selectedTab = it }, vm = vm)
                        AppTab.Store -> StoreScreen(state.products, state.isLoading, cart, vm)
                        AppTab.Events -> EventsScreen(state, vm)
                        AppTab.Community -> CommunityScreen(state, vm)
                        AppTab.Profile -> ProfileScreen(state, vm)
                    }
                }
            }
        }
    }

    if (showCart) {
        CartDialog(cart = cart, onDismiss = { showCart = false })
    }
}

@Composable
private fun SplashScreen() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF090A1A), Color(0xFF15113A)))),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Image(
                painter = painterResource(R.drawable.logosff),
                contentDescription = null,
                modifier = Modifier.size(116.dp).clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Text("ÚLTIMA TIRADA", fontWeight = FontWeight.Black, fontSize = 32.sp, color = Color.White)
            CircularProgressIndicator(color = Blue, strokeWidth = 3.dp, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun LoginPanel(vm: MainViewModel) {
    var credential by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painterResource(R.drawable.logosff), null, Modifier.size(88.dp).clip(CircleShape), contentScale = ContentScale.Crop)
        Text("Última Tirada", color = Color.White, fontWeight = FontWeight.Black, fontSize = 30.sp)
        Text("Entra con tu cuenta de la comunidad", color = Muted)
        OutlinedTextField(credential, { credential = it }, label = { Text("Email o nick") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(password, { password = it }, label = { Text("Contraseña") }, singleLine = true, modifier = Modifier.fillMaxWidth())
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
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (loading) "Entrando..." else "Entrar")
        }
    }
}

@Composable
private fun AppHeader(user: ApiUser?, cartCount: Int, onCartTap: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Panel)
            .border(1.dp, Border)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(painterResource(R.drawable.logosff), null, Modifier.size(42.dp).clip(CircleShape), contentScale = ContentScale.Crop)
        Column(Modifier.weight(1f)) {
            Text("Última Tirada", color = Color.White, fontWeight = FontWeight.Black, fontSize = 19.sp)
            Text("Magic: The Gathering en Ceuta", color = Muted, fontSize = 12.sp, maxLines = 1)
        }
        IconButton(onClick = onCartTap) {
            BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = Muted)
            }
        }
        Avatar(user?.avatarUrl, user?.avatarColor, user?.initial ?: "UT", 38.dp)
    }
}

@Composable
private fun AppBottomBar(selected: AppTab, onSelected: (AppTab) -> Unit) {
    NavigationBar(containerColor = Panel) {
        AppTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selected == tab,
                onClick = { onSelected(tab) },
                icon = {
                    Icon(
                        imageVector = when (tab) {
                            AppTab.Home -> Icons.Default.Home
                            AppTab.Store -> Icons.Default.Storefront
                            AppTab.Events -> Icons.Default.CalendarMonth
                            AppTab.Community -> Icons.Default.Favorite
                            AppTab.Profile -> Icons.Default.Person
                        },
                        contentDescription = tab.label,
                    )
                },
                label = { Text(tab.label, fontSize = 10.sp) },
            )
        }
    }
}

@Composable
private fun HomeScreen(state: UiState, onTab: (AppTab) -> Unit, vm: MainViewModel) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            HeroCard(onEvents = { onTab(AppTab.Events) }, onStore = { onTab(AppTab.Store) })
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("${state.statsPlayers}", "Jugadores", Modifier.weight(1f))
                StatCard("${state.statsEvents}", "Eventos", Modifier.weight(1f))
                StatCard("${state.statsProducts}", "Productos", Modifier.weight(1f))
            }
        }
        state.errorMessage?.let { item { ErrorBanner(it) { vm.loadAll() } } }
        item {
            SectionPanel("Próximos eventos") {
                if (state.upcomingEvents.isEmpty()) EmptyHint("Sin eventos próximos por ahora.")
                state.upcomingEvents.take(2).forEach { EventRow(it, onJoin = { vm.joinEvent(it) {} }) }
            }
        }
        item {
            SectionPanel("Productos destacados") {
                ProductCarousel(state.featuredProducts)
            }
        }
        item {
            SectionPanel("Top jugadores") {
                state.topThree.forEachIndexed { index, player -> RankingRow(index + 1, player) }
            }
        }
    }
}

@Composable
private fun HeroCard(onEvents: () -> Unit, onStore: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF14173A), Color(0xFF251849))))
            .border(1.dp, Border, RoundedCornerShape(8.dp))
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("BIENVENIDO A", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
        Text("ÚLTIMA TIRADA", color = Color.White, fontWeight = FontWeight.Black, fontSize = 34.sp)
        Text(
            "Tu comunidad oficial de Magic: The Gathering en Ceuta.\nTorneos, eventos, ligas, ranking y tienda online.",
            color = Muted,
            lineHeight = 21.sp,
        )
        Button(onEvents, Modifier.fillMaxWidth()) { Text("Próximos eventos") }
        OutlinedButton(onStore, Modifier.fillMaxWidth()) { Text("Ir a la tienda") }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoreScreen(products: List<ApiProduct>, loading: Boolean, cart: CartStore, vm: MainViewModel) {
    var search by remember { mutableStateOf("") }
    var selectedBrand by remember { mutableStateOf<String?>(null) }
    var showSell by remember { mutableStateOf(false) }
    val filtered = products.filter { product ->
        (search.isBlank() || product.name.contains(search, true) || product.brand.orEmpty().contains(search, true)) &&
            (selectedBrand == null || product.brand == selectedBrand)
    }.sortedWith(compareByDescending<ApiProduct> { it.featured }.thenBy { it.name })
    val brands = products.mapNotNull { it.brand?.takeIf(String::isNotBlank) }.distinct().sorted()

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Buscar producto") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            Button({ showSell = true }) { Text("Vender") }
        }
        FlowRow(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = { selectedBrand = null }, label = { Text("Todos") })
            brands.take(12).forEach { brand -> AssistChip(onClick = { selectedBrand = brand }, label = { Text(brand) }) }
        }
        if (loading && products.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(158.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(filtered, key = { it.id }) { product ->
                    ProductCard(product) { cart.add(product) }
                }
            }
        }
    }

    if (showSell) SellDialog(vm = vm, onDismiss = { showSell = false })
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
private fun ProductCard(product: ApiProduct, onAdd: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(8.dp)) {
        ProductImage(product.image, Modifier.fillMaxWidth().aspectRatio(1.1f))
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(product.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(product.brand ?: product.collection ?: "Última Tirada", color = Muted, fontSize = 12.sp, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(product.priceFormatted, color = Gold, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                Button(onClick = onAdd, enabled = product.isInStock, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) {
                    Text(if (product.isInStock) "Añadir" else "Sin stock", fontSize = 12.sp)
                }
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
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { PageTitle("Eventos", "Calendario, torneos y quedadas de la comunidad") }
        if (state.upcomingEvents.isEmpty()) item { EmptyHint("Sin eventos próximos por ahora.") }
        items(state.upcomingEvents, key = { it.id }) { event ->
            EventRow(event, onJoin = { vm.joinEvent(event) {} })
        }
    }
}

@Composable
private fun EventRow(event: ApiEvent, onJoin: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(8.dp)) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier
                    .width(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF22244A))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(event.dayOfMonth, color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text(event.monthShort, color = Gold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(event.title, color = Color.White, fontWeight = FontWeight.Bold)
                Text("${event.time.take(5)} · ${event.detailLine}", color = Muted, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("${event.currentPlayers ?: 0}/${event.maxPlayers ?: 0} jugadores", color = Muted, fontSize = 12.sp)
            }
            Button(onJoin, enabled = event.isJoined != true) { Text(if (event.isJoined == true) "Apuntado" else "Unirme") }
        }
    }
}

@Composable
private fun CommunityScreen(state: UiState, vm: MainViewModel) {
    var postText by remember { mutableStateOf("") }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { PageTitle("Comunidad", "Publicaciones, logros y conversación") }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(8.dp)) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(postText, { postText = it }, label = { Text("¿Qué estás preparando?") }, modifier = Modifier.fillMaxWidth())
                    Button(
                        onClick = { vm.publishPost(postText) { if (it == null) postText = "" } },
                        enabled = postText.isNotBlank(),
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Icon(Icons.Default.Send, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Publicar")
                    }
                }
            }
        }
        if (state.communityPosts.isEmpty()) item { EmptyHint("Aún no hay publicaciones.") }
        items(state.communityPosts, key = { it.id }) { post ->
            CommunityPostCard(post) { vm.toggleLike(post) }
        }
    }
}

@Composable
private fun CommunityPostCard(post: ApiCommunityPost, onLike: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(8.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Avatar(post.avatarUrl, post.avatarColor, post.initials, 38.dp)
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(post.userName, color = Color.White, fontWeight = FontWeight.Bold)
                        if (post.userRole == "admin") {
                            Spacer(Modifier.width(5.dp))
                            Icon(Icons.Default.Verified, null, tint = Blue, modifier = Modifier.size(16.dp))
                        }
                    }
                    Text("@${post.userNick} · ${post.createdAt.isoDateLabel()}", color = Muted, fontSize = 12.sp)
                }
            }
            Text(post.content, color = Color.White)
            post.imageUrl?.takeIf { it.isNotBlank() }?.let {
                AsyncImage(it, null, Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onLike) {
                    Icon(Icons.Default.Favorite, null, tint = if (post.likedByMe) Color(0xFFFF6B9C) else Muted)
                    Spacer(Modifier.width(4.dp))
                    Text("${post.likes}")
                }
                Text("${post.commentsCount} comentarios", color = Muted, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ProfileScreen(state: UiState, vm: MainViewModel) {
    val user = state.currentUser
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { PageTitle("Perfil", "Tu cuenta y ranking") }
        if (user != null) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(8.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Avatar(user.avatarUrl, user.avatarColor, user.initials, 58.dp)
                            Column(Modifier.weight(1f)) {
                                Text(user.name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                                Text("@${user.nick}", color = Muted)
                            }
                            OutlinedButton({ vm.logout() }) { Text("Salir") }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatCard("${user.points ?: 0}", "Puntos", Modifier.weight(1f))
                            StatCard("${user.wins ?: 0}", "Victorias", Modifier.weight(1f))
                            StatCard("${user.losses ?: 0}", "Derrotas", Modifier.weight(1f))
                        }
                        user.bio?.let { Text(it, color = Muted) }
                    }
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
            item {
                LoginPanel(vm)
            }
        }
        item {
            SectionPanel("Ranking") {
                state.sortedRanking.take(20).forEachIndexed { index, player -> RankingRow(index + 1, player) }
            }
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

@Composable
private fun CartDialog(cart: CartStore, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val items by cart.items.collectAsState()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Carrito", modifier = Modifier.weight(1f))
                IconButton(onDismiss) { Icon(Icons.Default.Close, null) }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (items.isEmpty()) EmptyHint("Tu carrito está vacío.")
                items.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(item.product.name, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("x${item.quantity} · %.2f€".format(item.lineTotal), color = Muted)
                        }
                        TextButton({ cart.updateQuantity(item.product.id, item.quantity - 1) }) { Text("-") }
                        TextButton({ cart.updateQuantity(item.product.id, item.quantity + 1) }) { Text("+") }
                    }
                }
                Text("TOTAL: ${cart.subtotalFormatted}", color = Gold, fontWeight = FontWeight.Black)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    context.startActivity(cart.emailIntent())
                    onDismiss()
                },
                enabled = items.isNotEmpty(),
            ) { Text("Reservar por email") }
        },
        dismissButton = { TextButton(onDismiss) { Text("Cerrar") } },
        containerColor = Panel,
    )
}

@Composable
private fun SellDialog(vm: MainViewModel, onDismiss: () -> Unit) {
    var mode by remember { mutableStateOf("1 carta") }
    var cardName by remember { mutableStateOf("") }
    var collection by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("Near Mint") }
    var bulkList by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var preferredContact by remember { mutableStateOf("whatsapp") }
    var date by remember { mutableStateOf(LocalDate.now().plusDays(1).toString()) }
    var result by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Vender cartas") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.height(460.dp)) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterPill("1 carta", selected = mode == "1 carta") { mode = "1 carta" }
                        FilterPill("Varias cartas", selected = mode == "Varias cartas") { mode = "Varias cartas" }
                    }
                }
                if (mode == "1 carta") {
                    item { OutlinedTextField(cardName, { cardName = it }, label = { Text("Nombre de la carta") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(collection, { collection = it }, label = { Text("Colección") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(condition, { condition = it }, label = { Text("Estado") }, modifier = Modifier.fillMaxWidth()) }
                } else {
                    item {
                        OutlinedTextField(
                            bulkList,
                            { bulkList = it },
                            label = { Text("Una carta por línea") },
                            placeholder = { Text("Black Lotus - Alpha") },
                            minLines = 5,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                item { OutlinedTextField(phone, { phone = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(preferredContact, { preferredContact = it }, label = { Text("Contacto preferido") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(date, { date = it }, label = { Text("Fecha de cita") }, modifier = Modifier.fillMaxWidth()) }
                result?.let { item { Text(it, color = if (it.startsWith("Solicitud")) Gold else Color(0xFFFF8A80)) } }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    sending = true
                    result = null
                    vm.submitSellRequest(
                        cardName = cardName.takeIf { mode == "1 carta" },
                        collection = collection.takeIf { mode == "1 carta" },
                        condition = condition.takeIf { mode == "1 carta" },
                        bulkList = bulkList.takeIf { mode != "1 carta" },
                        contactPhone = phone,
                        contactEmail = email,
                        preferredContact = preferredContact,
                        appointmentDate = date,
                        appointmentSlotIndex = 0,
                    ) { error ->
                        sending = false
                        result = error ?: "Solicitud enviada correctamente."
                    }
                },
                enabled = !sending && (cardName.isNotBlank() || bulkList.isNotBlank()) && (phone.isNotBlank() || email.isNotBlank()),
            ) { Text(if (sending) "Enviando..." else "Enviar") }
        },
        dismissButton = { TextButton(onDismiss) { Text("Cerrar") } },
        containerColor = Panel,
    )
}

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
private fun SectionPanel(title: String, content: @Composable Column.() -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
            content()
        }
    }
}

@Composable
private fun PageTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
        Text(subtitle, color = Muted)
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CardColor)
            .border(1.dp, Border, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 21.sp)
        Text(label, color = Muted, fontSize = 12.sp, maxLines = 1)
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
