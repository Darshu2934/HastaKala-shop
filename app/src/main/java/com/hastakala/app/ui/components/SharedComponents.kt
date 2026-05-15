package com.hastakala.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hastakala.app.ui.theme.*

// ─── Product Icon ─────────────────────────────────────────────────────────────
@Composable
fun ProductIcon(iconName: String, modifier: Modifier = Modifier, tint: Color = MaterialTheme.colorScheme.primary) {
    val vector: ImageVector = when (iconName) {
        "ShoppingBag"  -> Icons.Default.ShoppingBag
        "Key"          -> Icons.Default.Key
        "Palette"      -> Icons.Default.Palette
        "Gift"         -> Icons.Default.CardGiftcard
        "Flower"       -> Icons.Default.LocalFlorist
        "Gem"          -> Icons.Default.Diamond
        "Brush"        -> Icons.Default.Brush
        "Heart"        -> Icons.Default.Favorite
        "Star"         -> Icons.Default.Star
        "ShoppingCart" -> Icons.Default.ShoppingCart
        else           -> Icons.Default.ShoppingBag
    }
    Icon(imageVector = vector, contentDescription = iconName, modifier = modifier, tint = tint)
}

// ─── Stat Card ────────────────────────────────────────────────────────────────
@Composable
fun StatCard(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) { icon() }
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.headlineMedium.copy(
                color = ArtisanClay, fontWeight = FontWeight.Normal
            ))
        }
    }
}

// ─── Section Header ───────────────────────────────────────────────────────────
@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
        modifier = modifier
    )
}

// ─── Color Dot ────────────────────────────────────────────────────────────────
@Composable
fun ColorDot(hex: String, size: Int = 12) {
    val color = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { Color.Gray }
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color)
            .border(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape)
    )
}

// ─── Primary Button ───────────────────────────────────────────────────────────
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    icon: @Composable (() -> Unit)? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        if (icon != null) {
            icon()
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

// ─── Card Surface ─────────────────────────────────────────────────────────────
@Composable
fun ArtisanCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        content = content
    )
}

// ─── Badge ────────────────────────────────────────────────────────────────────
@Composable
fun StatusBadge(text: String, color: Color, background: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(background)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = color)
    }
}
