package com.ultimatirada.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ultimatirada.app.ApiEvent
import com.ultimatirada.app.ApiMatchHistory
import com.ultimatirada.app.ApiRankingEntry
import com.ultimatirada.app.ApiUser
import com.ultimatirada.app.ApiUserMedal
import com.ultimatirada.app.MainViewModel
import com.ultimatirada.app.UiState
import com.ultimatirada.app.isoDateLabel
import com.ultimatirada.app.ui.auth.LoginPanel
import com.ultimatirada.app.ui.shared.Avatar
import com.ultimatirada.app.ui.shared.Background
import com.ultimatirada.app.ui.shared.Blue
import com.ultimatirada.app.ui.shared.Border
import com.ultimatirada.app.ui.shared.CardColor
import com.ultimatirada.app.ui.shared.DeepPurple
import com.ultimatirada.app.ui.shared.EmptyHint
import com.ultimatirada.app.ui.shared.Gold
import com.ultimatirada.app.ui.shared.Muted
import com.ultimatirada.app.ui.shared.PageTitle
import com.ultimatirada.app.ui.shared.Panel
import com.ultimatirada.app.ui.shared.Purple
import com.ultimatirada.app.ui.shared.RemoveAdsCard
import com.ultimatirada.app.ui.shared.RewardedAdCard
import com.ultimatirada.app.ui.shared.toColorOrNull
import java.time.LocalDateTime

@Composable
internal fun ProfileScreen(state: UiState, vm: MainViewModel, adsRemoved: Boolean = false, onAdsRemoved: () -> Unit = {}) {
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
private fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
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
private fun ProfileActionButton(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
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
internal fun ProfileMenuSheet(
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
private fun ProfileMenuOption(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
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
internal fun UserProfileSheet(user: ApiUser, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Panel) {
        ProfileSheetContent(user = user, title = "Mi Perfil")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PublicProfileSheet(userId: Int, vm: MainViewModel, onDismiss: () -> Unit) {
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

// ─── USER EVENTS SHEET ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UserEventsSheet(state: UiState, vm: MainViewModel, onDismiss: () -> Unit) {
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
internal fun UserRankingSheet(user: ApiUser, state: UiState, vm: MainViewModel, onDismiss: () -> Unit) {
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
private fun PointsRow(icon: ImageVector, label: String, value: Int, color: Color) {
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
