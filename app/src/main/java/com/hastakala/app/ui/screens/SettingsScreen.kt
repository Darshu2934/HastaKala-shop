package com.hastakala.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hastakala.app.data.*
import com.hastakala.app.ui.components.*
import com.hastakala.app.ui.theme.*

@Composable
fun SettingsScreen(
    currentLang: Language,
    onLanguageChange: (Language) -> Unit,
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
) {
    val t = getTranslations(currentLang)
    var showGuide by remember { mutableStateOf(false) }

    val systemDark = isSystemInDarkTheme()
    val effectiveDark = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> systemDark
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        Column {
            Text(t.settingsTitle, style = MaterialTheme.typography.displayLarge, color = ArtisanOlive)
            Text(t.about, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // ── Appearance (theme) ─────────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(t.themeSection)
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                ThemeToggle(
                    checked = effectiveDark,
                    onCheckedChange = { wantDark ->
                        onThemeModeChange(if (wantDark) AppThemeMode.DARK else AppThemeMode.LIGHT)
                    },
                    label = t.themeDarkLabel,
                    supportingText = t.themeDarkSupporting,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                )
            }
        }

        // ── Language Selection ───────────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(t.language)
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Language.values().forEachIndexed { idx, lang ->
                    val isSelected = lang == currentLang
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageChange(lang) }
                            .background(if (isSelected) ArtisanOlive.copy(0.05f) else Color.Transparent)
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) ArtisanOlive else Color(0xFFF0F0F0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Language, null,
                                    tint = if (isSelected) Color.White else Color.Gray,
                                    modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(lang.label, style = MaterialTheme.typography.titleMedium, color = ArtisanInk)
                                Text(lang.sub.uppercase(),
                                    style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                        if (isSelected) {
                            Box(
                                modifier = Modifier.size(24.dp).clip(CircleShape).background(ArtisanOlive),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    if (idx < Language.values().size - 1) {
                        Divider(color = Color.Black.copy(0.04f), modifier = Modifier.padding(horizontal = 20.dp))
                    }
                }
            }
        }

        // ── App & Support ────────────────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(t.appSupport)
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // User Guide
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showGuide = true }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape).background(ArtisanCream),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.HelpOutline, null, tint = ArtisanOlive, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(t.guide, style = MaterialTheme.typography.titleMedium, color = ArtisanInk)
                            Text(t.guideDesc, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = Color.Gray.copy(0.4f))
                }

                Divider(color = Color.Black.copy(0.04f), modifier = Modifier.padding(horizontal = 20.dp))

                // Version Info
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFF0F0F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Info, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(t.artisanApp, style = MaterialTheme.typography.titleMedium, color = ArtisanInk)
                        Text(t.version, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }
        }

        // ── Footer ───────────────────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
            Text(t.madeFor.uppercase(),
                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }

    // ── User Guide Dialog ─────────────────────────────────────────────────────
    if (showGuide) {
        Dialog(onDismissRequest = { showGuide = false }) {
            Surface(shape = RoundedCornerShape(36.dp), color = Color.White) {
                Column(
                    modifier = Modifier.padding(28.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HelpOutline, null, tint = ArtisanOlive)
                            Text(t.guide, style = MaterialTheme.typography.headlineMedium, color = ArtisanOlive)
                        }
                        IconButton(onClick = { showGuide = false }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                    HorizontalDivider()

                    data class GuideItem(val icon: androidx.compose.ui.graphics.vector.ImageVector, val iconColor: Color, val title: String, val desc: String)

                    val items = listOf(
                        GuideItem(Icons.Default.AutoAwesome, ArtisanOlive,
                            if (currentLang == Language.HI) "बिक्री दर्ज करना" else if (currentLang == Language.KN) "ಮಾರಾಟ ದಾಖಲಿಸುವುದು" else "Recording Sales",
                            if (currentLang == Language.HI) "\"बिक्री जोड़ें\" पर क्लिक करें, उत्पाद और रंग चुनें।" else if (currentLang == Language.KN) "\"ಮಾರಾಟ ಸೇರಿಸಿ\" ಕ್ಲಿಕ್ ಮಾಡಿ, ಉತ್ಪನ್ನ ಮತ್ತು ಬಣ್ಣ ಆಯ್ಕೆಮಾಡಿ." else "Tap 'Add Sale', select your product and color, enter the price."
                        ),
                        GuideItem(Icons.Default.Inventory, ArtisanClay,
                            if (currentLang == Language.HI) "स्टॉक प्रबंधित करना" else if (currentLang == Language.KN) "ದಾಸ್ತಾನು ನಿರ್ವಹಣೆ" else "Managing Stock",
                            if (currentLang == Language.HI) "नई श्रेणियाँ बनाने के लिए 'नया जोड़ें' बटन का उपयोग करें।" else if (currentLang == Language.KN) "ಹೊಸ ವರ್ಗಗಳನ್ನು ರಚಿಸಲು 'ಹೊಸದು ಸೇರಿಸಿ' ಬಟನ್ ಬಳಸಿ." else "Use 'Add New' in the Sale screen to create products and set initial stock."
                        ),
                        GuideItem(Icons.Default.TrendingUp, ArtisanOlive,
                            if (currentLang == Language.HI) "व्यवसाय की जानकारी" else if (currentLang == Language.KN) "ವ್ಯಾಪಾರ ಮಾಹಿತಿ" else "Business Insights",
                            if (currentLang == Language.HI) "सबसे अच्छे उत्पादों और आय की जानकारी देखने के लिए 'जानकारी' टैब देखें।" else if (currentLang == Language.KN) "ಉತ್ಕೃಷ್ಟ ಉತ್ಪನ್ನಗಳು ಮತ್ತು ಆದಾಯ ನೋಡಲು 'ಮಾಹಿತಿ' ಟ್ಯಾಬ್ ಪರಿಶೀಲಿಸಿ." else "Check 'Insights' to view your best performing products and revenue charts."
                        ),
                        GuideItem(Icons.Default.Warning, Color(0xFFF97316),
                            if (currentLang == Language.HI) "कम स्टॉक अलर्ट" else if (currentLang == Language.KN) "ದಾಸ್ತಾನು ಎಚ್ಚರಿಕೆ" else "Low Stock Alerts",
                            if (currentLang == Language.HI) "जब किसी भी उत्पाद का स्टॉक 2 से कम होगा, होम स्क्रीन पर चेतावनी मिलेगी।" else if (currentLang == Language.KN) "2 ಕ್ಕಿಂತ ಕಡಿಮೆ ದಾಸ್ತಾನು ಇದ್ದಾಗ ಹೋಮ್ ಸ್ಕ್ರೀನ್ ಎಚ್ಚರಿಕೆ ನೀಡುತ್ತದೆ." else "The Home screen warns you when any product color has fewer than 3 items left."
                        ),
                    )

                    items.forEach { item ->
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(item.iconColor.copy(0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(item.icon, null, tint = item.iconColor, modifier = Modifier.size(20.dp))
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(item.title, style = MaterialTheme.typography.titleMedium, color = ArtisanInk)
                                Text(item.desc, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }

                    // Tip box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ArtisanCream)
                            .padding(12.dp)
                    ) {
                        Text(t.tip.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = ArtisanOlive.copy(0.6f))
                    }

                    Button(
                        onClick = { showGuide = false },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ArtisanOlive)
                    ) {
                        Text(t.done, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
