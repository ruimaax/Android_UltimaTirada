package com.ultimatirada.app.ui.cart

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ultimatirada.app.ApiCheckoutResponse
import com.ultimatirada.app.CartStore
import com.ultimatirada.app.MainViewModel
import com.ultimatirada.app.ui.payment.PaymentFormSheet
import com.ultimatirada.app.ui.shared.Gold
import com.ultimatirada.app.ui.shared.Muted
import com.ultimatirada.app.ui.shared.Panel
import com.ultimatirada.app.ui.shared.ProductImage
import com.ultimatirada.app.ui.shared.Purple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CartDialog(cart: CartStore, vm: MainViewModel, onDismiss: () -> Unit) {
    val items by cart.items.collectAsState()
    var showCheckout by remember { mutableStateOf(false) }
    var checkoutResult by remember { mutableStateOf<ApiCheckoutResponse?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF070711)),
        ) {
            Column(Modifier.fillMaxSize().statusBarsPadding()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BadgedBox(badge = { if (items.isNotEmpty()) Badge { Text("${items.sumOf { it.quantity }}") } }) {
                        Icon(Icons.Default.ShoppingCart, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Text("Carrito", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                    if (items.isNotEmpty()) {
                        TextButton(onClick = { showClearConfirm = true }) {
                            Text("Vaciar", color = Color(0xFFFF8A80), fontWeight = FontWeight.Bold)
                        }
                    }
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF211B3F), contentColor = Purple),
                    ) { Text("Cerrar", fontSize = 16.sp) }
                }

                if (checkoutResult != null) {
                    val result = checkoutResult!!
                    LazyColumn(
                        Modifier.weight(1f),
                        contentPadding = PaddingValues(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        item {
                            Column(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF0D1A0A)).border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                Icon(Icons.Default.Verified, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(52.dp))
                                Text("¡Pedido confirmado!", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                                result.orderCode?.let { code ->
                                    Text("Código: $code", color = Gold, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                }
                                result.magicPhrase?.let { phrase ->
                                    Column(
                                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF241C40)).padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Text("Tu frase mágica", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("\"$phrase\"", color = Purple, fontSize = 16.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                                    }
                                }
                                result.total?.let { total ->
                                    Text("Total: %.2f€".format(total), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    "Nos pondremos en contacto contigo para coordinar la recogida.",
                                    color = Muted, fontSize = 14.sp, textAlign = TextAlign.Center, lineHeight = 20.sp,
                                )
                                Button(
                                    onClick = onDismiss,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(30.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                                ) { Text("Cerrar", fontWeight = FontWeight.Black, fontSize = 16.sp) }
                            }
                        }
                    }
                } else if (items.isEmpty()) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.ShoppingCart, null, tint = Muted, modifier = Modifier.size(64.dp))
                            Text("Tu carrito está vacío", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            Text("Añade productos desde la tienda.", color = Muted, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    LazyColumn(
                        Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(items, key = { it.id }) { item ->
                            Row(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                    .background(Panel).padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                ProductImage(item.product.image, Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)))
                                Column(Modifier.weight(1f)) {
                                    Text(item.product.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Text("%.2f€/ud".format(item.product.price), color = Muted, fontSize = 12.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(30.dp).clip(CircleShape).background(Color(0xFF211B3F)).clickable { cart.updateQuantity(item.id, item.quantity - 1) }, contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Remove, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                    Text("${item.quantity}", color = Color.White, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 10.dp))
                                    Box(Modifier.size(30.dp).clip(CircleShape).background(Color(0xFF211B3F)).clickable { cart.updateQuantity(item.id, item.quantity + 1) }, contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Box(Modifier.size(30.dp).clip(CircleShape).background(Color(0xFF3A1B24)).clickable { cart.remove(item.id) }, contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Close, null, tint = Color(0xFFFF8A80), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                        item {
                            Row(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF211B3F)).padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Subtotal", color = Muted, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(cart.subtotalFormatted, color = Gold, fontSize = 20.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                    Box(
                        Modifier.fillMaxWidth().background(Color(0xF5070711))
                            .navigationBarsPadding().padding(horizontal = 18.dp, vertical = 14.dp),
                    ) {
                        Button(
                            onClick = { showCheckout = true },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(34.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple),
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Realizar pedido · ${cart.subtotalFormatted}", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    if (showCheckout) {
        PaymentFormSheet(
            title = "Confirmar pedido",
            subtitle = "Revisa tu pedido y elige cómo pagar.",
            summaryTitle = "${items.sumOf { it.quantity }} producto(s)",
            summarySubtitle = "${items.size} artículo(s) en tu carrito",
            subtotal = cart.subtotal,
            chequetiendaBalance = vm.uiState.value.currentUser?.chequetiendaBalance ?: 0.0,
            onDismiss = { showCheckout = false },
            onConfirm = { method, phone, email, note, discount, useCheque ->
                vm.checkoutCart(cart, method, phone, email, note, discount, useCheque) { result, error ->
                    if (error != null) {
                        // error handled inside PaymentFormSheet via callback
                    } else {
                        checkoutResult = result
                    }
                    showCheckout = false
                }
            },
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("¿Vaciar el carrito?") },
            text = { Text("Se eliminarán todos los productos.", color = Muted) },
            confirmButton = {
                Button(
                    onClick = { cart.clear(); showClearConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B1A2F)),
                ) { Text("Vaciar") }
            },
            dismissButton = { TextButton({ showClearConfirm = false }) { Text("Cancelar") } },
            containerColor = Panel,
        )
    }
}
