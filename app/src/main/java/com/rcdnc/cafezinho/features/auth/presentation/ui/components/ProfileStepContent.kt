package com.rcdnc.cafezinho.features.auth.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.features.auth.domain.model.ProfileStep
import com.rcdnc.cafezinho.features.auth.domain.model.RegistrationData
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme
import java.text.SimpleDateFormat
import java.util.*

/**
 * Content component for each step of the profile completion wizard
 * Handles different input types and validation for each profile field
 */
@Composable
fun ProfileStepContent(
    currentStep: ProfileStep,
    registrationData: RegistrationData,
    onDataChange: (String, Any) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentStep) {
            ProfileStep.FIRST_NAME -> FirstNameStep(
                value = registrationData.firstName,
                onValueChange = { onDataChange("firstName", it) }
            )
            ProfileStep.LAST_NAME -> LastNameStep(
                value = registrationData.lastName,
                onValueChange = { onDataChange("lastName", it) }
            )
            ProfileStep.DATE_OF_BIRTH -> DateOfBirthStep(
                value = registrationData.dateOfBirth,
                onValueChange = { onDataChange("dateOfBirth", it) }
            )
            ProfileStep.GENDER -> GenderStep(
                value = registrationData.gender,
                onValueChange = { onDataChange("gender", it) }
            )
            ProfileStep.GENDER_PREFERENCE -> GenderPreferenceStep(
                value = registrationData.genderPreference,
                onValueChange = { onDataChange("genderPreference", it) }
            )
            ProfileStep.BIO -> BioStep(
                value = registrationData.bio,
                onValueChange = { onDataChange("bio", it) }
            )
            ProfileStep.SCHOOL -> SchoolStep(
                value = registrationData.school,
                onValueChange = { onDataChange("school", it) }
            )
            ProfileStep.PHOTOS -> PhotosStep(
                value = registrationData.profilePhotos,
                onValueChange = { onDataChange("photos", it) }
            )
            ProfileStep.COMPLETION -> CompletionStep()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FirstNameStep(
    value: String,
    onValueChange: (String) -> Unit
) {
    StepContainer(
        title = "Qual é o seu primeiro nome?",
        subtitle = "Este será o nome que outras pessoas verão"
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Digite seu primeiro nome") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LastNameStep(
    value: String,
    onValueChange: (String) -> Unit
) {
    StepContainer(
        title = "E o seu sobrenome?",
        subtitle = "Seu nome completo ajuda na verificação"
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Digite seu sobrenome") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateOfBirthStep(
    value: Date?,
    onValueChange: (Date) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    StepContainer(
        title = "Quando você nasceu?",
        subtitle = "Você deve ter pelo menos 18 anos para usar o app"
    ) {
        OutlinedTextField(
            value = value?.let { dateFormatter.format(it) } ?: "",
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Selecione sua data de nascimento") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Selecionar data")
                }
            }
        )
        
        if (showDatePicker) {
            DatePickerDialog(
                onDateSelected = { selectedDate ->
                    onValueChange(selectedDate)
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}

@Composable
private fun GenderStep(
    value: String?,
    onValueChange: (String) -> Unit
) {
    val genderOptions = listOf(
        "Mulher" to "woman",
        "Homem" to "man",
        "Não-binário" to "non-binary",
        "Prefiro não dizer" to "prefer-not-to-say"
    )
    
    StepContainer(
        title = "Como você se identifica?",
        subtitle = "Isso nos ajuda a personalizar sua experiência"
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genderOptions) { (label, optionValue) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = value == optionValue,
                            onClick = { onValueChange(optionValue) },
                            role = Role.RadioButton
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (value == optionValue) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = if (value == optionValue) {
                        CardDefaults.outlinedCardBorder().copy(
                            width = 2.dp
                        )
                    } else {
                        CardDefaults.outlinedCardBorder()
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = value == optionValue,
                            onClick = { onValueChange(optionValue) }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GenderPreferenceStep(
    value: String?,
    onValueChange: (String) -> Unit
) {
    val preferenceOptions = listOf(
        "Mulheres" to "women",
        "Homens" to "men",
        "Todos" to "everyone"
    )
    
    StepContainer(
        title = "Quem você gostaria de conhecer?",
        subtitle = "Você pode alterar isso depois nas configurações"
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(preferenceOptions) { (label, optionValue) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = value == optionValue,
                            onClick = { onValueChange(optionValue) },
                            role = Role.RadioButton
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (value == optionValue) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = if (value == optionValue) {
                        CardDefaults.outlinedCardBorder().copy(
                            width = 2.dp
                        )
                    } else {
                        CardDefaults.outlinedCardBorder()
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = value == optionValue,
                            onClick = { onValueChange(optionValue) }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BioStep(
    value: String,
    onValueChange: (String) -> Unit
) {
    StepContainer(
        title = "Conte um pouco sobre você",
        subtitle = "Escreva algo interessante para chamar atenção"
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("Ex: Amo viajar, adoro café e estou sempre procurando novas aventuras...") },
            maxLines = 5,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            )
        )
        
        Text(
            text = "${value.length}/500",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchoolStep(
    value: String,
    onValueChange: (String) -> Unit
) {
    StepContainer(
        title = "Onde você estuda ou estudou?",
        subtitle = "Isso ajuda a encontrar pessoas com interesses similares"
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ex: Universidade de São Paulo") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
    }
}

@Composable
private fun PhotosStep(
    value: List<String>,
    onValueChange: (List<String>) -> Unit
) {
    StepContainer(
        title = "Adicione suas melhores fotos",
        subtitle = "Adicione pelo menos uma foto para continuar"
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📸",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Toque para adicionar fotos",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Mínimo: 1 foto • Máximo: 6 fotos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        // Placeholder for photo count - in real implementation this would show actual photos
        if (value.isEmpty()) {
            // Auto-add a placeholder photo for demo purposes
            LaunchedEffect(Unit) {
                onValueChange(listOf("placeholder_photo_1"))
            }
        }
    }
}

@Composable
private fun CompletionStep() {
    StepContainer(
        title = "Perfil completo! 🎉",
        subtitle = "Agora você está pronto para conhecer pessoas incríveis"
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "✨",
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Seu perfil está completo e verificado!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Agora você pode começar a explorar e conhecer pessoas interessantes.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun StepContainer(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(Date(millis))
                    }
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileStepContentPreview() {
    CafezinhoTheme {
        ProfileStepContent(
            currentStep = ProfileStep.FIRST_NAME,
            registrationData = RegistrationData(),
            onDataChange = { _, _ -> },
            modifier = Modifier.padding(16.dp)
        )
    }
}