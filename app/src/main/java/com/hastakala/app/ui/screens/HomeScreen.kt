package com.hastakala.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hastakala.app.data.*
import com.hastakala.app.ui.components.*
import com.hastakala.app.ui.theme.*

@Composable
fun HomeScreen(
    sales: List<Sale>,
    products: List<Product>,
    lang: Language,
    onNavigate: (String) -> Unit,
    onUpdateProduct: (Product) -> Unit = {}
) {
    val t = getTranslations(lang)

    val totalIncome = sales.sumOf { it.price }

    // Best performing variation
    val variationCounts = mutableMapOf<String, Int>()
    sales.forEach { s ->
        val color = t.localizeColor(s.colorName)
        val prod = t.localizeProduct(s.productName)
        val key = if (color.isNotBlank()) "$color $prod" else prod
        variationCounts[key] = (variationCounts[key] ?: 0) + 1
    }
    val maxSales = variationCounts.values.maxOrNull() ?: 0
    val bestVariations = if (maxSales > 0) {
        variationCounts.filter { it.value == maxSales }.keys.toList()
    } else {
        emptyList()
    }

    // Low stock alerts
    val lowStockAlerts = mutableListOf<Triple<Product, String, Int>>() // product, colorName, stock
    products.forEach { p ->
        p.variations.forEach { (colorName, stock) ->
            if (stock < 3) lowStockAlerts.add(Triple(p, colorName, stock))
        }
    }

    var restockTarget by remember { mutableStateOf<Triple<Product, String, Int>?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        item {
            Column {
                Text(
                    text = when (lang) {
                        Language.KN -> "ಹಸ್ತ-ಕಲಾ ಮಳಿಗೆ"
                        Language.HI -> "हस्त-कला शॉप"
                        else -> "Hasta-Kala Shop"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(t.welcome, style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
                Text(t.subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }

        // ── Stat Cards ──────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = { Icon(Icons.Default.CurrencyRupee, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
                    label = t.totalIncome,
                    value = "₹${totalIncome.toLong()}",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = { Icon(Icons.Default.ShoppingBag, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
                    label = t.orders,
                    value = "${sales.size}",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Empty State ─────────────────────────────────────────────────────
        if (sales.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(40.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(40.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(80.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                        }
                        Text(t.emptyStateTitle, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text(t.emptyStateSub, style = MaterialTheme.typography.bodySmall, color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Spacer(Modifier.height(4.dp))
                        PrimaryButton(
                            text = t.addNow,
                            onClick = { onNavigate("add") },
                            icon = { Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }
        }

        // ── Best Seller Card ────────────────────────────────────────────────
        if (sales.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer))
                        )
                        .clickable { onNavigate("analytics") }
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.TrendingUp, null, tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                    modifier = Modifier.size(14.dp))
                                Text(t.bestSeller.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                            }
                            
                            if (bestVariations.isEmpty()) {
                                Text(t.noSalesYet,
                                    style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.onPrimary))
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    bestVariations.forEach { variation ->
                                        Text(
                                            text = variation,
                                            style = if (bestVariations.size > 1) 
                                                MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onPrimary)
                                            else 
                                                MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.onPrimary),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    Spacer(Modifier.height(4.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "$maxSales ${t.salesCount}",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                            
                            Text(t.bestSellerSub,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f))
                        }
                        Box(
                            modifier = Modifier.size(56.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.BarChart, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(28.dp))
                        }
                    }
                    // Decorative blob
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 40.dp, y = 40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.08f))
                    )
                }
            }
        }

        // ── Inventory Status ─────────────────────────────────────────────────
        item {
            SectionHeader(t.inventoryTitle)
        }

        if (lowStockAlerts.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = if(isSystemInDarkTheme()) Color(0xFF064E3B) else Color(0xFFF0FDF4)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                                .background(if(isSystemInDarkTheme()) Color(0xFF065F46) else Color(0xFFBBF7D0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, null,
                                tint = if(isSystemInDarkTheme()) Color(0xFF34D399) else Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                        }
                        Text("All products have sufficient stock.",
                            style = MaterialTheme.typography.bodySmall,
                            color = if(isSystemInDarkTheme()) Color(0xFFD1FAE5) else Color(0xFF15803D))
                    }
                }
            }
        } else {
            items(lowStockAlerts) { (product, colorName, stock) ->
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if(isSystemInDarkTheme()) Color(0xFF450A0A) else Color(0xFFFFF1F2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape)
                                .background(ColorDanger.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Warning, null,
                                tint = ColorDanger, modifier = Modifier.size(24.dp))
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val prodName = t.localizeProduct(product.name)
                            val pluralSuffix = if (lang == Language.EN && stock > 1 && !prodName.endsWith("s", ignoreCase = true)) "s" else ""
                            
                            Text(
                                text = if (stock == 0)
                                    "${t.localizeColor(colorName)} $prodName is out of stock"
                                else
                                    "Only $stock ${t.localizeColor(colorName)} $prodName$pluralSuffix left",
                                style = MaterialTheme.typography.titleMedium,
                                color = if(isSystemInDarkTheme()) Color(0xFFFECACA) else Color(0xFF9F1239),
                                fontWeight = FontWeight.Bold
                            )
                            if (stock > 0) {
                                Text(
                                    text = t.timeToMake,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if(isSystemInDarkTheme()) Color(0xFFFECACA).copy(alpha = 0.7f) else Color(0xFF9F1239).copy(alpha = 0.7f)
                                )
                            }
                        }
                        
                        // Restock Button
                        Button(
                            onClick = { restockTarget = Triple(product, colorName, stock) },
                            colors = ButtonDefaults.buttonColors(containerColor = ArtisanClay),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(t.restock, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }

    // Restock Dialog
    if (restockTarget != null) {
        RestockDialog(
            target = restockTarget!!,
            t = t,
            onDismiss = { restockTarget = null },
            onConfirm = { product, color, amount ->
                val newVariations = product.variations.toMutableMap()
                newVariations[color] = (newVariations[color] ?: 0) + amount
                onUpdateProduct(product.copy(variations = newVariations))
                restockTarget = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RestockDialog(
    target: Triple<Product, String, Int>,
    t: Translations,
    onDismiss: () -> Unit,
    onConfirm: (Product, String, Int) -> Unit
) {
    var quantity by remember { mutableStateOf("10") }
    val (product, colorName, _) = target

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(32.dp), color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier.padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${t.addStock}: ${t.localizeColor(colorName)} ${t.localizeProduct(product.name)}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text(t.quantity) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(0.1f)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(t.cancel, color = Color.Gray)
                    }
                    Button(
                        onClick = { 
                            val amount = quantity.toIntOrNull() ?: 0
                            if (amount > 0) onConfirm(product, colorName, amount)
                        },
                        enabled = quantity.toIntOrNull() != null && (quantity.toIntOrNull() ?: 0) > 0,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ArtisanClay),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(t.done)
                    }
                }
            }
        }
    }
}
