package com.rcdnc.cafezinho.features.swipe.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.ui.components.InterestChip
import com.rcdnc.cafezinho.ui.components.InterestChipState
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.theme.*
import kotlin.math.roundToInt

/**
 * Filter preferences data class
 */
data class FilterPreferences(
    val minAge: Int = 18,
    val maxAge: Int = 99,
    val maxDistance: Int = 50,
    val showOnlineOnly: Boolean = false,
    val showVerifiedOnly: Boolean = false,
    val selectedInterests: List<String> = emptyList(),
    val selectedGenders: List<String> = emptyList()
)

/**
 * Filter bottom sheet component for discovery preferences
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    isVisible: Boolean,
    preferences: FilterPreferences,
    onPreferencesChange: (FilterPreferences) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    availableInterests: List<String> = emptyList(),
    availableGenders: List<String> = listOf("Masculino", "Feminino", "Não-binário")
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            modifier = modifier
        ) {
            FilterBottomSheetContent(
                preferences = preferences,
                onPreferencesChange = onPreferencesChange,
                onDismiss = onDismiss,
                onApply = onApply,
                onReset = onReset,
                availableInterests = availableInterests,
                availableGenders = availableGenders
            )
        }
    }
}

@Composable
private fun FilterBottomSheetContent(
    preferences: FilterPreferences,
    onPreferencesChange: (FilterPreferences) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    availableInterests: List<String>,
    availableGenders: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = CafezinhoPrimary
                )
                Text(
                    text = "Filtros de Descoberta",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Age range
        AgeRangeFilter(
            minAge = preferences.minAge,
            maxAge = preferences.maxAge,
            onAgeRangeChange = { min, max ->
                onPreferencesChange(preferences.copy(minAge = min, maxAge = max))
            }
        )
        
        // Distance
        DistanceFilter(
            maxDistance = preferences.maxDistance,
            onDistanceChange = { distance ->
                onPreferencesChange(preferences.copy(maxDistance = distance))
            }
        )
        
        // Gender preferences
        if (availableGenders.isNotEmpty()) {
            GenderFilter(
                selectedGenders = preferences.selectedGenders,
                availableGenders = availableGenders,
                onGenderToggle = { gender ->
                    val newGenders = if (gender in preferences.selectedGenders) {
                        preferences.selectedGenders - gender
                    } else {
                        preferences.selectedGenders + gender
                    }
                    onPreferencesChange(preferences.copy(selectedGenders = newGenders))
                }
            )
        }
        
        // Quick toggles
        QuickToggles(
            showOnlineOnly = preferences.showOnlineOnly,
            showVerifiedOnly = preferences.showVerifiedOnly,
            onToggleOnlineOnly = { 
                onPreferencesChange(preferences.copy(showOnlineOnly = it))
            },
            onToggleVerifiedOnly = { 
                onPreferencesChange(preferences.copy(showVerifiedOnly = it))
            }
        )
        
        // Interests filter
        if (availableInterests.isNotEmpty()) {
            InterestsFilter(
                selectedInterests = preferences.selectedInterests,
                availableInterests = availableInterests,
                onInterestToggle = { interest ->
                    val newInterests = if (interest in preferences.selectedInterests) {
                        preferences.selectedInterests - interest
                    } else {
                        preferences.selectedInterests + interest
                    }
                    onPreferencesChange(preferences.copy(selectedInterests = newInterests))
                }
            )
        }
        
        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f)
            ) {
                Text("Resetar")
            }
            
            Button(
                onClick = onApply,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CafezinhoPrimary
                )
            ) {
                Text("Aplicar Filtros")
            }
        }
        
        // Bottom padding for navigation
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun AgeRangeFilter(
    minAge: Int,
    maxAge: Int,
    onAgeRangeChange: (Int, Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Faixa etária: $minAge - $maxAge anos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        RangeSlider(
            value = minAge.toFloat()..maxAge.toFloat(),
            onValueChange = { range ->
                onAgeRangeChange(range.start.roundToInt(), range.endInclusive.roundToInt())
            },
            valueRange = 18f..99f,
            colors = SliderDefaults.colors(
                thumbColor = CafezinhoPrimary,
                activeTrackColor = CafezinhoPrimary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "18",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "99+",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DistanceFilter(
    maxDistance: Int,
    onDistanceChange: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Distância máxima: ${if (maxDistance >= 100) "100+ km" else "$maxDistance km"}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        Slider(
            value = maxDistance.toFloat(),
            onValueChange = { onDistanceChange(it.roundToInt()) },
            valueRange = 1f..100f,
            colors = SliderDefaults.colors(
                thumbColor = CafezinhoPrimary,
                activeTrackColor = CafezinhoPrimary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "1 km",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "100+ km",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GenderFilter(
    selectedGenders: List<String>,
    availableGenders: List<String>,
    onGenderToggle: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Mostrar-me",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        // Gender chips in flow layout
        val chunkedGenders = availableGenders.chunked(2)
        chunkedGenders.forEach { rowGenders ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowGenders.forEach { gender ->
                    InterestChip(
                        text = gender,
                        onClick = { onGenderToggle(gender) },
                        state = if (gender in selectedGenders) {
                            InterestChipState.SELECTED
                        } else {
                            InterestChipState.UNSELECTED
                        },
                        size = ComponentSize.MEDIUM,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Fill remaining space
                repeat(2 - rowGenders.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun QuickToggles(
    showOnlineOnly: Boolean,
    showVerifiedOnly: Boolean,
    onToggleOnlineOnly: (Boolean) -> Unit,
    onToggleVerifiedOnly: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Filtros rápidos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        // Online only toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Apenas pessoas online",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Mostra apenas usuários ativos recentemente",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = showOnlineOnly,
                onCheckedChange = onToggleOnlineOnly,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CafezinhoOnPrimary,
                    checkedTrackColor = CafezinhoPrimary
                )
            )
        }
        
        // Verified only toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Apenas perfis verificados",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Mostra apenas usuários com verificação",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = showVerifiedOnly,
                onCheckedChange = onToggleVerifiedOnly,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CafezinhoOnPrimary,
                    checkedTrackColor = CafezinhoPrimary
                )
            )
        }
    }
}

@Composable
private fun InterestsFilter(
    selectedInterests: List<String>,
    availableInterests: List<String>,
    onInterestToggle: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Interesses em comum",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = "Prioriza pessoas com estes interesses",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Interest chips in flow layout
        val chunkedInterests = availableInterests.chunked(3)
        chunkedInterests.forEach { rowInterests ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                rowInterests.forEach { interest ->
                    InterestChip(
                        text = interest,
                        onClick = { onInterestToggle(interest) },
                        state = if (interest in selectedInterests) {
                            InterestChipState.SELECTED
                        } else {
                            InterestChipState.UNSELECTED
                        },
                        size = ComponentSize.SMALL,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
                
                // Fill remaining space
                repeat(3 - rowInterests.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// Preview functions
@Preview(name = "FilterBottomSheet")
@Composable
private fun FilterBottomSheetPreview() {
    CafezinhoTheme {
        var preferences by remember {
            mutableStateOf(
                FilterPreferences(
                    minAge = 22,
                    maxAge = 35,
                    maxDistance = 25,
                    showOnlineOnly = true,
                    showVerifiedOnly = false,
                    selectedInterests = listOf("Música", "Viagem"),
                    selectedGenders = listOf("Feminino", "Não-binário")
                )
            )
        }
        
        FilterBottomSheet(
            isVisible = true,
            preferences = preferences,
            onPreferencesChange = { preferences = it },
            onDismiss = { },
            onApply = { },
            onReset = { preferences = FilterPreferences() },
            availableInterests = listOf(
                "Música", "Viagem", "Esportes", "Culinária", "Arte", "Tecnologia",
                "Leitura", "Cinema", "Fotografia", "Dança"
            ),
            availableGenders = listOf("Masculino", "Feminino", "Não-binário")
        )
    }
}