package com.ultimatirada.app.ui.sell

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ultimatirada.app.ApiCardSaleAvailability
import com.ultimatirada.app.ApiCardSaleSlot
import com.ultimatirada.app.MainViewModel
import com.ultimatirada.app.isoDateLabel
import com.ultimatirada.app.ui.shared.DeepPurple
import com.ultimatirada.app.ui.shared.FilterChipPill
import com.ultimatirada.app.ui.shared.Gold
import com.ultimatirada.app.ui.shared.Muted
import com.ultimatirada.app.ui.shared.Purple
import com.ultimatirada.app.ui.shared.SegmentedControl
import com.ultimatirada.app.ui.shared.loginFieldColors
import java.io.ByteArrayOutputStream
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun SellDialog(vm: MainViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var mode by remember { mutableStateOf("1 carta") }
    var cardName by remember { mutableStateOf("") }
    var collection by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("Near Mint") }
    var bulkList by remember { mutableStateOf("") }
    var photoDataUrl by remember { mutableStateOf<String?>(null) }
    var cameraBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var preferredContact by remember { mutableStateOf("telefono") }
    var date by remember { mutableStateOf(nextCardSaleDate().toString()) }
    var availability by remember { mutableStateOf<ApiCardSaleAvailability?>(null) }
    var selectedSlot by remember { mutableStateOf<Int?>(null) }
    var result by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }

    val libraryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            photoUri = uri
            cameraBitmap = null
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            val bitmap = bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            photoDataUrl = bitmap?.toJpegDataUrl()
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            cameraBitmap = bitmap
            photoUri = null
            photoDataUrl = bitmap.toJpegDataUrl()
        }
    }

    LaunchedEffect(date) {
        selectedSlot = null
        vm.fetchCardSaleAvailability(date) { data ->
            availability = data
            selectedSlot = data?.slots?.firstOrNull { it.available }?.slotIndex
        }
    }
    LaunchedEffect(Unit) {
        sheetState.partialExpand()
    }

    val slots = availability?.slots ?: fallbackCardSaleSlots()
    val contactValue = if (preferredContact == "email") email else phone
    val canSend = !sending &&
        selectedSlot != null &&
        contactValue.isNotBlank() &&
        if (mode == "1 carta") cardName.isNotBlank() && collection.isNotBlank() else bulkList.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = Color(0xFF090913),
        dragHandle = {
            Box(
                Modifier
                    .padding(top = 10.dp)
                    .size(width = 56.dp, height = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF777684)),
            )
        },
    ) {
        Box(Modifier.fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp),
                contentPadding = PaddingValues(bottom = 112.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.weight(1f))
                        Text("Vender cartas", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF242234), contentColor = Color.White),
                        ) { Text("Cerrar", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    }
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Default.Euro, null, tint = Color.White, modifier = Modifier.size(24.dp))
                        Text("Vender Cartas", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    }
                    Text(
                        "Envía tus cartas y recibe una valoración para tienda o efectivo.",
                        color = Color(0xFFB8B1CC),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                item {
                    SellSection {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Inventory2, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text("¿Cuántas cartas quieres vender?", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        }
                        SegmentedControl(listOf("1 carta", "Varias cartas"), mode) { mode = it }
                    }
                }
                if (mode == "1 carta") {
                    item {
                        SellSection {
                            SellSectionTitle("Datos de la carta", Icons.Default.ShoppingCart)
                            SellTextField(cardName, { cardName = it }, "Nombre de la carta")
                            SellTextField(collection, { collection = it }, "Colección (ej: Tarkir: Dragonstorm)")
                            Text("Estado", color = Muted, fontWeight = FontWeight.Black)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Mint", "Near Mint", "Good", "Played", "Damaged").forEach { option ->
                                    FilterChipPill(option, condition == option) { condition = option }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                SellOutlineButton("Biblioteca", Icons.Default.Inventory2, Modifier.weight(1f)) { libraryLauncher.launch("image/*") }
                                SellOutlineButton("Cámara", Icons.Default.CameraAlt, Modifier.weight(1f)) { cameraLauncher.launch(null) }
                            }
                            if (photoUri != null || cameraBitmap != null) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(170.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF080812)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    cameraBitmap?.let {
                                        Image(it.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                    } ?: AsyncImage(photoUri, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                }
                            }
                        }
                    }
                } else {
                    item {
                        SellSection {
                            SellSectionTitle("Lista de cartas", Icons.Default.FilterList)
                            Text("Una carta por línea con el formato:", color = Muted, fontSize = 16.sp)
                            Text("Nombre de carta - Colección", color = Purple, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            OutlinedTextField(
                                bulkList,
                                { bulkList = it },
                                placeholder = { Text("Mox Jasper - Tarkir: Dragonstorm\nLightning Bolt - Alpha\nBlack Lotus - Limited Edition...") },
                                minLines = 8,
                                modifier = Modifier.fillMaxWidth(),
                                colors = loginFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                            )
                        }
                    }
                }
                item {
                    SellSection {
                        SellSectionTitle("Contacto y cita", Icons.Default.CalendarMonth)
                        SegmentedControl(listOf("Teléfono", "Email"), if (preferredContact == "email") "Email" else "Teléfono") {
                            preferredContact = if (it == "Email") "email" else "telefono"
                        }
                        if (preferredContact == "email") {
                            SellTextField(email, { email = it }, "Email de contacto", KeyboardType.Email)
                        } else {
                            SellTextField(phone, { phone = it }, "Teléfono de contacto", KeyboardType.Phone)
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Día de cita", color = Color.White, fontSize = 15.sp, modifier = Modifier.weight(1f))
                            Text(
                                date.isoDateLabel(),
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color(0xFF302E3F))
                                    .padding(horizontal = 16.dp, vertical = 9.dp),
                            )
                        }
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            slots.forEach { slot ->
                                val selected = selectedSlot == slot.slotIndex
                                Surface(
                                    onClick = { if (slot.available) selectedSlot = slot.slotIndex },
                                    enabled = slot.available,
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) Purple.copy(alpha = 0.35f) else Color(0xFF11101B),
                                    border = BorderStroke(1.2.dp, if (selected) Purple else DeepPurple.copy(alpha = 0.75f)),
                                    modifier = Modifier.width(148.dp),
                                ) {
                                    Column(Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${slot.startTime.take(5)} - ${slot.endTime.take(5)}", color = Muted, fontWeight = FontWeight.Black)
                                        Text(if (slot.available) "Disponible" else "Ocupado", color = Muted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                result?.let {
                    item { Text(it, color = if (it.startsWith("Solicitud")) Gold else Color(0xFFFF8A80), fontWeight = FontWeight.Bold) }
                }
            }
            Button(
                onClick = {
                    sending = true
                    result = null
                    vm.submitSellRequest(
                        cardName = cardName.takeIf { mode == "1 carta" },
                        collection = collection.takeIf { mode == "1 carta" },
                        condition = condition.takeIf { mode == "1 carta" },
                        bulkList = bulkList.takeIf { mode != "1 carta" },
                        contactPhone = phone.takeIf { preferredContact != "email" },
                        contactEmail = email.takeIf { preferredContact == "email" },
                        preferredContact = preferredContact,
                        appointmentDate = date,
                        appointmentSlotIndex = selectedSlot ?: 0,
                        photoDataUrl = photoDataUrl.takeIf { mode == "1 carta" },
                    ) { error ->
                        sending = false
                        result = error ?: "Solicitud enviada correctamente."
                    }
                },
                enabled = canSend,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp, vertical = 22.dp)
                    .height(62.dp),
                shape = RoundedCornerShape(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
            ) {
                Icon(Icons.Default.Send, null)
                Spacer(Modifier.width(10.dp))
                Text(if (sending) "Enviando..." else "Enviar solicitud", fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun SellSection(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF151329))
            .border(1.2.dp, DeepPurple.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = content,
    )
}

@Composable
private fun SellSectionTitle(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(16.dp))
        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun SellTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value,
        onValueChange,
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        colors = loginFieldColors(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
    )
}

@Composable
private fun SellOutlineButton(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
        border = BorderStroke(1.2.dp, DeepPurple.copy(alpha = 0.9f)),
    ) {
        Icon(icon, null)
        Spacer(Modifier.width(10.dp))
        Text(label, fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}

private fun Bitmap.toJpegDataUrl(): String {
    val output = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 82, output)
    return "data:image/jpeg;base64,${Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)}"
}

private fun nextCardSaleDate(from: LocalDate = LocalDate.now()): LocalDate {
    var cursor = from.plusDays(1)
    while (cursor.dayOfWeek !in setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)) {
        cursor = cursor.plusDays(1)
    }
    return cursor
}

private fun fallbackCardSaleSlots(): List<ApiCardSaleSlot> = listOf(
    ApiCardSaleSlot(0, "17:30", "18:00", true),
    ApiCardSaleSlot(1, "18:00", "18:30", true),
    ApiCardSaleSlot(2, "18:30", "19:00", true),
    ApiCardSaleSlot(3, "19:00", "19:30", true),
    ApiCardSaleSlot(4, "19:30", "20:00", true),
)
