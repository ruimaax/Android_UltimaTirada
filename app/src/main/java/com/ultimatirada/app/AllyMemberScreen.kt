@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.ultimatirada.app

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

private val AllyInk = Color(0xFF05050A)
private val AllyPanel = Color(0xFF111021)
private val AllyPurple = Color(0xFF8F52FF)
private val AllyIndigo = Color(0xFF25256B)
private val AllyCyan = Color(0xFF28C7F7)
private val AllyRed = Color(0xFFB92812)
private val AllyAmber = Color(0xFFE2A826)
private val AllyText = Color(0xFFEFEAFD)
private val AllyMuted = Color(0xFFB8B0CE)

@Composable
fun AllyMemberScreen(state: UiState, vm: MainViewModel) {
    val user = state.currentUser
    val allyState = state.ally
    val snapshot = remember(allyState.response, user) { findAllyMatchForUser(allyState.response, user) }
    val fallbackEvent = allyState.response?.events?.firstOrNull { it.isJoined != false }

    LaunchedEffect(user?.id) {
        if (user != null) {
            while (true) {
                vm.refreshAlly()
                delay(5_000)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AllyInk)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            AllyTitle(
                subtitle = when {
                    snapshot?.pairing != null -> "Partida lista para jugar"
                    fallbackEvent != null || snapshot != null -> "Esperando emparejamientos del organizador"
                    else -> "Tus eventos aparecerán aquí"
                },
            )
        }
        item {
            when {
                user == null -> AllyEmptyCard("Inicia sesión para ver Aliado.", Icons.Default.AutoAwesome)
                allyState.isLoading && allyState.response == null -> AllyWaitingCard("Sincronizando Aliado", "Buscando tus torneos activos...")
                allyState.errorMessage != null && allyState.response == null -> AllyErrorCard(allyState.errorMessage) { vm.refreshAlly() }
                snapshot?.isBye == true -> AllyByeView(snapshot)
                snapshot?.pairing != null -> AllyMatchView(snapshot)
                snapshot != null || fallbackEvent != null -> {
                    AllyWaitingCard(
                        title = snapshot?.eventTitle ?: fallbackEvent?.title ?: "Evento",
                        message = "Se está preparando el evento",
                    )
                }
                else -> AllyEmptyCard("No estás apuntado a ningún evento con Aliado activo.", Icons.Default.AutoAwesome)
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun AllyTitle(subtitle: String) {
    Column(Modifier.padding(top = 24.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(42.dp))
            Text("Aliado", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black, maxLines = 1)
        }
        Text(subtitle, color = AllyMuted, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AllyWaitingCard(title: String, message: String) {
    AllyGlassPanel {
        Icon(Icons.Default.HourglassTop, null, tint = AllyAmber, modifier = Modifier.size(58.dp))
        Text(title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        Text(message, color = AllyMuted, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        CircularProgressIndicator(color = AllyPurple, strokeWidth = 4.dp, modifier = Modifier.size(34.dp))
    }
}

@Composable
private fun AllyErrorCard(message: String, onRetry: () -> Unit) {
    AllyGlassPanel {
        Text("No se pudo sincronizar Aliado", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text(message, color = AllyMuted, fontSize = 16.sp, textAlign = TextAlign.Center)
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = AllyPurple, contentColor = Color.White),
            shape = RoundedCornerShape(28.dp),
        ) {
            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Reintentar", fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun AllyEmptyCard(message: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    AllyGlassPanel {
        Icon(icon, null, tint = AllyAmber, modifier = Modifier.size(64.dp))
        Text(message, color = AllyMuted, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
private fun AllyByeView(snapshot: AllyMatchSnapshot) {
    AllyGlassPanel {
        Icon(Icons.Default.SentimentSatisfied, null, tint = AllyAmber, modifier = Modifier.size(70.dp))
        Text(snapshot.eventTitle, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        Text("Descansas esta partida", color = AllyAmber, fontSize = 28.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        Text("El organizador avanzará la siguiente ronda cuando corresponda.", color = AllyMuted, fontSize = 16.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun AllyGlassPanel(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xF01A1730), Color(0xE90D0B18)),
                ),
            )
            .border(1.dp, AllyPurple.copy(alpha = 0.65f), RoundedCornerShape(18.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllyMatchView(snapshot: AllyMatchSnapshot) {
    val pairing = snapshot.pairing ?: return
    var mode by remember(snapshot.key) { mutableStateOf(AllyMode.OneVsOne) }
    var initialLives by remember(snapshot.key) { mutableStateOf(20) }
    var counters by remember(snapshot.key, mode, initialLives) {
        mutableStateOf(initialCountersFor(pairing, mode, initialLives))
    }
    var rolling by remember(snapshot.key) { mutableStateOf(false) }
    var startMessage by remember(snapshot.key) { mutableStateOf<String?>(null) }
    val rotation by animateFloatAsState(if (rolling) 360f else 0f, label = "diceLogo")

    LaunchedEffect(rolling) {
        if (rolling) {
            repeat(12) {
                counters = counters.map { it.copy(dice = Random.nextInt(1, 7), isWinner = false) }
                delay(95)
            }
            val result = resolveDiceRoll(counters.map { it.id })
            counters = counters.map { player ->
                player.copy(
                    dice = result.rolls[player.id] ?: player.dice,
                    isWinner = player.id == result.winnerId,
                )
            }
            startMessage = counters.firstOrNull { it.id == result.winnerId }?.let { "Empieza ${it.name}" }
            rolling = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AllyMatchHeader(snapshot, pairing, startMessage)
        AllyModeSelector(mode) { mode = it }
        AllyLifePresetSelector(initialLives) {
            initialLives = it
            counters = initialCountersFor(pairing, mode, it)
            startMessage = null
        }
        AllyArena(
            counters = counters,
            rolling = rolling,
            logoRotation = rotation,
            onLife = { id, delta ->
                counters = counters.map { if (it.id == id) it.copy(lives = it.lives + delta) else it }
            },
            onRoll = {
                if (!rolling) {
                    startMessage = null
                    rolling = true
                }
            },
        )
        Button(
            onClick = {
                counters = initialCountersFor(pairing, mode, initialLives)
                startMessage = null
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF201A38), contentColor = Color.White),
        ) {
            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Reiniciar vidas", fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun AllyMatchHeader(snapshot: AllyMatchSnapshot, pairing: AllyPairing, message: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF141226), Color(0xFF090910))))
            .border(1.dp, AllyPurple.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(snapshot.eventTitle, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text("Partida ${snapshot.game.number} / ${snapshot.game.total ?: snapshot.tournament.totalGames ?: 1}", color = AllyMuted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(pairing.teamA.joinToString(" + ") { it.displayName }.ifBlank { "Equipo A" }, color = AllyCyan, fontSize = 16.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("VS", color = AllyMuted, fontSize = 12.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp))
            Text(pairing.teamB.joinToString(" + ") { it.displayName }.ifBlank { "BYE" }, color = AllyAmber, fontSize = 16.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.End)
        }
        message?.let {
            Text(it, color = AllyAmber, fontSize = 19.sp, fontWeight = FontWeight.Black, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllyModeSelector(selected: AllyMode, onSelected: (AllyMode) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AllyMode.entries.forEach { mode ->
            FilterChip(
                selected = selected == mode,
                onClick = { onSelected(mode) },
                label = { Text(mode.label, fontWeight = FontWeight.Black) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AllyPurple,
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFF151326),
                    labelColor = AllyMuted,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected == mode,
                    borderColor = AllyPurple.copy(alpha = 0.45f),
                    selectedBorderColor = AllyPurple,
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllyLifePresetSelector(selected: Int, onSelected: (Int) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(20, 40, 60, 100).forEach { lives ->
            FilterChip(
                selected = selected == lives,
                onClick = { onSelected(lives) },
                label = { Text("$lives vidas", fontWeight = FontWeight.Black) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AllyAmber,
                    selectedLabelColor = Color(0xFF160F02),
                    containerColor = Color(0xFF151326),
                    labelColor = AllyMuted,
                ),
            )
        }
    }
}

@Composable
private fun AllyArena(
    counters: List<LifeCounterPlayer>,
    rolling: Boolean,
    logoRotation: Float,
    onLife: (String, Int) -> Unit,
    onRoll: () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black)
            .border(1.dp, AllyPurple.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(10.dp),
    ) {
        val compact = maxWidth < 380.dp
        val cardWidth = if (compact || counters.size > 4) (maxWidth - 32.dp) / 2 else (maxWidth - 28.dp) / 2
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                counters.forEach { player ->
                    AllyLifeCounterCard(
                        player = player,
                        rolling = rolling,
                        modifier = Modifier.width(cardWidth),
                        onPlus = { onLife(player.id, 1) },
                        onMinus = { onLife(player.id, -1) },
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(76.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(AllyAmber, Color(0xFF5C3700))))
                .border(2.dp, Color.Black.copy(alpha = 0.5f), CircleShape)
                .clickable(enabled = !rolling, onClick = onRoll),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.logosff),
                contentDescription = "Tirar dados",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(58.dp).rotate(logoRotation),
            )
        }
    }
}

@Composable
private fun AllyLifeCounterCard(
    player: LifeCounterPlayer,
    rolling: Boolean,
    modifier: Modifier,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
) {
    val color = if (player.team == 0) AllyCyan else AllyRed
    val accent = if (player.team == 0) AllyIndigo else AllyAmber
    val lives by animateIntAsState(player.lives, label = "lives")
    val defeatAlpha by animateFloatAsState(if (player.lives <= 0) 1f else 0f, label = "defeat")
    val borderColor by animateColorAsState(if (player.isWinner) AllyAmber else color.copy(alpha = 0.75f), label = "winner")

    Box(
        modifier = modifier
            .height(230.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    listOf(color.copy(alpha = 0.95f), if (player.team == 0) Color(0xFF19A9D9) else Color(0xFFD44A17)),
                ),
            )
            .border(BorderStroke(if (player.isWinner) 3.dp else 1.dp, borderColor), RoundedCornerShape(8.dp))
            .padding(10.dp),
    ) {
        Text(
            player.name,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.TopCenter),
        )
        IconButton(
            onClick = onPlus,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.16f)),
        ) {
            Icon(Icons.Default.Add, null, tint = Color.White)
        }
        IconButton(
            onClick = onMinus,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.16f)),
        ) {
            Icon(Icons.Default.Remove, null, tint = Color.White)
        }
        Text(
            "$lives",
            color = Color.White,
            fontSize = 66.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(Icons.Default.Casino, null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
            Text(
                if (rolling) "..." else player.dice.toString(),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.52f * defeatAlpha))
                .alpha(defeatAlpha),
            contentAlignment = Alignment.Center,
        ) {
            Text("DERROTA", color = accent, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
    }
}
