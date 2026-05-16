package com.ultimatirada.app.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.ultimatirada.app.ApiEvent
import com.ultimatirada.app.MainViewModel
import com.ultimatirada.app.UiState
import com.ultimatirada.app.isoDateLabel
import com.ultimatirada.app.ui.payment.PaymentFormSheet
import com.ultimatirada.app.ui.profile.PublicProfileSheet
import com.ultimatirada.app.ui.shared.Avatar
import com.ultimatirada.app.ui.shared.Blue
import com.ultimatirada.app.ui.shared.Border
import com.ultimatirada.app.ui.shared.DeepPurple
import com.ultimatirada.app.ui.shared.EmptyHint
import com.ultimatirada.app.ui.shared.Gold
import com.ultimatirada.app.ui.shared.Muted
import com.ultimatirada.app.ui.shared.Purple
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

@Composable
internal fun EventsScreen(state: UiState, vm: MainViewModel) {
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

internal fun ApiEvent.eventColor(): Color {
    val typeText = listOfNotNull(type, format, title).joinToString(" ").lowercase()
    return when {
        "casual" in typeText || "liga" in typeText -> Color(0xFF2DD66F)
        "especial" in typeText || "presentación" in typeText || "presentacion" in typeText -> Color(0xFFFFA13A)
        else -> Purple
    }
}

// ─── EVENT DETAIL DIALOG (shared between HomeScreen and EventsScreen) ─────────

@Composable
internal fun EventDetailDialog(initialEvent: ApiEvent, vm: MainViewModel, onDismiss: () -> Unit) {
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
internal fun EventTag(text: String, color: Color) {
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
internal fun ProductStockTag(stock: Int) {
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
