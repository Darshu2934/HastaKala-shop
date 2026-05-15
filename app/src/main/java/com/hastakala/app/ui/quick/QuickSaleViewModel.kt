package com.hastakala.app.ui.quick

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hastakala.app.data.AppRepository
import com.hastakala.app.data.ColorOption
import com.hastakala.app.data.EXTENDED_COLORS
import com.hastakala.app.data.PREDEFINED_COLORS
import com.hastakala.app.data.Product
import com.hastakala.app.data.Sale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class QuickSaleUiState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val selectedProductId: String? = null,
    val selectedVariantName: String? = null,
    val quantity: Int = 1,
    /** Raw text for unit price (₹) entry — parsed on complete. */
    val unitPriceInput: String = "",
    val isSaving: Boolean = false,
    val successForSnackbar: Boolean = false,
    val errorMessage: String? = null,
) {
    val selectedProduct: Product?
        get() = products.find { it.id == selectedProductId }

    val filteredProducts: List<Product>
        get() {
            val q = searchQuery.trim().lowercase()
            if (q.isEmpty()) return products
            return products.filter { it.name.lowercase().contains(q) }
        }

    val variantNames: List<String>
        get() = selectedProduct?.variations?.keys?.sorted().orEmpty()

    val unitPrice: Double
        get() = unitPriceInput.replace(",", ".").toDoubleOrNull() ?: 0.0

    val lineTotal: Double
        get() = unitPrice * quantity.coerceAtLeast(1)

    val availableStock: Int
        get() {
            val p = selectedProduct ?: return 0
            val v = selectedVariantName ?: return 0
            return p.variations[v] ?: 0
        }
}

fun colorHexForVariant(variantName: String): String {
    val all: List<ColorOption> = PREDEFINED_COLORS + EXTENDED_COLORS
    return all.find { it.name.equals(variantName, ignoreCase = true) }?.hex ?: "#5C4033"
}

class QuickSaleViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AppRepository.getInstance(application)

    private val _uiState = MutableStateFlow(QuickSaleUiState())
    val uiState: StateFlow<QuickSaleUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repo.getAllProducts().collect { list ->
                _uiState.update { prev ->
                    val stillValid = prev.selectedProductId?.let { id -> list.any { it.id == id } } == true
                    val newId = if (stillValid) prev.selectedProductId else null
                    val p = list.find { it.id == newId }
                    val variants = p?.variations?.keys?.sorted().orEmpty()
                    val variant = when {
                        newId == null -> null
                        variants.size == 1 -> variants[0]
                        prev.selectedVariantName != null && variants.contains(prev.selectedVariantName) ->
                            prev.selectedVariantName
                        else -> null
                    }
                    prev.copy(
                        products = list,
                        selectedProductId = newId,
                        selectedVariantName = variant,
                    )
                }
            }
        }
    }

    fun setSearchQuery(q: String) = _uiState.update { it.copy(searchQuery = q) }

    fun selectProduct(productId: String?) {
        val p = _uiState.value.products.find { it.id == productId }
        val keys = p?.variations?.keys?.sorted().orEmpty()
        val autoVariant = if (keys.size == 1) keys[0] else null
        _uiState.update {
            it.copy(
                selectedProductId = productId,
                selectedVariantName = autoVariant,
                quantity = 1,
                errorMessage = null,
            )
        }
    }

    fun selectVariant(name: String?) {
        _uiState.update { it.copy(selectedVariantName = name, quantity = 1, errorMessage = null) }
    }

    fun setQuantity(q: Int) {
        val clamped = q.coerceIn(1, 9_999)
        _uiState.update { it.copy(quantity = clamped) }
    }

    fun bumpQuantity(delta: Int) {
        _uiState.update { it.copy(quantity = (it.quantity + delta).coerceIn(1, 9_999)) }
    }

    fun setUnitPriceInput(text: String) {
        val filtered = text.filter { it.isDigit() || it == '.' || it == ',' }
        _uiState.update { it.copy(unitPriceInput = filtered, errorMessage = null) }
    }

    fun clearTransientMessages() {
        _uiState.update { it.copy(successForSnackbar = false, errorMessage = null) }
    }

    /**
     * Persists [quantity] unit [Sale] rows (existing Room model = one row per unit).
     * Invokes [onSuccess] on the main thread after DB work (for snackbar + focus).
     */
    fun completeSale(
        productName: String,
        strings: QuickSaleStrings,
        onSuccess: () -> Unit,
    ) {
        val s = _uiState.value
        val product = s.selectedProduct
        val variant = s.selectedVariantName
        if (product == null) {
            _uiState.update { it.copy(errorMessage = strings.selectProduct) }
            return
        }
        if (variant == null) {
            _uiState.update { it.copy(errorMessage = strings.selectVariant) }
            return
        }
        val unit = s.unitPrice
        if (unit <= 0) {
            _uiState.update { it.copy(errorMessage = strings.invalidPrice) }
            return
        }
        val qty = s.quantity
        val stock = product.variations[variant] ?: 0
        if (stock < qty) {
            _uiState.update { it.copy(errorMessage = strings.insufficientStock(stock)) }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val hex = colorHexForVariant(variant)
                repeat(qty) {
                    repo.saveSale(
                        Sale(
                            productId = product.id,
                            productName = productName,
                            colorName = variant,
                            colorHex = hex,
                            price = unit,
                        ),
                    )
                }
                _uiState.update {
                    it.copy(
                        searchQuery = "",
                        selectedProductId = null,
                        selectedVariantName = null,
                        quantity = 1,
                        unitPriceInput = "",
                        isSaving = false,
                        successForSnackbar = true,
                        errorMessage = null,
                    )
                }
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message ?: strings.saveFailed,
                    )
                }
            }
        }
    }
}

/** Localized validation strings passed from Composable (keeps ViewModel free of Language). */
data class QuickSaleStrings(
    val selectProduct: String,
    val selectVariant: String,
    val invalidPrice: String,
    val insufficientStock: (Int) -> String,
    val saveFailed: String,
)
