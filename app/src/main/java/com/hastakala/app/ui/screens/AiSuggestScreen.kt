package com.hastakala.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hastakala.app.data.Language
import com.hastakala.app.data.SuggestSuccess
import com.hastakala.app.data.getTranslations
import com.hastakala.app.data.requestProductSuggestions
import com.hastakala.app.ui.theme.ArtisanClay
import com.hastakala.app.ui.theme.ArtisanCream
import com.hastakala.app.ui.theme.ArtisanLight
import com.hastakala.app.ui.theme.ArtisanOlive
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiSuggestScreen(
    apiBaseUrl: String,
    lang: Language,
) {
    val t = getTranslations(lang)
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var material by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<SuggestSuccess?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = ArtisanOlive,
        unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
        cursorColor = ArtisanOlive,
        focusedLabelColor = ArtisanOlive,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ArtisanCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(t.aiScreenTitle, style = MaterialTheme.typography.displayLarge, color = ArtisanOlive)
            Text(t.aiScreenSubtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(t.aiFieldTitle) },
            placeholder = { Text(t.aiHintTitle) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = fieldColors,
            shape = RoundedCornerShape(16.dp),
        )
        OutlinedTextField(
            value = material,
            onValueChange = { material = it },
            label = { Text(t.aiFieldMaterial) },
            placeholder = { Text(t.aiHintMaterial) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = fieldColors,
            shape = RoundedCornerShape(16.dp),
        )

        Button(
            onClick = {
                error = null
                result = null
                loading = true
                scope.launch {
                    val res = requestProductSuggestions(
                        apiBaseUrl,
                        title.trim(),
                        material.trim(),
                    )
                    loading = false
                    res.fold(
                        onSuccess = { result = it },
                        onFailure = { e ->
                            error = when (e.message) {
                                "empty_base" -> t.aiErrorEmptyBase
                                else -> e.message ?: t.aiErrorGeneric
                            }
                        },
                    )
                }
            },
            enabled = !loading && title.isNotBlank() && material.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ArtisanOlive,
                contentColor = Color.White,
                disabledContainerColor = ArtisanOlive.copy(alpha = 0.35f),
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 10.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                }
                Text(if (loading) t.aiLoading else t.aiButtonSuggest)
            }
        }

        if (error != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = error!!,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFB71C1C),
                )
            }
        }

        if (result == null && error == null && !loading) {
            Text(
                text = t.aiPlaceholderEmpty,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
        }

        result?.let { r ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(t.aiSectionResults, style = MaterialTheme.typography.titleMedium, color = ArtisanOlive)
                    Text(t.aiLabelDescription, style = MaterialTheme.typography.labelMedium, color = ArtisanClay)
                    Text(r.productDescription, style = MaterialTheme.typography.bodyMedium)
                    Text(t.aiLabelSeoTitle, style = MaterialTheme.typography.labelMedium, color = ArtisanClay)
                    Text(r.seoTitle, style = MaterialTheme.typography.bodyMedium)
                    Text(t.aiLabelMeta, style = MaterialTheme.typography.labelMedium, color = ArtisanClay)
                    Text(r.seoMetaDescription, style = MaterialTheme.typography.bodyMedium)
                    Text(t.aiLabelTags, style = MaterialTheme.typography.labelMedium, color = ArtisanClay)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        r.seoTags.forEach { tag ->
                            Surface(
                                color = ArtisanLight.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(50),
                            ) {
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = ArtisanOlive,
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}
