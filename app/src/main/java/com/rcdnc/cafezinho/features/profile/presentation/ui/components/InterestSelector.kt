package com.rcdnc.cafezinho.features.profile.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.ui.components.InterestChip
import com.rcdnc.cafezinho.ui.components.InterestChipState
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.theme.*

/**
 * Interest selector component for profile editing
 * Uses InterestChip base component with selection logic
 */
@Composable
fun InterestSelector(
    selectedInterests: List<String>,
    availableInterests: List<String>,
    onInterestToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxSelections: Int = 5,
    allowCustomInterests: Boolean = true,
    onAddCustomInterest: ((String) -> Unit)? = null,
    size: ComponentSize = ComponentSize.MEDIUM
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with counter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Interesses",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "${selectedInterests.size}/$maxSelections",
                style = MaterialTheme.typography.bodyMedium,
                color = if (selectedInterests.size >= maxSelections) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
        
        // Selected interests
        if (selectedInterests.isNotEmpty()) {
            InterestFlowRow(
                interests = selectedInterests,
                chipState = InterestChipState.SELECTED,
                onChipClick = onInterestToggle,
                size = size,
                title = "Selecionados"
            )
        }
        
        // Available interests
        if (availableInterests.isNotEmpty()) {
            InterestFlowRow(
                interests = availableInterests.filter { it !in selectedInterests },
                chipState = InterestChipState.UNSELECTED,
                onChipClick = { interest ->
                    if (selectedInterests.size < maxSelections) {
                        onInterestToggle(interest)
                    }
                },
                size = size,
                title = "Disponíveis",
                enabled = selectedInterests.size < maxSelections
            )
        }
        
        // Add custom interest
        if (allowCustomInterests && onAddCustomInterest != null) {
            AddCustomInterest(
                onAddInterest = onAddCustomInterest,
                enabled = selectedInterests.size < maxSelections
            )
        }
    }
}

@Composable
private fun InterestFlowRow(
    interests: List<String>,
    chipState: InterestChipState,
    onChipClick: (String) -> Unit,
    size: ComponentSize,
    title: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    if (interests.isEmpty()) return
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Using LazyColumn with FlowRow-like behavior
        FlowRow(
            interests = interests,
            chipState = chipState,
            onChipClick = onChipClick,
            size = size,
            enabled = enabled
        )
    }
}

@Composable
private fun FlowRow(
    interests: List<String>,
    chipState: InterestChipState,
    onChipClick: (String) -> Unit,
    size: ComponentSize,
    enabled: Boolean = true
) {
    // Simple grid-like layout for interests
    val chunkedInterests = interests.chunked(3) // 3 chips per row
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chunkedInterests.forEach { rowInterests ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowInterests.forEach { interest ->
                    InterestChip(
                        text = interest,
                        onClick = if (enabled) { { onChipClick(interest) } } else null,
                        state = chipState,
                        size = size,
                        enabled = enabled,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
                
                // Fill remaining space in incomplete rows
                repeat(3 - rowInterests.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCustomInterest(
    onAddInterest: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var newInterest by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!isExpanded) {
            // Add interest button
            OutlinedButton(
                onClick = { isExpanded = true },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adicionar interesse personalizado")
            }
        } else {
            // Text field for custom interest
            OutlinedTextField(
                value = newInterest,
                onValueChange = { newInterest = it },
                label = { Text("Novo interesse") },
                placeholder = { Text("Digite um interesse...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    TextButton(
                        onClick = {
                            if (newInterest.isNotBlank()) {
                                onAddInterest(newInterest.trim())
                                newInterest = ""
                                isExpanded = false
                                focusManager.clearFocus()
                            }
                        },
                        enabled = newInterest.isNotBlank()
                    ) {
                        Text("Adicionar")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (newInterest.isNotBlank()) {
                            onAddInterest(newInterest.trim())
                            newInterest = ""
                            isExpanded = false
                            focusManager.clearFocus()
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Cancel button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        newInterest = ""
                        isExpanded = false
                        focusManager.clearFocus()
                    }
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

/**
 * Searchable interest selector with categories
 */
@Composable
fun SearchableInterestSelector(
    selectedInterests: List<String>,
    interestCategories: Map<String, List<String>>,
    onInterestToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxSelections: Int = 5,
    onAddCustomInterest: ((String) -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar interesses") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Category tabs
        ScrollableTabRow(
            selectedTabIndex = interestCategories.keys.indexOf(selectedCategory).takeIf { it >= 0 } ?: 0,
            edgePadding = 0.dp
        ) {
            Tab(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                text = { Text("Todos") }
            )
            
            interestCategories.keys.forEach { category ->
                Tab(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    text = { Text(category) }
                )
            }
        }
        
        // Interest list
        val filteredInterests = when {
            selectedCategory != null -> interestCategories[selectedCategory] ?: emptyList()
            searchQuery.isNotBlank() -> interestCategories.values.flatten()
                .filter { it.contains(searchQuery, ignoreCase = true) }
            else -> interestCategories.values.flatten()
        }
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredInterests.chunked(3)) { rowInterests ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowInterests.forEach { interest ->
                        val isSelected = interest in selectedInterests
                        val canSelect = !isSelected && selectedInterests.size < maxSelections
                        
                        InterestChip(
                            text = interest,
                            onClick = if (isSelected || canSelect) {
                                { onInterestToggle(interest) }
                            } else null,
                            state = if (isSelected) InterestChipState.SELECTED else InterestChipState.UNSELECTED,
                            enabled = isSelected || canSelect,
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
}

// Preview functions
@Preview(name = "InterestSelector")
@Composable
private fun InterestSelectorPreview() {
    CafezinhoTheme {
        val availableInterests = listOf(
            "Música", "Viagem", "Esportes", "Culinária", "Arte", "Tecnologia",
            "Leitura", "Cinema", "Fotografia", "Dança", "Yoga", "Caminhada"
        )
        var selectedInterests by remember { 
            mutableStateOf(listOf("Música", "Viagem", "Culinária")) 
        }
        
        InterestSelector(
            selectedInterests = selectedInterests,
            availableInterests = availableInterests,
            onInterestToggle = { interest ->
                selectedInterests = if (interest in selectedInterests) {
                    selectedInterests - interest
                } else {
                    selectedInterests + interest
                }
            },
            onAddCustomInterest = { newInterest ->
                selectedInterests = selectedInterests + newInterest
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "SearchableInterestSelector")
@Composable
private fun SearchableInterestSelectorPreview() {
    CafezinhoTheme {
        val interestCategories = mapOf(
            "Esporte" to listOf("Futebol", "Basquete", "Tênis", "Natação", "Corrida"),
            "Arte" to listOf("Pintura", "Música", "Teatro", "Dança", "Fotografia"),
            "Tecnologia" to listOf("Programação", "Games", "IA", "Robótica", "Apps"),
            "Natureza" to listOf("Caminhada", "Acampamento", "Jardinagem", "Pesca", "Trilha")
        )
        var selectedInterests by remember { mutableStateOf(listOf("Futebol", "Música")) }
        
        SearchableInterestSelector(
            selectedInterests = selectedInterests,
            interestCategories = interestCategories,
            onInterestToggle = { interest ->
                selectedInterests = if (interest in selectedInterests) {
                    selectedInterests - interest
                } else {
                    selectedInterests + interest
                }
            },
            onAddCustomInterest = { newInterest ->
                selectedInterests = selectedInterests + newInterest
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}