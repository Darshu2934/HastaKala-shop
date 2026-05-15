package com.hastakala.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hastakala.app.BuildConfig
import com.hastakala.app.data.*
import com.hastakala.app.ui.components.BottomNavBar
import com.hastakala.app.ui.quick.QuickSaleViewModel
import com.hastakala.app.ui.screens.*
import com.hastakala.app.ui.theme.HastaKalaTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()
    private val quickSaleViewModel: QuickSaleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val useDarkTheme = when (themeMode) {
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            HastaKalaTheme(darkTheme = useDarkTheme) {
                HastaKalaApp(viewModel, quickSaleViewModel)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HastaKalaApp(
    viewModel: AppViewModel,
    quickSaleViewModel: QuickSaleViewModel,
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val sales    by viewModel.sales.collectAsStateWithLifecycle()
    val lang     by viewModel.language.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    var activeTab  by remember { mutableStateOf("home") }
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2500)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen(lang = lang)
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavBar(
                activeTab   = activeTab,
                onTabSelect = { activeTab = it },
                lang        = lang
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = activeTab,
            transitionSpec = {
                (fadeIn(animationSpec = tween(220)) +
                        slideInHorizontally(animationSpec = tween(220)) { it / 10 })
                    .togetherWith(
                        fadeOut(animationSpec = tween(220)) +
                                slideOutHorizontally(animationSpec = tween(220)) { -it / 10 }
                    )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "screen"
        ) { tab ->
            when (tab) {
                "home" -> HomeScreen(
                    sales           = sales,
                    products        = products,
                    lang            = lang,
                    onNavigate      = { activeTab = it },
                    onUpdateProduct = viewModel::updateProduct
                )
                "quick" -> QuickSaleScreen(
                    viewModel = quickSaleViewModel,
                    lang = lang,
                )
                "add" -> AddSaleScreen(
                    products        = products,
                    lang            = lang,
                    onSave          = { sale -> viewModel.saveSale(sale); activeTab = "home" },
                    onAddProduct    = viewModel::saveProduct,
                    onDeleteProduct = viewModel::deleteProduct,
                    onUpdateProduct = viewModel::updateProduct
                )
                "analytics" -> AnalyticsScreen(
                    sales    = sales,
                    products = products,
                    lang     = lang
                )
                "income" -> IncomeScreen(
                    sales     = sales,
                    products  = products,
                    lang      = lang,
                    onDelete  = viewModel::deleteSale
                )
                "ai" -> AiSuggestScreen(
                    apiBaseUrl = BuildConfig.SUGGEST_API_BASE_URL,
                    lang = lang,
                )
                "settings" -> SettingsScreen(
                    currentLang        = lang,
                    onLanguageChange   = viewModel::setLanguage,
                    themeMode          = themeMode,
                    onThemeModeChange  = viewModel::setThemeMode,
                )
                else -> HomeScreen(
                    sales           = sales,
                    products        = products,
                    lang            = lang,
                    onNavigate      = { activeTab = it },
                    onUpdateProduct = viewModel::updateProduct
                )
            }
        }
    }
}
