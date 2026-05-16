package com.ultimatirada.app.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ultimatirada.app.R
import com.ultimatirada.app.ApiUser

@Composable
internal fun AppHeader(user: ApiUser?, cartCount: Int, onCartTap: () -> Unit, onAvatarTap: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color(0xCC08061A))
            .statusBarsPadding()
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
            Box(contentAlignment = Alignment.Center) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF211B3F)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = Color(0xFFBDB6CF), modifier = Modifier.size(22.dp))
                }
                if (cartCount > 0) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE53935)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("$cartCount", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, lineHeight = 10.sp)
                    }
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
