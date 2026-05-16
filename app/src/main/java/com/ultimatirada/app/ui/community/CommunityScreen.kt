package com.ultimatirada.app.ui.community

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ultimatirada.app.ApiCommunityPost
import com.ultimatirada.app.ApiRankingEntry
import com.ultimatirada.app.CommunityFeedType
import com.ultimatirada.app.MainViewModel
import com.ultimatirada.app.UiState
import com.ultimatirada.app.ui.profile.PublicProfileSheet
import com.ultimatirada.app.ui.shared.Avatar
import com.ultimatirada.app.ui.shared.Blue
import com.ultimatirada.app.ui.shared.DeepPurple
import com.ultimatirada.app.ui.shared.EmptyHint
import com.ultimatirada.app.ui.shared.Gold
import com.ultimatirada.app.ui.shared.MonetizationConfig
import com.ultimatirada.app.ui.shared.Muted
import com.ultimatirada.app.ui.shared.NativeAdCard
import com.ultimatirada.app.ui.shared.Purple
import com.ultimatirada.app.ui.shared.RewardedAdCard
import com.ultimatirada.app.ui.shared.SegmentedControl
import com.ultimatirada.app.ui.shared.loginFieldColors
import com.ultimatirada.app.ui.shared.relativeLabel

@Composable
internal fun CommunityScreen(state: UiState, vm: MainViewModel, adsRemoved: Boolean = false) {
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
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
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
