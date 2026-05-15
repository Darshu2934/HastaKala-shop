package com.hastakala.app.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hastakala.app.data.*
import com.hastakala.app.ui.components.*
import com.hastakala.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class SaleFilter { ALL, WEEK, MONTH }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(
    sales: List<Sale>,
    products: List<Product>,
    lang: Language,
    onDelete: (String) -> Unit
) {
    val t = getTranslations(lang)
    var filter by remember { mutableStateOf(SaleFilter.ALL) }

    val filteredSales = remember(sales, filter) {
        val filtered = when (filter) {
            SaleFilter.ALL -> sales
            SaleFilter.WEEK -> {
                val startOfWeek = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
                }.timeInMillis
                sales.filter { it.date >= startOfWeek }
            }
            SaleFilter.MONTH -> {
                val startOfMonth = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
                }.timeInMillis
                sales.filter { it.date >= startOfMonth }
            }
        }
        filtered.sortedByDescending { it.date }
    }

    val totalIncome = filteredSales.sumOf { it.price }

    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp, ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(t.income, style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
                    Text(t.history, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
                // Filter dropdown
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(0.1f), CircleShape)
                            .clickable { expanded = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.FilterList, null, tint = MaterialTheme.colorScheme.primary.copy(0.6f), modifier = Modifier.size(14.dp))
                        Text(
                            when (filter) {
                                SaleFilter.ALL -> t.filterAll
                                SaleFilter.WEEK -> t.filterWeek
                                SaleFilter.MONTH -> t.filterMonth
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary.copy(0.6f)
                        )
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text(t.filterAll) },
                            onClick = { filter = SaleFilter.ALL; expanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text(t.filterWeek) },
                            onClick = { filter = SaleFilter.WEEK; expanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text(t.filterMonth) },
                            onClick = { filter = SaleFilter.MONTH; expanded = false }
                        )
                    }
                }
            }
        }

        // ── Total Income Banner ──────────────────────────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = ArtisanClay),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(t.totalIncome.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(0.8f))
                        Text("₹${String.format("%,.0f", totalIncome)}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Color.White, fontWeight = FontWeight.Normal
                            ))
                    }
                    Box(
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CalendarToday, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }

        // ── Transaction List ─────────────────────────────────────────────────
        item { SectionHeader(t.transactionHistory) }

        if (filteredSales.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(t.noSales,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray))
                }
            }
        } else {
            items(filteredSales, key = { it.id }) { sale ->
                val product = products.find { it.id == sale.productId }
                val iconName = product?.iconName ?: "ShoppingBag"

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                ProductIcon(iconName, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    getTranslations(lang).localizeProduct(sale.productName),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    ColorDot(sale.colorHex, size = 10)
                                    Text(
                                        "${getTranslations(lang).localizeColor(sale.colorName)} • ${dateFormat.format(Date(sale.date))}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "₹${sale.price.toLong()}",
                                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary)
                            )
                            IconButton(
                                onClick = { onDelete(sale.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, null,
                                    tint = Color.Gray.copy(0.5f), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(64.dp)) }
    }
}
