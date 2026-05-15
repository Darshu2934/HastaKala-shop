package com.hastakala.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hastakala.app.data.*
import com.hastakala.app.ui.components.*
import com.hastakala.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddSaleScreen(
    products: List<Product>,
    lang: Language,
    onSave: (Sale) -> Unit,
    onAddProduct: (Product) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onUpdateProduct: (Product) -> Unit,
) {
    val t = getTranslations(lang)

    var selectedProduct by remember(products) { mutableStateOf(products.firstOrNull()) }
    var selectedColor   by remember { mutableStateOf(PREDEFINED_COLORS.first()) }
    var price           by remember { mutableStateOf("") }
    var initialStock    by remember { mutableStateOf("10") }
    var showSuccess     by remember { mutableStateOf(false) }
    var showMoreColors  by remember { mutableStateOf(false) }
    var showNewProduct  by remember { mutableStateOf(false) }
    var deleteTargetId  by remember { mutableStateOf<String?>(null) }

    val isCombinationNew = selectedProduct != null &&
            selectedProduct!!.variations[selectedColor.name] == null

    fun getTotalStock(p: Product) = p.variations.values.sum()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(t.newSale, style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
            Text(t.recordCreation, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        // Step 1: Select Product
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader("1. ${t.selectProduct}")
                TextButton(
                    onClick = { showNewProduct = true },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp), tint = ArtisanClay)
                    Spacer(Modifier.width(4.dp))
                    Text(t.addNew, style = MaterialTheme.typography.labelMedium, color = ArtisanClay)
                }
            }

            if (products.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No products yet. Tap '${t.addNew}' to create one!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.heightIn(max = 500.dp)
                ) {
                    items(products) { p ->
                        val isSelected = selectedProduct?.id == p.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(28.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                .border(
                                    2.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.05f),
                                    RoundedCornerShape(28.dp)
                                )
                                .clickable { selectedProduct = p }
                                .padding(16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (isSelected) Color.White.copy(0.2f) else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ProductIcon(
                                        p.iconName,
                                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Text(
                                    t.localizeProduct(p.name),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "${getTotalStock(p)} ${t.stock}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.White.copy(0.7f) else Color.Gray
                                )
                            }
                            // Delete button overlay
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .align(Alignment.TopEnd)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable { deleteTargetId = p.id },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Delete, null,
                                    tint = ColorDanger,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Step 2: Select Color
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader("2. ${t.selectColor}")
            // Use Compose built-in FlowRow (no Accompanist needed)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PREDEFINED_COLORS.forEach { c ->
                    val isSelected = selectedColor.name == c.name
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(
                                2.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.05f),
                                CircleShape
                            )
                            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(0.06f) else MaterialTheme.colorScheme.surface)
                            .clickable { selectedColor = c }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ColorDot(c.hex, size = 14)
                        Text(
                            t.localizeColor(c.name),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Gray
                        )
                        if (isSelected) {
                            Icon(Icons.Default.Check, null,
                                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                        }
                    }
                }
                // More colors pill
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .border(2.dp, ArtisanClay, CircleShape)
                        .clickable { showMoreColors = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Add, null, tint = ArtisanClay, modifier = Modifier.size(14.dp))
                    Text(t.moreColors, style = MaterialTheme.typography.labelMedium, color = ArtisanClay)
                }
            }
        }

        // Step 3: Initial Stock (only for new combinations)
        AnimatedVisibility(visible = isCombinationNew, enter = fadeIn(), exit = fadeOut()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionHeader("3. ${t.initialStock}")
                OutlinedTextField(
                    value = initialStock,
                    onValueChange = { initialStock = it },
                    placeholder = { Text("e.g. 10") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(0.08f)
                    )
                )
                Text(t.newCombinationHint,
                    style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        // Step 4: Price
        val priceStep = if (isCombinationNew) "4" else "3"
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionHeader("$priceStep. ${t.price}")
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                placeholder = { Text("0") },
                leadingIcon = {
                    Text(
                        "₹",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary.copy(0.4f),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = MaterialTheme.typography.headlineLarge,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(0.08f)
                )
            )
        }

        // Save Button
        val canSave = selectedProduct != null && price.isNotBlank() && !showSuccess
        Button(
            onClick = {
                val product = selectedProduct ?: return@Button
                if (isCombinationNew) {
                    val stock = initialStock.toIntOrNull() ?: 0
                    onUpdateProduct(product.copy(variations = product.variations + (selectedColor.name to stock)))
                }
                onSave(
                    Sale(
                        productId   = product.id,
                        productName = product.name,
                        colorName   = selectedColor.name,
                        colorHex    = selectedColor.hex,
                        price       = price.toDoubleOrNull() ?: 0.0
                    )
                )
                price = ""
                showSuccess = true
            },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (showSuccess) ColorSuccess else MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color.Gray.copy(0.3f)
            )
        ) {
            if (showSuccess) {
                LaunchedEffect(Unit) { delay(2000); showSuccess = false }
                Icon(Icons.Default.Check, null, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text(t.saved, style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
            } else {
                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(t.saveSale, style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
            }
        }
    }

    // Delete Confirmation Dialog
    if (deleteTargetId != null) {
        AlertDialog(
            onDismissRequest = { deleteTargetId = null },
            icon = { Icon(Icons.Default.Delete, null, tint = ColorDanger) },
            title = { Text("${t.delete}?", style = MaterialTheme.typography.headlineMedium) },
            text = { Text(t.deleteProductConfirm, style = MaterialTheme.typography.bodySmall) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProduct(deleteTargetId!!)
                        if (selectedProduct?.id == deleteTargetId) selectedProduct = null
                        deleteTargetId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorDanger)
                ) { Text(t.delete) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTargetId = null }) { Text(t.cancel) }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    // New Product Dialog
    if (showNewProduct) {
        NewProductDialog(
            t = t,
            onDismiss = { showNewProduct = false },
            onConfirm = { newProduct -> onAddProduct(newProduct); showNewProduct = false }
        )
    }

    // More Colors Dialog
    if (showMoreColors) {
        Dialog(onDismissRequest = { showMoreColors = false }) {
            Surface(shape = RoundedCornerShape(32.dp), color = MaterialTheme.colorScheme.surface) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(t.selectColorTitle,
                            style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                        IconButton(onClick = { showMoreColors = false }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 280.dp)
                    ) {
                        items(EXTENDED_COLORS) { c ->
                            val isSelected = selectedColor.name == c.name
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        2.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.05f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedColor = c; showMoreColors = false }
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ColorDot(c.hex, size = 36)
                                Text(t.localizeColor(c.name),
                                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewProductDialog(
    t: Translations,
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit
) {
    var name         by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("ShoppingBag") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(40.dp), color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier.padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(t.newProduct,
                        style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(t.productName) },
                    placeholder = { Text("e.g. Bag, Keychain") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(0.1f)
                    )
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 110.dp)
                ) {
                    items(ICON_OPTIONS) { iconName ->
                        val isSelected = selectedIcon == iconName
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedIcon = iconName },
                            contentAlignment = Alignment.Center
                        ) {
                            ProductIcon(
                                iconName,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary.copy(0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = { if (name.isNotBlank()) onConfirm(Product(name = name.trim(), iconName = selectedIcon)) },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ArtisanClay)
                ) {
                    Text(t.createProduct, style = MaterialTheme.typography.labelLarge, color = Color.White)
                }
            }
        }
    }
}
