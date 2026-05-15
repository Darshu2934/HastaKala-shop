package com.hastakala.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hastakala.app.data.*
import com.hastakala.app.ui.components.ProductIcon
import com.hastakala.app.ui.quick.QuickSaleStrings
import com.hastakala.app.ui.quick.QuickSaleViewModel
import com.hastakala.app.ui.theme.ArtisanClay
import com.hastakala.app.ui.theme.ArtisanOlive
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * High-speed checkout: product grid + variant chips + quantity + live total + complete.
 * State lives in [QuickSaleViewModel]; each completed sale writes multiple [com.hastakala.app.data.Sale] rows.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuickSaleScreen(
    viewModel: QuickSaleViewModel,
    lang: Language,
) {
    val t = getTranslations(lang)
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val quantityFocus = remember { FocusRequester() }

    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000)
            nowMillis = System.currentTimeMillis()
        }
    }
    val dateFmt = remember(lang) {
        SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault())
    }

    val strings = remember(t) {
        QuickSaleStrings(
            selectProduct = t.quickSaleSelectProduct,
            selectVariant = t.quickSaleSelectVariant,
            invalidPrice = t.quickSaleInvalidPrice,
            insufficientStock = { n -> String.format(t.quickSaleInsufficientStock, n) },
            saveFailed = t.quickSaleSaveFailed,
        )
    }

    LaunchedEffect(ui.selectedProductId, ui.selectedVariantName) {
        if (ui.selectedProduct != null && ui.selectedVariantName != null) {
            delay(100)
            quantityFocus.requestFocus()
        }
    }

    LaunchedEffect(ui.successForSnackbar) {
        if (ui.successForSnackbar) {
            try {
                snackbarHostState.showSnackbar(t.quickSaleSuccess)
            } finally {
                viewModel.clearTransientMessages()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = t.quickSaleTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = dateFmt.format(Date(nowMillis)),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val p = ui.selectedProduct ?: return@Button
                    val localized = t.localizeProduct(p.name)
                    viewModel.completeSale(localized, strings) {
                        quantityFocus.requestFocus()
                    }
                },
                enabled = !ui.isSaving &&
                    ui.selectedProduct != null &&
                    ui.selectedVariantName != null &&
                    ui.unitPrice > 0 &&
                    ui.quantity <= ui.availableStock,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ArtisanOlive,
                    contentColor = Color.White,
                ),
            ) {
                if (ui.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(t.quickSaleComplete, style = MaterialTheme.typography.titleMedium)
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Search
            OutlinedTextField(
                value = ui.searchQuery,
                onValueChange = viewModel::setSearchQuery,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(t.quickSaleSearchHint) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ArtisanOlive,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                ),
            )

            Text(
                t.quickSalePickupProduct,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (ui.products.isEmpty()) {
                Text(
                    t.quickSaleNoProducts,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.heightIn(max = 280.dp),
                    userScrollEnabled = true,
                ) {
                    items(ui.filteredProducts, key = { it.id }) { p ->
                        val selected = ui.selectedProductId == p.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) ArtisanOlive else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(20.dp),
                                )
                                .clickable { viewModel.selectProduct(p.id) },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selected) {
                                    ArtisanOlive.copy(alpha = 0.12f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    ProductIcon(
                                        p.iconName,
                                        tint = if (selected) ArtisanOlive else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(26.dp),
                                    )
                                }
                                Text(
                                    t.localizeProduct(p.name),
                                    style = MaterialTheme.typography.titleSmall,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                )
                            }
                        }
                    }
                }
            }

            // Variants
            if (ui.selectedProduct != null) {
                Text(
                    t.quickSaleDesign,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (ui.variantNames.isEmpty()) {
                    Text(
                        t.quickSaleNoVariants,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ui.variantNames.forEach { name ->
                            val picked = ui.selectedVariantName == name
                            FilterChip(
                                selected = picked,
                                onClick = { viewModel.selectVariant(name) },
                                label = { Text(t.localizeColor(name)) },
                            )
                        }
                    }
                }
            }

            // Unit price
            OutlinedTextField(
                value = ui.unitPriceInput,
                onValueChange = viewModel::setUnitPriceInput,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(t.quickSaleUnitPrice) },
                placeholder = { Text("0") },
                prefix = { Text("₹ ", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ArtisanOlive,
                ),
            )

            // Quantity stepper + field
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(t.quantity, style = MaterialTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        IconButton(
                            onClick = { viewModel.bumpQuantity(-1) },
                            enabled = ui.quantity > 1,
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = null)
                        }
                        OutlinedTextField(
                            value = ui.quantity.toString(),
                            onValueChange = { raw ->
                                raw.filter { it.isDigit() }.take(4).toIntOrNull()?.let { viewModel.setQuantity(it) }
                            },
                            modifier = Modifier
                                .width(120.dp)
                                .focusRequester(quantityFocus),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.headlineMedium,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                        )
                        IconButton(onClick = { viewModel.bumpQuantity(1) }) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                        }
                    }
                }
            }

            // Live total — serif for premium feel
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                ),
                elevation = CardDefaults.cardElevation(0.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        t.quickSaleTotalLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "₹%.2f", ui.lineTotal),
                        fontFamily = FontFamily.Serif,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Medium,
                        color = ArtisanOlive,
                    )
                    if (ui.selectedVariantName != null) {
                        Text(
                            "${t.stock}: ${ui.availableStock}",
                            style = MaterialTheme.typography.labelSmall,
                            color = ArtisanClay,
                        )
                    }
                }
            }

            if (ui.errorMessage != null) {
                Text(
                    text = ui.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(Modifier.height(72.dp))
        }
    }
}
