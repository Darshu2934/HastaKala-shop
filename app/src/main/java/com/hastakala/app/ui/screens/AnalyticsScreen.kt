package com.hastakala.app.ui.screens

import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.hastakala.app.data.*
import com.hastakala.app.ui.components.*
import com.hastakala.app.ui.theme.*

@Composable
fun AnalyticsScreen(
    sales: List<Sale>,
    products: List<Product>,
    lang: Language,
) {
    val t = getTranslations(lang)

    val CHART_COLORS = listOf(
        ArtisanOlive.toArgb(),
        ArtisanClay.toArgb(),
        ArtisanSand.toArgb(),
        MaterialTheme.colorScheme.primaryContainer.toArgb(),
        Color(0xFF8C7460).toArgb()
    )

    // ── Derived data ──────────────────────────────────────────────────────────
    val productDist = mutableMapOf<String, Int>()
    sales.forEach { s ->
        val name = t.localizeProduct(s.productName)
        productDist[name] = (productDist[name] ?: 0) + 1
    }

    val variationDist = mutableMapOf<String, Int>()
    sales.forEach { s ->
        val key = "${t.localizeColor(s.colorName)} ${t.localizeProduct(s.productName)}"
        variationDist[key] = (variationDist[key] ?: 0) + 1
    }
    val top5 = variationDist.entries.sortedByDescending { it.value }.take(5)

    val revByProduct = mutableMapOf<String, Double>()
    sales.forEach { s ->
        val name = t.localizeProduct(s.productName)
        revByProduct[name] = (revByProduct[name] ?: 0.0) + s.price
    }

    val salesByProductId = mutableMapOf<String, Int>()
    sales.forEach { s -> salesByProductId[s.productId] = (salesByProductId[s.productId] ?: 0) + 1 }

    val totalSales = sales.size
    val avgSales = if (products.isNotEmpty()) totalSales.toFloat() / products.size else 0f

    data class ProductInsight(
        val product: Product,
        val salesCount: Int,
        val isFast: Boolean,
        val isLow: Boolean
    )

    val maxProductSales = salesByProductId.values.maxOrNull() ?: 0
    val insights = products.map { p ->
        val count = salesByProductId[p.id] ?: 0
        ProductInsight(
            product    = p,
            salesCount = count,
            isFast     = (count == maxProductSales && maxProductSales > 0) || (count > avgSales * 1.5f && totalSales > 5),
            isLow      = count < avgSales * 0.7f && totalSales > 3 && count < maxProductSales
        )
    }
    val fastMoving = insights.filter { it.isFast }
    val slowMoving = insights.filter { it.isLow && !it.isFast }

    // ── UI ─────────────────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header card
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(t.analyticsTitle,
                        style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onSurface)
                    Text(t.growth,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                }
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Insights, null,
                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Empty state
        if (sales.isEmpty()) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.padding(40.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.BarChart, null,
                            tint = Color.Gray.copy(0.3f), modifier = Modifier.size(48.dp))
                        Text("Record some sales to see analytics here.",
                            style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }
            }
            return@Column
        }

        // Fast Moving
        InsightCard(
            title = t.fastMoving,
            icon = Icons.Default.TrendingUp,
            iconBg = if(isSystemInDarkTheme()) Color(0xFF064E3B) else Color(0xFFDCFCE7),
            iconTint = if(isSystemInDarkTheme()) Color(0xFF34D399) else Color(0xFF16A34A),
            emptyText = t.performanceGood
        ) {
            fastMoving.forEach { insight ->
                AnalyticsProductRow(
                    product      = insight.product,
                    salesCount   = insight.salesCount,
                    lang         = lang,
                    badgeText    = "Fast Selling",
                    badgeColor   = if(isSystemInDarkTheme()) Color(0xFF34D399) else Color(0xFF16A34A),
                    badgeBg      = if(isSystemInDarkTheme()) Color(0xFF064E3B) else Color(0xFFDCFCE7),
                    salesColor   = if(isSystemInDarkTheme()) Color(0xFF34D399) else Color(0xFF16A34A),
                    t            = t
                )
            }
        }

        // Slow Moving
        if (slowMoving.isNotEmpty()) {
            InsightCard(
                title    = t.lowSelling,
                icon     = Icons.Default.TrendingDown,
                iconBg   = if(isSystemInDarkTheme()) Color(0xFF431407) else Color(0xFFFFF7ED),
                iconTint = ColorWarning,
                emptyText = t.noLowSelling
            ) {
                slowMoving.forEach { insight ->
                    AnalyticsProductRow(
                        product    = insight.product,
                        salesCount = insight.salesCount,
                        lang       = lang,
                        badgeText  = "Low Selling",
                        badgeColor = ColorWarning,
                        badgeBg    = if(isSystemInDarkTheme()) Color(0xFF431407) else Color(0xFFFFF7ED),
                        salesColor = ColorWarning,
                        t          = t
                    )
                }
            }
        }

        // Top 5 Variations (PieChart)
        if (top5.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(t.bestSellingVariations.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary.copy(0.6f))
                    
                    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()
                    AndroidView(
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        factory = { context ->
                            PieChart(context).apply {
                                description.isEnabled = false
                                isDrawHoleEnabled = false
                                holeRadius = 50f
                                transparentCircleRadius = 55f
                                setDrawEntryLabels(false)
                                setUsePercentValues(true)
                                legend.apply {
                                    isWordWrapEnabled = true
                                    horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
                                    textColor = onSurfaceColor
                                }
                            }
                        },
                        update = { pieChart ->
                            val entries = top5.map { PieEntry(it.value.toFloat(), it.key) }
                            val dataSet = PieDataSet(entries, "").apply {
                                colors = CHART_COLORS
                                valueTextSize = 12f
                                valueTextColor = android.graphics.Color.WHITE
                                valueTypeface = Typeface.DEFAULT_BOLD
                                sliceSpace = 2f
                            }
                            pieChart.data = PieData(dataSet)
                            pieChart.legend.textColor = onSurfaceColor
                            pieChart.invalidate()
                        }
                    )
                }
            }
        }

        // Revenue by product (BarChart)
        if (revByProduct.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(t.revenue.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary.copy(0.6f))

                    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()
                    AndroidView(
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        factory = { context ->
                            BarChart(context).apply {
                                description.isEnabled = false
                                legend.isEnabled = false
                                setDrawGridBackground(false)
                                setDrawBorders(false)
                                
                                xAxis.apply {
                                    position = XAxis.XAxisPosition.BOTTOM
                                    setDrawGridLines(false)
                                    granularity = 1f
                                    textColor = onSurfaceColor
                                }
                                
                                axisLeft.apply {
                                    setDrawGridLines(true)
                                    axisMinimum = 0f
                                    textColor = onSurfaceColor
                                }
                                axisRight.isEnabled = false
                            }
                        },
                        update = { barChart ->
                            val labels = revByProduct.keys.toList()
                            val entries = revByProduct.values.mapIndexed { index, value ->
                                BarEntry(index.toFloat(), value.toFloat())
                            }
                            
                            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                            barChart.xAxis.textColor = onSurfaceColor
                            barChart.axisLeft.textColor = onSurfaceColor
                            
                            val dataSet = BarDataSet(entries, "Revenue").apply {
                                colors = CHART_COLORS
                                valueTextSize = 10f
                                valueTextColor = onSurfaceColor
                            }
                            
                            barChart.data = BarData(dataSet).apply {
                                barWidth = 0.6f
                            }
                            barChart.invalidate()
                        }
                    )
                }
            }
        }
    }
}

// ── Reusable insight card wrapper ─────────────────────────────────────────────
@Composable
private fun InsightCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    emptyText: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(iconBg),
                    contentAlignment = Alignment.Center
                ) { Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp)) }
                Text(title.uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
            }
            content()
            if (content.toString().isEmpty()) {
                Text(emptyText, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

// ── Product row inside insight card ───────────────────────────────────────────
@Composable
private fun AnalyticsProductRow(
    product: Product,
    salesCount: Int,
    lang: Language,
    badgeText: String,
    badgeColor: Color,
    badgeBg: Color,
    salesColor: Color,
    t: Translations
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(badgeBg.copy(0.35f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                ProductIcon(product.iconName, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(t.localizeProduct(product.name),
                        style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                    StatusBadge(badgeText, badgeColor, badgeBg)
                }
                Text("${t.stock}: ${product.variations.values.sum()}",
                    style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("$salesCount",
                style = MaterialTheme.typography.titleMedium.copy(color = salesColor))
            Text(t.salesCount,
                style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}
