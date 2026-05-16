package com.ultimatirada.app.ui.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ultimatirada.app.ui.shared.Blue
import com.ultimatirada.app.ui.shared.Gold
import com.ultimatirada.app.ui.shared.Muted
import com.ultimatirada.app.ui.shared.Purple

internal data class PlayerRankData(
    val name: String,
    val icon: ImageVector,
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

internal fun playerRank(points: Int): PlayerRankData = when {
    points >= 200 -> PlayerRankData("Leyenda Local", Icons.Default.EmojiEvents, Gold, 200, null)
    points >= 100 -> PlayerRankData("Planeswalker", Icons.Default.AutoAwesome, Purple, 100, 200)
    points >= 40  -> PlayerRankData("Duelista", Icons.Default.Shield, Blue, 40, 100)
    else          -> PlayerRankData("Aprendiz", Icons.Default.School, Muted, 0, 40)
}
