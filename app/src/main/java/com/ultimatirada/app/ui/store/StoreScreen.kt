package com.ultimatirada.app.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ultimatirada.app.ApiProduct
import com.ultimatirada.app.CartStore
import com.ultimatirada.app.MainViewModel
import com.ultimatirada.app.ui.events.EventTag
import com.ultimatirada.app.ui.events.ProductStockTag
import com.ultimatirada.app.ui.sell.SellDialog
import com.ultimatirada.app.ui.shared.Blue
import com.ultimatirada.app.ui.shared.CardColor
import com.ultimatirada.app.ui.shared.DeepPurple
import com.ultimatirada.app.ui.shared.EmptyHint
import com.ultimatirada.app.ui.shared.FilterChipPill
import com.ultimatirada.app.ui.shared.Gold
import com.ultimatirada.app.ui.shared.MonetizationConfig
import com.ultimatirada.app.ui.shared.Muted
import com.ultimatirada.app.ui.shared.NativeAdCard
import com.ultimatirada.app.ui.shared.ProductImage
import com.ultimatirada.app.ui.shared.Purple
import com.ultimatirada.app.ui.shared.loginFieldColors

@Composable
internal fun StoreScreen(products: List<ApiProduct>, loading: Boolean, cart: CartStore, vm: MainViewModel, adsRemoved: Boolean = false) {
    var search by remember { mutableStateOf("") }
    var showSell by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<ApiProduct?>(null) }
    var selectedBrands by remember { mutableStateOf(setOf<String>()) }
    var selectedCollections by remember { mutableStateOf(setOf<String>()) }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var stockFilter by remember { mutableStateOf("Todos") }
    var sortOption by remember { mutableStateOf("Destacados") }
    val brands = products.mapNotNull { it.brand?.takeIf(String::isNotBlank) }.distinct().sorted()
    val collections = products.mapNotNull { it.collection?.takeIf(String::isNotBlank) }.distinct().sorted()
    val categories = products.mapNotNull { it.category?.takeIf(String::isNotBlank) }.distinct().sorted()
    val min = minPrice.replace(",", ".").toDoubleOrNull()
    val max = maxPrice.replace(",", ".").toDoubleOrNull()
    val filtered = products.filter { product ->
        val matchesSearch = search.isBlank() ||
            product.name.contains(search, true) ||
            product.brand.orEmpty().contains(search, true) ||
            product.collection.orEmpty().contains(search, true) ||
            product.category.orEmpty().contains(search, true)
        val matchesBrand = selectedBrands.isEmpty() || selectedBrands.contains(product.brand.orEmpty())
        val matchesCollection = selectedCollections.isEmpty() || selectedCollections.contains(product.collection.orEmpty())
        val matchesCategory = selectedCategories.isEmpty() || selectedCategories.contains(product.category.orEmpty())
        val matchesPrice = (min == null || product.price >= min) && (max == null || product.price <= max)
        val matchesStock = when (stockFilter) {
            "En stock" -> product.stock > 0
            "Sin stock" -> product.stock <= 0
            else -> true
        }
        matchesSearch && matchesBrand && matchesCollection && matchesCategory && matchesPrice && matchesStock
    }.let { list ->
        when (sortOption) {
            "Precio ↑" -> list.sortedBy { it.price }
            "Precio ↓" -> list.sortedByDescending { it.price }
            "Nombre" -> list.sortedBy { it.name.lowercase() }
            else -> list.sortedWith(compareByDescending<ApiProduct> { it.featured }.thenBy { it.name })
        }
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item { StoreTitle() }
        item { SellCardsBanner { showSell = true } }
        item {
            StoreFiltersPanel(
                brands = brands,
                collections = collections,
                categories = categories,
                selectedBrands = selectedBrands,
                selectedCollections = selectedCollections,
                selectedCategories = selectedCategories,
                minPrice = minPrice,
                maxPrice = maxPrice,
                stockFilter = stockFilter,
                sortOption = sortOption,
                onToggleBrand = { selectedBrands = selectedBrands.toggle(it) },
                onToggleCollection = { selectedCollections = selectedCollections.toggle(it) },
                onToggleCategory = { selectedCategories = selectedCategories.toggle(it) },
                onMinPrice = { minPrice = it },
                onMaxPrice = { maxPrice = it },
                onStockFilter = { stockFilter = it },
                onSortOption = { sortOption = it },
                onClear = {
                    selectedBrands = emptySet()
                    selectedCollections = emptySet()
                    selectedCategories = emptySet()
                    minPrice = ""
                    maxPrice = ""
                    stockFilter = "Todos"
                    sortOption = "Destacados"
                    search = ""
                },
            )
        }
        item {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Buscar producto") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = loginFieldColors(),
            )
        }
        if (loading && products.isEmpty()) {
            item { Box(Modifier.fillMaxWidth().height(260.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Purple) } }
        } else {
            item {
                Text(
                    "${filtered.size} productos",
                    color = Color(0xFFB8B1CC),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                )
            }
            val chunked = filtered.chunked(2)
            itemsIndexed(chunked, key = { _, row -> row.joinToString("-") { it.id.toString() } }) { index, row ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        row.forEach { product ->
                            Box(Modifier.weight(1f)) {
                                ProductCard(product, onTap = { selectedProduct = product })
                            }
                        }
                        if (row.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                    if (!adsRemoved && (index + 1) % 5 == 0) {
                        NativeAdCard(MonetizationConfig.NATIVE_STORE_AD_UNIT_ID)
                    }
                }
            }
        }
    }

    if (showSell) SellDialog(vm = vm, onDismiss = { showSell = false })
    selectedProduct?.let { product ->
        ProductDetailDialog(
            product = product,
            cart = cart,
            onDismiss = { selectedProduct = null },
        )
    }
}

private fun Set<String>.toggle(value: String): Set<String> = if (contains(value)) this - value else this + value

@Composable
private fun StoreTitle() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Default.Inventory2, null, tint = Color.White, modifier = Modifier.size(24.dp))
            Text("Tienda", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
        }
        Text(
            "Accesorios, fundas, binders y material para tus partidas.",
            color = Color(0xFFB8B1CC),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun SellCardsBanner(onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xAA11101C))
            .border(1.dp, Gold.copy(alpha = 0.30f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(Modifier.size(44.dp).clip(CircleShape).background(Gold.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Euro, null, tint = Gold, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f)) {
            Text("¿Quieres vender tus cartas?", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Text("Pide una valoración gratuita", color = Color(0xFFB8B1CC), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFB8B1CC), modifier = Modifier.size(18.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoreFiltersPanel(
    brands: List<String>,
    collections: List<String>,
    categories: List<String>,
    selectedBrands: Set<String>,
    selectedCollections: Set<String>,
    selectedCategories: Set<String>,
    minPrice: String,
    maxPrice: String,
    stockFilter: String,
    sortOption: String,
    onToggleBrand: (String) -> Unit,
    onToggleCollection: (String) -> Unit,
    onToggleCategory: (String) -> Unit,
    onMinPrice: (String) -> Unit,
    onMaxPrice: (String) -> Unit,
    onStockFilter: (String) -> Unit,
    onSortOption: (String) -> Unit,
    onClear: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xEE0D0A1C))
            .border(1.2.dp, DeepPurple.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.FilterList, null, tint = Color.White, modifier = Modifier.size(18.dp))
            Text("Filtros", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
        FilterRow("Marca", brands, selectedBrands, onToggleBrand)
        FilterRow("Colección", collections, selectedCollections, onToggleCollection)
        FilterRow("Categoría", categories, selectedCategories, onToggleCategory)
        Text("Rango de precio", color = Color(0xFFB8B1CC), fontSize = 12.sp, fontWeight = FontWeight.Black)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StoreSmallField("Mín €", minPrice, onMinPrice, Modifier.weight(1f))
            StoreSmallField("Máx €", maxPrice, onMaxPrice, Modifier.weight(1f))
        }
        Text("Stock", color = Color(0xFFB8B1CC), fontSize = 12.sp, fontWeight = FontWeight.Black)
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF343240)),
        ) {
            listOf("Todos", "En stock", "Sin stock").forEach { option ->
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(28.dp))
                        .background(if (stockFilter == option) Color(0xFF777480) else Color.Transparent)
                        .clickable { onStockFilter(option) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(option, color = Color.White, fontSize = 16.sp)
                }
            }
        }
        Text("Ordenar", color = Color(0xFFB8B1CC), fontSize = 12.sp, fontWeight = FontWeight.Black)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("Destacados", "Precio ↑", "Precio ↓", "Nombre").forEach { option ->
                FilterChipPill(option, sortOption == option) { onSortOption(option) }
            }
        }
        Button(
            onClick = onClear,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF211B3F), contentColor = Color.White),
        ) {
            Text("Limpiar filtros", fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun FilterRow(title: String, options: List<String>, selected: Set<String>, onToggle: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, color = Color(0xFFB8B1CC), fontSize = 16.sp, fontWeight = FontWeight.Black)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(options) { option -> FilterChipPill(option.uppercase(), selected.contains(option)) { onToggle(option) } }
        }
    }
}

@Composable
private fun StoreSmallField(label: String, value: String, onValue: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        placeholder = { Text(label) },
        singleLine = true,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = loginFieldColors(),
    )
}

@Composable
private fun ProductCarousel(products: List<ApiProduct>) {
    if (products.isEmpty()) {
        EmptyHint("Aún no hay productos destacados.")
    } else {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(products, key = { it.id }) { product ->
                ProductMiniCard(product)
            }
        }
    }
}

@Composable
private fun ProductMiniCard(product: ApiProduct) {
    Card(colors = CardDefaults.cardColors(containerColor = CardColor), modifier = Modifier.width(170.dp)) {
        ProductImage(product.image, Modifier.fillMaxWidth().height(112.dp))
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(product.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(product.priceFormatted, color = Gold, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun ProductCard(product: ApiProduct, onTap: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xEE0D0A1C)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.clickable(onClick = onTap),
    ) {
        ProductImage(product.image, Modifier.fillMaxWidth().aspectRatio(1.1f).padding(8.dp))
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(product.name.uppercase(), color = Color.White, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(product.brand ?: product.collection ?: "Última Tirada", color = Color(0xFFB8B1CC), fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(product.priceFormatted, color = Blue, fontWeight = FontWeight.Black, fontSize = 18.sp, modifier = Modifier.weight(1f))
                Text("Stock: ${product.stock}", color = if (product.isInStock) Color(0xFF56E45D) else Color(0xFFFF555D), fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun ProductDetailDialog(product: ApiProduct, cart: CartStore, onDismiss: () -> Unit) {
    var quantity by remember(product.id) { mutableStateOf(1) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        Box(Modifier.fillMaxSize().background(Color(0xFF070711))) {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 26.dp, vertical = 18.dp)
                    .padding(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF211F32), contentColor = Purple),
                        ) { Text("Cerrar", fontSize = 18.sp) }
                    }
                }
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(310.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF4B2388), Color(0xFF050509)))),
                        contentAlignment = Alignment.Center,
                    ) {
                        ProductImage(product.image, Modifier.fillMaxSize().padding(28.dp))
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text((product.brand ?: "ULTIMA TIRADA").uppercase(), color = Color(0xFFB8B1CC), fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                        Text(product.name.uppercase(), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black, lineHeight = 26.sp)
                        Text(product.priceFormatted, color = Purple, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            EventTag(product.collection ?: "TIENDA", Purple)
                            EventTag(product.category ?: "PRODUCTO", Blue)
                            ProductStockTag(product.stock)
                        }
                    }
                }
                item {
                    Column(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF19162C)).padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Descripción", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Text(product.description ?: "Sin descripción disponible.", color = Color(0xFFB8B1CC), fontSize = 14.sp)
                    }
                }
                item {
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF19162C)).padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Cantidad", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                        Row(Modifier.clip(RoundedCornerShape(28.dp)).background(Color(0xFF343240)), verticalAlignment = Alignment.CenterVertically) {
                            IconButton({ quantity = (quantity - 1).coerceAtLeast(1) }) { Icon(Icons.Default.Remove, null, tint = Color.White) }
                            Text("$quantity", color = Color.White, fontWeight = FontWeight.Black)
                            IconButton({ quantity = (quantity + 1).coerceAtMost(product.stock.coerceAtLeast(1)) }) { Icon(Icons.Default.Add, null, tint = Color.White) }
                        }
                    }
                }
            }
            Button(
                onClick = {
                    cart.add(product, quantity)
                    onDismiss()
                },
                enabled = product.isInStock,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp)
                    .padding(bottom = 54.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
            ) {
                Icon(Icons.Default.ShoppingCart, null)
                Spacer(Modifier.width(8.dp))
                Text("Añadir al carrito", fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
