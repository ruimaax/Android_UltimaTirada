package com.ultimatirada.app.ui.shared

import androidx.compose.ui.graphics.Color

internal val Background = Color(0xFF090911)
internal val Panel = Color(0xFF17162B)
internal val CardColor = Color(0xFF0E0E16)
internal val Border = Color(0xFF523895)
internal val Active = Color(0xFF291F57)
internal val Muted = Color(0xFFB8BCE5)
internal val Blue = Color(0xFF4EA8FF)
internal val Purple = Color(0xFF9B6CFF)
internal val DeepPurple = Color(0xFF6E43C6)
internal val Gold = Color(0xFFFFC857)

internal object MonetizationConfig {
    const val REMOVE_ADS_PRODUCT_ID = "ultima_tirada_remove_ads_lifetime_999"
    const val ADS_REMOVED_KEY = "monetization.adsRemoved"
    const val NATIVE_COMMUNITY_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110" // test
    const val NATIVE_STORE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110" // test
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917" // test
}

internal enum class AppTab(val label: String) {
    Home("Inicio"),
    Store("Tienda"),
    Events("Eventos"),
    Ally("Aliado"),
    Community("Comunidad"),
    Profile("Perfil"),
}
