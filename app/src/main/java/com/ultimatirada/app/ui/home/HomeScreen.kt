package com.ultimatirada.app.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.ultimatirada.app.ApiEvent
import com.ultimatirada.app.ApiProduct
import com.ultimatirada.app.ApiRankingEntry
import com.ultimatirada.app.ApiYouTubeVideo
import com.ultimatirada.app.MainViewModel
import com.ultimatirada.app.UiState
import com.ultimatirada.app.ui.events.EventDetailDialog
import com.ultimatirada.app.ui.payment.PaymentFormSheet
import com.ultimatirada.app.ui.profile.PublicProfileSheet
import com.ultimatirada.app.ui.shared.Active
import com.ultimatirada.app.ui.shared.AppTab
import com.ultimatirada.app.ui.shared.Avatar
import com.ultimatirada.app.ui.shared.Background
import com.ultimatirada.app.ui.shared.Blue
import com.ultimatirada.app.ui.shared.Border
import com.ultimatirada.app.ui.shared.CardColor
import com.ultimatirada.app.ui.shared.DeepPurple
import com.ultimatirada.app.ui.shared.EmptyHint
import com.ultimatirada.app.ui.shared.ErrorBanner
import com.ultimatirada.app.ui.shared.Gold
import com.ultimatirada.app.ui.shared.MonetizationConfig
import com.ultimatirada.app.ui.shared.Muted
import com.ultimatirada.app.ui.shared.NativeAdCard
import com.ultimatirada.app.ui.shared.Panel
import com.ultimatirada.app.ui.shared.ProductImage
import com.ultimatirada.app.ui.shared.Purple
import com.ultimatirada.app.ui.shared.StatCard
import com.ultimatirada.app.ui.shared.relativeLabel
import kotlinx.coroutines.delay

@Composable
internal fun HomeScreen(state: UiState, onTab: (AppTab) -> Unit, vm: MainViewModel, adsRemoved: Boolean = false) {
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
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(
                listOf(Color(0xFF1A0F3C), Color(0xFF0D0820)),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
            ))
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
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF60A5FA), Color(0xFFA855F7)),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 0f),
                ),
                fontFamily = FontFamily.Serif,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                shadow = Shadow(
                    color = Color(0xFF8B5CF6).copy(alpha = 0.6f),
                    blurRadius = 32f,
                ),
            ),
            maxLines = 1,
            softWrap = false,
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
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple),
        ) {
            Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Próximos Eventos", fontSize = 15.sp, fontWeight = FontWeight.Black)
        }
        Button(
            onClick = onStore,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D2E00), contentColor = Gold),
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
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    listOf(Panel, Active.copy(alpha = 0.22f), Panel),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                ),
            )
            .border(1.dp, Border, RoundedCornerShape(14.dp))
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

