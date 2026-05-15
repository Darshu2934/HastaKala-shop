package com.hastakala.app.data

import android.content.Context
import android.content.SharedPreferences
import com.hastakala.app.data.room.AppDatabase
import kotlinx.coroutines.flow.Flow

class AppRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("hastakala_prefs", Context.MODE_PRIVATE)
    
    private val dao = AppDatabase.getDatabase(context).appDao()

    companion object {
        private const val KEY_LANGUAGE = "app_lang"
        private const val KEY_THEME = "app_theme_mode"

        @Volatile
        private var INSTANCE: AppRepository? = null

        fun getInstance(context: Context): AppRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // ─── Language ───────────────────────────────────────────────────────────────

    fun getLanguage(): Language {
        val code = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        return Language.entries.find { it.code == code } ?: Language.EN
    }

    fun saveLanguage(lang: Language) {
        prefs.edit().putString(KEY_LANGUAGE, lang.code).apply()
    }

    // ─── Theme ──────────────────────────────────────────────────────────────────

    fun getThemeMode(): AppThemeMode {
        val name = prefs.getString(KEY_THEME, AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        return try { AppThemeMode.valueOf(name) } catch (e: Exception) { AppThemeMode.SYSTEM }
    }

    fun saveThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME, mode.name).apply()
    }

    // ─── Products ────────────────────────────────────────────────────────────────

    fun getAllProducts(): Flow<List<Product>> = dao.getAllProducts()

    suspend fun saveProduct(product: Product) {
        dao.insertProduct(product)
    }

    suspend fun updateProduct(updated: Product) {
        dao.updateProduct(updated)
    }

    suspend fun deleteProduct(id: String) {
        dao.deleteProduct(id)
        dao.deleteSalesByProductId(id)
    }

    // ─── Sales ───────────────────────────────────────────────────────────────────

    fun getAllSales(): Flow<List<Sale>> = dao.getAllSales()

    suspend fun saveSale(sale: Sale) {
        dao.insertSale(sale)

        // Decrement stock
        val product = dao.getProductById(sale.productId)
        if (product != null) {
            val variations = product.variations.toMutableMap()
            val current = variations[sale.colorName] ?: 0
            variations[sale.colorName] = maxOf(0, current - 1)
            dao.updateProduct(product.copy(variations = variations))
        }
    }

    suspend fun deleteSale(id: String) {
        val sale = dao.getSaleById(id)
        dao.deleteSale(id)

        // Restore stock
        if (sale != null) {
            val product = dao.getProductById(sale.productId)
            if (product != null) {
                val variations = product.variations.toMutableMap()
                val current = variations[sale.colorName] ?: 0
                variations[sale.colorName] = current + 1
                dao.updateProduct(product.copy(variations = variations))
            }
        }
    }

    // ─── Initial Setup ──────────────────────────────────────────────────────────

    suspend fun clearAllData() {
        // Optional helper for the user to reset their local database if needed
    }
}
