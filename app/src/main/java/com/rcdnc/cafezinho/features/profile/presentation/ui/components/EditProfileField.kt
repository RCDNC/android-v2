package com.rcdnc.cafezinho.features.profile.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.ui.theme.*

/**
 * Validation state for form fields
 */
data class FieldValidation(
    val isValid: Boolean = true,
    val errorMessage: String? = null
)

/**
 * Enhanced text field for profile editing with validation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    validation: FieldValidation = FieldValidation(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            supportingText = if (!validation.isValid && validation.errorMessage != null) {
                { Text(validation.errorMessage) }
            } else if (supportingText != null) {
                { Text(supportingText) }
            } else null,
            isError = !validation.isValid,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                capitalization = capitalization,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { 
                    onImeAction?.invoke() ?: focusManager.moveFocus(FocusDirection.Down) 
                },
                onDone = { 
                    onImeAction?.invoke() ?: focusManager.clearFocus() 
                },
                onSend = { onImeAction?.invoke() },
                onSearch = { onImeAction?.invoke() }
            ),
            leadingIcon = leadingIcon,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Face else Icons.Default.Clear,
                            contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha"
                        )
                    }
                }
            } else trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CafezinhoPrimary,
                focusedLabelColor = CafezinhoPrimary,
                cursorColor = CafezinhoPrimary,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error,
                errorSupportingTextColor = MaterialTheme.colorScheme.error,
                errorLeadingIconColor = MaterialTheme.colorScheme.error,
                errorTrailingIconColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Number field with increment/decrement buttons
 */
@Composable
fun NumberEditField(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    min: Int = 0,
    max: Int = Int.MAX_VALUE,
    step: Int = 1,
    validation: FieldValidation = FieldValidation(),
    enabled: Boolean = true,
    supportingText: String? = null
) {
    EditProfileField(
        value = value.toString(),
        onValueChange = { newValue ->
            newValue.toIntOrNull()?.let { intValue ->
                val clampedValue = intValue.coerceIn(min, max)
                onValueChange(clampedValue)
            }
        },
        label = label,
        keyboardType = KeyboardType.Number,
        validation = validation,
        enabled = enabled,
        supportingText = supportingText,
        trailingIcon = {
            Row {
                IconButton(
                    onClick = { 
                        if (value > min) onValueChange((value - step).coerceAtLeast(min))
                    },
                    enabled = enabled && value > min
                ) {
                    Text("-")
                }
                IconButton(
                    onClick = { 
                        if (value < max) onValueChange((value + step).coerceAtMost(max))
                    },
                    enabled = enabled && value < max
                ) {
                    Text("+")
                }
            }
        },
        modifier = modifier
    )
}

/**
 * Bio/description field with character counter
 */
@Composable
fun BioEditField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLength: Int = 500,
    validation: FieldValidation = FieldValidation(),
    enabled: Boolean = true
) {
    val remainingChars = maxLength - value.length
    val isOverLimit = remainingChars < 0
    
    EditProfileField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= maxLength) {
                onValueChange(newValue)
            }
        },
        label = "Sobre mim",
        placeholder = "Conte um pouco sobre você...",
        singleLine = false,
        maxLines = 6,
        capitalization = KeyboardCapitalization.Sentences,
        imeAction = ImeAction.Default,
        validation = if (isOverLimit) {
            FieldValidation(false, "Limite de caracteres excedido")
        } else validation,
        enabled = enabled,
        supportingText = "$remainingChars caracteres restantes",
        modifier = modifier
    )
}

/**
 * Selection field with dropdown
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SelectionEditField(
    value: T?,
    onValueChange: (T) -> Unit,
    options: List<T>,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    validation: FieldValidation = FieldValidation(),
    optionToString: (T) -> String = { it.toString() }
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it && enabled },
        modifier = modifier
    ) {
        EditProfileField(
            value = value?.let(optionToString) ?: "",
            onValueChange = { },
            label = label,
            readOnly = true,
            enabled = enabled,
            validation = validation,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionToString(option)) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Preview functions
@Preview(name = "EditProfileField - States")
@Composable
private fun EditProfileFieldStatesPreview() {
    CafezinhoTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EditProfileField(
                value = "",
                onValueChange = { },
                label = "Nome",
                placeholder = "Digite seu nome"
            )
            
            EditProfileField(
                value = "João Silva",
                onValueChange = { },
                label = "Nome",
                supportingText = "Nome será exibido no perfil"
            )
            
            EditProfileField(
                value = "email@invalido",
                onValueChange = { },
                label = "Email",
                validation = FieldValidation(false, "Email inválido"),
                keyboardType = KeyboardType.Email
            )
            
            EditProfileField(
                value = "senha123",
                onValueChange = { },
                label = "Senha",
                isPassword = true
            )
        }
    }
}

@Preview(name = "NumberEditField")
@Composable
private fun NumberEditFieldPreview() {
    CafezinhoTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NumberEditField(
                value = 25,
                onValueChange = { },
                label = "Idade",
                min = 18,
                max = 99,
                supportingText = "Idade entre 18 e 99 anos"
            )
            
            NumberEditField(
                value = 50,
                onValueChange = { },
                label = "Distância máxima (km)",
                min = 1,
                max = 100,
                step = 5
            )
        }
    }
}

@Preview(name = "BioEditField")
@Composable
private fun BioEditFieldPreview() {
    CafezinhoTheme {
        var bio by remember { mutableStateOf("Olá! Sou apaixonado por viagens e sempre em busca de novas aventuras. Amo conhecer pessoas interessantes e compartilhar experiências únicas.") }
        
        BioEditField(
            value = bio,
            onValueChange = { bio = it },
            maxLength = 500,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "SelectionEditField")
@Composable
private fun SelectionEditFieldPreview() {
    CafezinhoTheme {
        val genderOptions = listOf("Masculino", "Feminino", "Não-binário", "Prefiro não dizer")
        var selectedGender by remember { mutableStateOf<String?>(null) }
        
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectionEditField(
                value = selectedGender,
                onValueChange = { selectedGender = it },
                options = genderOptions,
                label = "Gênero"
            )
            
            SelectionEditField(
                value = "São Paulo",
                onValueChange = { },
                options = listOf("São Paulo", "Rio de Janeiro", "Belo Horizonte", "Salvador"),
                label = "Cidade",
                enabled = false
            )
        }
    }
}