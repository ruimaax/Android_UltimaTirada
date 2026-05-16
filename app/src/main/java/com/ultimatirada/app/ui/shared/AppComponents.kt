package com.ultimatirada.app.ui.shared

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.shadow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ultimatirada.app.isoDateLabel

@Composable
internal fun BrandBackground(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF07051A)),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF5B1FA8).copy(alpha = 0.55f),
                radius = size.minDimension * 0.52f,
                center = Offset(size.width * 0.10f, size.height * 0.04f),
            )
            drawCircle(
                color = Color(0xFF0D3872).copy(alpha = 0.40f),
                radius = size.minDimension * 0.55f,
                center = Offset(size.width * 0.92f, size.height * 0.90f),
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
internal fun Avatar(url: String?, colorHex: String?, initial: String, size: Dp) {
    val background = colorHex?.toColorOrNull() ?: Purple
    if (!url.isNullOrBlank()) {
        AsyncImage(url, null, Modifier.size(size).clip(CircleShape).background(background), contentScale = ContentScale.Crop)
    } else {
        Box(Modifier.size(size).clip(CircleShape).background(background), contentAlignment = Alignment.Center) {
            Text(initial.take(2), color = Color.White, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
internal fun EmptyHint(text: String) {
    Box(Modifier.fillMaxWidth().padding(14.dp), contentAlignment = Alignment.Center) {
        Text(text, color = Muted)
    }
}

@Composable
internal fun ErrorBanner(message: String, onRetry: () -> Unit) {
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
internal fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
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
internal fun PageTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
        Text(subtitle, color = Muted, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun SectionPanel(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp), ambientColor = Color.Black.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(14.dp))
            .background(Panel)
            .border(1.dp, Border, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
        content()
    }
}

@Composable
internal fun AppCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp), ambientColor = Color.Black.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(14.dp))
            .background(Panel)
            .border(1.dp, Border, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
internal fun AppButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(34.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Purple),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Text(text, fontWeight = FontWeight.Black, fontSize = 16.sp)
    }
}

@Composable
internal fun AppButtonSecondary(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(34.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D2E00), contentColor = Gold),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Text(text, fontWeight = FontWeight.Black, fontSize = 16.sp)
    }
}

@Composable
internal fun FilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
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
internal fun FilterChipPill(text: String, selected: Boolean, onClick: () -> Unit) {
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
internal fun SegmentedControl(options: List<String>, selected: String, onSelected: (String) -> Unit) {
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
internal fun ProductImage(url: String?, modifier: Modifier) {
    if (url.isNullOrBlank()) {
        Box(modifier.background(Color(0xFF24264A)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.LocalMall, null, tint = Muted, modifier = Modifier.size(40.dp))
        }
    } else {
        AsyncImage(model = url, contentDescription = null, modifier = modifier.background(Color(0xFF24264A)), contentScale = ContentScale.Crop)
    }
}

@Composable
internal fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Purple,
    focusedBorderColor = DeepPurple,
    unfocusedBorderColor = DeepPurple,
    focusedContainerColor = Color(0xFF1A1830),
    unfocusedContainerColor = Color(0xFF1A1830),
)

internal fun String.toColorOrNull(): Color? = runCatching {
    val clean = removePrefix("#")
    Color(android.graphics.Color.parseColor("#$clean"))
}.getOrNull()

internal fun String.relativeLabel(): String = runCatching {
    val normalized = replace("Z", "").replace("T", " ").take(19)
    val created = java.time.LocalDateTime.parse(normalized, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val minutes = java.time.temporal.ChronoUnit.MINUTES.between(created, java.time.LocalDateTime.now()).coerceAtLeast(0)
    when {
        minutes < 1 -> "ahora"
        minutes < 60 -> "hace ${minutes} min"
        minutes < 24 * 60 -> "hace ${minutes / 60} h"
        minutes < 7 * 24 * 60 -> "hace ${minutes / (24 * 60)} d"
        else -> take(10).isoDateLabel()
    }
}.getOrElse { take(10).isoDateLabel() }

