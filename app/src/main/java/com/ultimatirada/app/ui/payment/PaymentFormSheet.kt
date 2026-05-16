package com.ultimatirada.app.ui.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ultimatirada.app.ui.shared.Border
import com.ultimatirada.app.ui.shared.DeepPurple
import com.ultimatirada.app.ui.shared.Gold
import com.ultimatirada.app.ui.shared.Muted
import com.ultimatirada.app.ui.shared.Purple
import com.ultimatirada.app.ui.shared.loginFieldColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PaymentFormSheet(
    title: String,
    subtitle: String,
    summaryTitle: String,
    summarySubtitle: String,
    subtotal: Double,
    chequetiendaBalance: Double,
    onDismiss: () -> Unit,
    onConfirm: (method: String, phone: String, email: String, note: String, discount: String, useCheque: Boolean) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var method by remember { mutableStateOf("bizum") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var discountCode by remember { mutableStateOf("") }
    var useChequetienda by remember { mutableStateOf(false) }
    var sending by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val canConfirm = !sending && (phone.isNotBlank() || email.isNotBlank())

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xFF090913),
        dragHandle = {
            Box(Modifier.padding(top = 10.dp).size(width = 56.dp, height = 6.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFF777684)))
        },
    ) {
        LazyColumn(
            Modifier.fillMaxWidth().padding(horizontal = 22.dp),
            contentPadding = PaddingValues(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text(subtitle, color = Muted, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }
            item {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF15132A)).border(1.dp, Border.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(summaryTitle, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(summarySubtitle, color = Muted, fontSize = 12.sp)
                        }
                        Text("%.2f€".format(subtotal), color = Purple, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                    if (chequetiendaBalance > 0) {
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1A1530)).padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, null, tint = Gold, modifier = Modifier.size(18.dp))
                            Text("Usar ChequeTienda (%.2f€ disponibles)".format(chequetiendaBalance), color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Switch(
                                checked = useChequetienda,
                                onCheckedChange = { useChequetienda = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Gold, checkedTrackColor = Gold.copy(alpha = 0.4f)),
                            )
                        }
                    }
                }
            }
            item {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF15132A)).border(1.dp, Border.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.AccountBalanceWallet, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Text("Método de pago", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Color(0xFF343240)),
                    ) {
                        listOf("bizum" to "Bizum", "cash" to "Efectivo").forEach { (value, label) ->
                            Box(
                                Modifier.weight(1f).clip(RoundedCornerShape(28.dp))
                                    .background(if (method == value) DeepPurple else Color.Transparent)
                                    .clickable { method = value }.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text(
                        if (method == "cash") "Pago en efectivo: nos pondremos en contacto para coordinar la recogida."
                        else "Pago por Bizum: rápido y sin comisiones. Te contactaremos para coordinar.",
                        color = Muted, fontSize = 12.sp, lineHeight = 18.sp,
                    )
                    OutlinedTextField(
                        discountCode, { discountCode = it.uppercase() },
                        label = { Text("Código de descuento (opcional)") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        colors = loginFieldColors(), shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(autoCorrect = false),
                    )
                    OutlinedTextField(
                        phone, { phone = it },
                        label = { Text("Teléfono (WhatsApp)") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        colors = loginFieldColors(), shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    )
                    OutlinedTextField(
                        email, { email = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        colors = loginFieldColors(), shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    )
                    OutlinedTextField(
                        note, { note = it },
                        label = { Text("Nota adicional (opcional)") },
                        modifier = Modifier.fillMaxWidth().height(88.dp),
                        colors = loginFieldColors(), shape = RoundedCornerShape(10.dp),
                        maxLines = 3,
                    )
                }
            }
            errorMessage?.let { err ->
                item { Text(err, color = Color(0xFFFF8A80), fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            }
            item {
                Button(
                    onClick = {
                        errorMessage = null
                        sending = true
                        onConfirm(method, phone, email, note, discountCode, useChequetienda)
                    },
                    enabled = canConfirm,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(34.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                ) {
                    if (sending) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    else {
                        Icon(Icons.Default.Verified, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Confirmar pedido", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
