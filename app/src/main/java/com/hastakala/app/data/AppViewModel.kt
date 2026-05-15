package com.hastakala.app.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AppRepository.getInstance(application)

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _sales = MutableStateFlow<List<Sale>>(emptyList())
    val sales: StateFlow<List<Sale>> = _sales.asStateFlow()

    private val _language = MutableStateFlow(Language.EN)
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                repo.getAllProducts().collect {
                    _products.value = it
                }
            }
            
            launch {
                repo.getAllSales().collect {
                    _sales.value = it
                }
            }
            
            _language.value = repo.getLanguage()
            _themeMode.value = repo.getThemeMode()
        }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            repo.saveProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repo.updateProduct(product)
        }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            repo.deleteProduct(id)
        }
    }

    fun saveSale(sale: Sale) {
        viewModelScope.launch {
            repo.saveSale(sale)
        }
    }

    fun deleteSale(id: String) {
        viewModelScope.launch {
            repo.deleteSale(id)
        }
    }

    fun setLanguage(lang: Language) {
        _language.value = lang
        repo.saveLanguage(lang)
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        repo.saveThemeMode(mode)
    }

    fun getProductById(id: String) = _products.value.find { it.id == id }
}
