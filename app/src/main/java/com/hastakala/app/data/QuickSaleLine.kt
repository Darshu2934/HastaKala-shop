package com.hastakala.app.data

import java.util.UUID

/**
 * Represents one quick-billing line for UI and totals. Maps to multiple [Sale] rows
 * (one per unit) when persisted via [AppRepository.saveSale].
 */
data class QuickSaleLine(
    val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val variantName: String,
    val quantity: Int,
    val totalPrice: Double,
    val timestamp: Long = System.currentTimeMillis(),
)
