package com.hastakala.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val iconName: String = "ShoppingBag",
    val variations: Map<String, Int> = emptyMap() // colorName -> stock
)

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val productId: String = "",
    val productName: String = "",
    val colorName: String = "",
    val colorHex: String = "#ef4444",
    val price: Double = 0.0,
    val date: Long = System.currentTimeMillis()
)

data class ProductStats(
    val name: String,
    val totalSales: Int,
    val totalRevenue: Double
)

enum class Language(val code: String, val label: String, val sub: String) {
    EN("en", "English", "English"),
    HI("hi", "हिन्दी", "Hindi"),
    KN("kn", "ಕನ್ನಡ", "Kannada")
}

data class ColorOption(val name: String, val hex: String)

val PREDEFINED_COLORS = listOf(
    ColorOption("Red", "#ef4444"),
    ColorOption("Blue", "#3b82f6"),
    ColorOption("Yellow", "#eab308"),
    ColorOption("Green", "#22c55e"),
    ColorOption("Black", "#18181b"),
    ColorOption("White", "#ffffff"),
)

val EXTENDED_COLORS = listOf(
    ColorOption("Orange", "#f97316"),
    ColorOption("Purple", "#8b5cf6"),
    ColorOption("Pink", "#ec4899"),
    ColorOption("Cyan", "#06b6d4"),
    ColorOption("Gray", "#71717a"),
    ColorOption("Clay", "#A67C52"),
)

val ICON_OPTIONS = listOf(
    "ShoppingBag", "Key", "Palette", "Gift", "Flower",
    "Gem", "Brush", "Heart", "Star", "ShoppingCart"
)
