package com.ultimatirada.app.ui.shared

import android.app.Activity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.ultimatirada.app.MainViewModel
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.QueryPurchasesParams

@Composable
internal fun NativeAdCard(adUnitId: String) {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    DisposableEffect(adUnitId) {
        val loader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad -> nativeAd = ad }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) { nativeAd = null }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().setVideoOptions(VideoOptions.Builder().setStartMuted(true).build()).build())
            .build()
        loader.loadAd(AdRequest.Builder().build())
        onDispose { nativeAd?.destroy() }
    }

    nativeAd?.let { ad ->
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0D0B1C)).border(1.dp, Border.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        ) {
            AndroidView(
                factory = { ctx ->
                    val adView = NativeAdView(ctx)
                    val layout = FrameLayout(ctx).apply {
                        setPadding(32, 28, 32, 28)
                    }
                    val headline = TextView(ctx).apply {
                        setTextColor(android.graphics.Color.WHITE)
                        textSize = 15f
                        setTypeface(null, android.graphics.Typeface.BOLD)
                        text = ad.headline ?: "Publicidad"
                    }
                    val body = TextView(ctx).apply {
                        setTextColor(android.graphics.Color.parseColor("#B8BCE5"))
                        textSize = 12f
                        text = ad.body ?: ""
                    }
                    val adLabel = TextView(ctx).apply {
                        text = "Anuncio"
                        textSize = 10f
                        setTextColor(android.graphics.Color.parseColor("#9B6CFF"))
                        background = android.graphics.drawable.GradientDrawable().apply {
                            setColor(android.graphics.Color.parseColor("#1A1530"))
                            cornerRadius = 20f
                        }
                        setPadding(16, 4, 16, 4)
                    }
                    val innerLayout = android.widget.LinearLayout(ctx).apply {
                        orientation = android.widget.LinearLayout.VERTICAL
                        val p8 = (8 * ctx.resources.displayMetrics.density).toInt()
                        addView(adLabel)
                        addView(headline)
                        addView(body)
                        for (i in 0 until childCount) getChildAt(i).let {
                            (it.layoutParams as? android.widget.LinearLayout.LayoutParams)?.apply { topMargin = p8 }
                        }
                    }
                    layout.addView(innerLayout)
                    adView.headlineView = headline
                    adView.bodyView = body
                    adView.addView(layout)
                    adView.setNativeAd(ad)
                    adView
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
internal fun RewardedAdCard(vm: MainViewModel) {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var rewardedAd by remember { mutableStateOf<RewardedAd?>(null) }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        RewardedAd.load(
            context,
            MonetizationConfig.REWARDED_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) { rewardedAd = ad }
                override fun onAdFailedToLoad(error: LoadAdError) { rewardedAd = null }
            },
        )
        onDispose { rewardedAd = null }
    }

    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1A1040), Color(0xFF0D0820), Color(0xFF1A1040))))
            .border(1.dp, Purple.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(18.dp),
    ) {
        Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(38.dp).clip(CircleShape).background(Purple.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PlayCircle, null, tint = Purple, modifier = Modifier.size(22.dp))
                }
                Column {
                    Text("Ver anuncio para ganar puntos", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    Text("Mira un breve vídeo y suma puntos a tu ranking.", color = Muted, fontSize = 12.sp)
                }
            }
            resultMessage?.let { msg ->
                Text(msg, color = if (msg.startsWith("¡")) Gold else Color(0xFFFF8A80), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Button(
                onClick = {
                    val activity = context as? Activity ?: return@Button
                    val ad = rewardedAd
                    if (ad != null) {
                        loading = true
                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                loading = false
                                rewardedAd = null
                            }
                        }
                        ad.show(activity) {
                            vm.claimRewardedAdPoints { points, error ->
                                loading = false
                                resultMessage = if (error != null) error
                                else "¡+${points ?: 0} puntos ganados!"
                            }
                        }
                    } else {
                        resultMessage = "Anuncio no disponible. Inténtalo más tarde."
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepPurple),
            ) {
                if (loading) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                else {
                    Icon(Icons.Default.Star, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Ver anuncio", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
internal fun RemoveAdsCard(adsRemoved: Boolean, onAdsRemoved: () -> Unit) {
    val context = LocalContext.current
    var billingClient by remember { mutableStateOf<BillingClient?>(null) }
    var productDetails by remember { mutableStateOf<ProductDetails?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        // clientHolder permite referenciar el cliente dentro del listener
        // antes de que la variable 'client' esté completamente asignada
        var clientHolder: BillingClient? = null
        val client = BillingClient.newBuilder(context)
            .setListener { result, purchases ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    purchases.forEach { purchase ->
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                            purchase.products.contains(MonetizationConfig.REMOVE_ADS_PRODUCT_ID)
                        ) {
                            if (!purchase.isAcknowledged) {
                                val ackParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                                clientHolder?.acknowledgePurchase(ackParams) {}
                            }
                            onAdsRemoved()
                        }
                    }
                }
            }
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()
        clientHolder = client
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    val params = QueryProductDetailsParams.newBuilder()
                        .setProductList(listOf(
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(MonetizationConfig.REMOVE_ADS_PRODUCT_ID)
                                .setProductType(BillingClient.ProductType.INAPP)
                                .build(),
                        ))
                        .build()
                    client.queryProductDetailsAsync(params) { _, details ->
                        productDetails = details.firstOrNull()
                    }
                    client.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                    ) { _, purchases ->
                        if (purchases.any { it.products.contains(MonetizationConfig.REMOVE_ADS_PRODUCT_ID) && it.purchaseState == Purchase.PurchaseState.PURCHASED }) {
                            onAdsRemoved()
                        }
                    }
                }
            }
            override fun onBillingServiceDisconnected() {}
        })
        billingClient = client
        onDispose { client.endConnection() }
    }

    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(if (adsRemoved) Brush.linearGradient(listOf(Color(0xFF0D1A0A), Color(0xFF0D1A0A))) else Brush.linearGradient(listOf(Color(0xFF101030), Color(0xFF0A0A18))))
            .border(1.dp, if (adsRemoved) Color(0xFF4CAF50).copy(alpha = 0.5f) else Blue.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(18.dp),
    ) {
        Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
                Box(
                    Modifier.size(42.dp).clip(CircleShape)
                        .background(if (adsRemoved) Color(0xFF4CAF50).copy(alpha = 0.2f) else Blue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (adsRemoved) Icons.Default.Verified else Icons.Default.Shield,
                        null,
                        tint = if (adsRemoved) Color(0xFF4CAF50) else Blue,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        if (adsRemoved) "Anuncios eliminados" else "Quitar anuncios",
                        color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp,
                    )
                    Text(
                        if (adsRemoved) "Gracias por apoyar Última Tirada." else (productDetails?.oneTimePurchaseOfferDetails?.formattedPrice?.let { "Pago único de $it. Sin suscripción." } ?: "Pago único · Sin suscripción."),
                        color = Muted, fontSize = 12.sp,
                    )
                }
            }
            statusMessage?.let { msg -> Text(msg, color = Color(0xFFFF8A80), fontSize = 12.sp) }
            if (!adsRemoved) {
                Button(
                    onClick = {
                        val activity = context as? Activity ?: return@Button
                        val details = productDetails ?: return@Button
                        loading = true
                        val flowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(listOf(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(details)
                                    .build(),
                            ))
                            .build()
                        val result = billingClient?.launchBillingFlow(activity, flowParams)
                        if (result?.responseCode != BillingClient.BillingResponseCode.OK) {
                            loading = false
                            statusMessage = "No se pudo iniciar la compra."
                        }
                    },
                    enabled = !loading && productDetails != null,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue),
                ) {
                    if (loading) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    else {
                        Icon(Icons.Default.Shield, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Comprar sin anuncios", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
