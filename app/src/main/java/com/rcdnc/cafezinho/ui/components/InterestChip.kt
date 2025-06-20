package com.rcdnc.cafezinho.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.theme.*

/**
 * Interest chip states for different contexts
 */
enum class InterestChipState {
    UNSELECTED,   // Chip não selecionado (outline)
    SELECTED,     // Chip selecionado (filled)
    READONLY,     // Chip apenas para leitura (profile view)
    EDITABLE      // Chip editável com botão remover
}

/**
 * Interest chip component for hobbies, tags, and categories
 * Based on legacy interest/hobby selection patterns
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestChip(
    text: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    state: InterestChipState = InterestChipState.UNSELECTED,
    size: ComponentSize = ComponentSize.MEDIUM,
    enabled: Boolean = true,
    onRemove: (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    val chipColors = getInterestChipColors(state)
    val chipSize = getInterestChipSize(size)
    
    when (state) {
        InterestChipState.EDITABLE -> {
            // Chip with remove button - using FilterChip
            FilterChip(
                onClick = { onRemove?.invoke() },
                label = {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selected = true,
                enabled = enabled,
                leadingIcon = leadingIcon,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remover $text",
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = chipColors.containerColor,
                    labelColor = chipColors.labelColor,
                    iconColor = chipColors.iconColor,
                    selectedContainerColor = chipColors.selectedContainerColor,
                    selectedLabelColor = chipColors.selectedLabelColor,
                    selectedLeadingIconColor = chipColors.selectedIconColor,
                    selectedTrailingIconColor = chipColors.selectedIconColor
                ),
                modifier = modifier.height(chipSize.height)
            )
        }
        
        InterestChipState.READONLY -> {
            // Read-only chip - using basic Surface instead of AssistChip
            Surface(
                modifier = modifier.height(chipSize.height),
                shape = CafezinhoComponentShapes.Chip,
                color = chipColors.containerColor,
                border = if (chipColors.borderColor != Color.Transparent) {
                    BorderStroke(1.dp, chipColors.borderColor)
                } else null
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = chipSize.horizontalPadding, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    leadingIcon?.let {
                        it()
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = chipColors.labelColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        
        else -> {
            // Selectable chip (selected/unselected) - using FilterChip
            FilterChip(
                onClick = { onClick?.invoke() },
                label = {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selected = state == InterestChipState.SELECTED,
                enabled = enabled,
                leadingIcon = leadingIcon,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = chipColors.containerColor,
                    labelColor = chipColors.labelColor,
                    iconColor = chipColors.iconColor,
                    selectedContainerColor = chipColors.selectedContainerColor,
                    selectedLabelColor = chipColors.selectedLabelColor,
                    selectedLeadingIconColor = chipColors.selectedIconColor
                ),
                modifier = modifier.height(chipSize.height)
            )
        }
    }
}

/**
 * Grid of interest chips with add functionality
 * For profile editing and interest selection
 */
@Composable
fun InterestChipGroup(
    interests: List<String>,
    selectedInterests: List<String>,
    onInterestToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxSelections: Int? = null,
    showAddButton: Boolean = false,
    onAddClick: (() -> Unit)? = null,
    size: ComponentSize = ComponentSize.MEDIUM
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(interests) { interest ->
            val isSelected = selectedInterests.contains(interest)
            val canSelect = maxSelections == null || 
                          selectedInterests.size < maxSelections || 
                          isSelected
            
            InterestChip(
                text = interest,
                onClick = { 
                    if (canSelect) {
                        onInterestToggle(interest)
                    }
                },
                state = if (isSelected) InterestChipState.SELECTED else InterestChipState.UNSELECTED,
                size = size,
                enabled = canSelect
            )
        }
        
        // Add button
        if (showAddButton && onAddClick != null) {
            item {
                InterestChip(
                    text = "Adicionar",
                    onClick = onAddClick,
                    state = InterestChipState.UNSELECTED,
                    size = size,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}

/**
 * Editable interest list for profile settings
 */
@Composable
fun EditableInterestList(
    interests: List<String>,
    onRemoveInterest: (String) -> Unit,
    modifier: Modifier = Modifier,
    onAddClick: (() -> Unit)? = null,
    size: ComponentSize = ComponentSize.MEDIUM
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(interests) { interest ->
            InterestChip(
                text = interest,
                state = InterestChipState.EDITABLE,
                size = size,
                onRemove = { onRemoveInterest(interest) }
            )
        }
        
        // Add button
        if (onAddClick != null) {
            item {
                InterestChip(
                    text = "Adicionar",
                    onClick = onAddClick,
                    state = InterestChipState.UNSELECTED,
                    size = size,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}

/**
 * Read-only interest display for profiles
 */
@Composable
fun ReadOnlyInterestList(
    interests: List<String>,
    modifier: Modifier = Modifier,
    size: ComponentSize = ComponentSize.SMALL
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(interests) { interest ->
            InterestChip(
                text = interest,
                state = InterestChipState.READONLY,
                size = size
            )
        }
    }
}

// Helper functions and data classes
private data class InterestChipColors(
    val containerColor: Color,
    val labelColor: Color,
    val iconColor: Color,
    val borderColor: Color,
    val selectedContainerColor: Color,
    val selectedLabelColor: Color,
    val selectedIconColor: Color
)

private data class InterestChipSize(
    val height: androidx.compose.ui.unit.Dp,
    val horizontalPadding: androidx.compose.ui.unit.Dp
)

@Composable
private fun getInterestChipColors(state: InterestChipState): InterestChipColors {
    return when (state) {
        InterestChipState.UNSELECTED -> InterestChipColors(
            containerColor = Color.Transparent,
            labelColor = MaterialTheme.colorScheme.onSurface,
            iconColor = MaterialTheme.colorScheme.onSurface,
            borderColor = CafezinhoOutline,
            selectedContainerColor = CafezinhoPrimary,
            selectedLabelColor = CafezinhoOnPrimary,
            selectedIconColor = CafezinhoOnPrimary
        )
        
        InterestChipState.SELECTED -> InterestChipColors(
            containerColor = CafezinhoPrimary,
            labelColor = CafezinhoOnPrimary,
            iconColor = CafezinhoOnPrimary,
            borderColor = Color.Transparent,
            selectedContainerColor = CafezinhoPrimary,
            selectedLabelColor = CafezinhoOnPrimary,
            selectedIconColor = CafezinhoOnPrimary
        )
        
        InterestChipState.READONLY -> InterestChipColors(
            containerColor = CafezinhoSurfaceVariant,
            labelColor = CafezinhoOnSurfaceVariant,
            iconColor = CafezinhoOnSurfaceVariant,
            borderColor = Color.Transparent,
            selectedContainerColor = CafezinhoSurfaceVariant,
            selectedLabelColor = CafezinhoOnSurfaceVariant,
            selectedIconColor = CafezinhoOnSurfaceVariant
        )
        
        InterestChipState.EDITABLE -> InterestChipColors(
            containerColor = CafezinhoPrimary.copy(alpha = 0.12f),
            labelColor = CafezinhoPrimary,
            iconColor = CafezinhoPrimary,
            borderColor = CafezinhoPrimary,
            selectedContainerColor = CafezinhoPrimary.copy(alpha = 0.12f),
            selectedLabelColor = CafezinhoPrimary,
            selectedIconColor = CafezinhoPrimary
        )
    }
}

private fun getInterestChipSize(size: ComponentSize): InterestChipSize {
    return when (size) {
        ComponentSize.SMALL -> InterestChipSize(
            height = 28.dp,
            horizontalPadding = 12.dp
        )
        ComponentSize.MEDIUM -> InterestChipSize(
            height = 32.dp,
            horizontalPadding = 16.dp
        )
        ComponentSize.LARGE -> InterestChipSize(
            height = 40.dp,
            horizontalPadding = 20.dp
        )
    }
}