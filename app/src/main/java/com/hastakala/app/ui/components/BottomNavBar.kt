package com.hastakala.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.hastakala.app.data.Language
import com.hastakala.app.data.getTranslations
import com.hastakala.app.ui.theme.*

data class NavItem(
    val id: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomNavBar(
    activeTab: String,
    onTabSelect: (String) -> Unit,
    lang: Language
) {
    val t = getTranslations(lang)
    val items = listOf(
        NavItem("home",      Icons.Default.Home,        t.home),
        NavItem("quick",     Icons.Filled.ShoppingCart, t.quickSaleTab),
        NavItem("add",       Icons.Default.AddCircle,   t.addSale),
        NavItem("analytics", Icons.Default.BarChart,    t.analytics),
        NavItem("income",    Icons.Default.CurrencyRupee, t.income),
        NavItem("ai",        Icons.Filled.AutoAwesome,    t.aiTab),
        NavItem("settings",  Icons.Default.Settings,    t.settings),
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isActive = activeTab == item.id
                val iconColor by animateColorAsState(
                    targetValue = if (isActive) ArtisanOlive else Color.Gray.copy(0.5f),
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    label = "iconColor"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onTabSelect(item.id) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                                .background(ArtisanOlive)
                                .align(Alignment.CenterHorizontally)
                        )
                        Spacer(Modifier.height(4.dp))
                    } else {
                        Spacer(Modifier.height(7.dp))
                    }

                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = iconColor,
                        modifier = Modifier.size(if (isActive) 26.dp else 24.dp)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = iconColor
                    )
                }
            }
        }
    }
}
