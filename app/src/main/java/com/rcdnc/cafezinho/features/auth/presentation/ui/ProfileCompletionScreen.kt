package com.rcdnc.cafezinho.features.auth.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcdnc.cafezinho.R
import com.rcdnc.cafezinho.features.auth.domain.model.ProfileStep
import com.rcdnc.cafezinho.features.auth.mvi.AuthIntent
import com.rcdnc.cafezinho.features.auth.mvi.AuthState
import com.rcdnc.cafezinho.features.auth.presentation.ui.components.ProfileStepContent
import com.rcdnc.cafezinho.features.auth.presentation.viewmodel.AuthViewModel
import com.rcdnc.cafezinho.ui.components.CafezinhoButton
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme

/**
 * Profile completion screen with 8-step wizard
 * Guides users through completing their dating profile
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCompletionScreen(
    onProfileComplete: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Extract current step and registration data from state
    val currentStep = when (state) {
        is AuthState.ProfileCompletion -> state.currentStep
        else -> ProfileStep.FIRST_NAME
    }
    
    val registrationData = when (state) {
        is AuthState.ProfileCompletion -> state.registrationData
        else -> com.rcdnc.cafezinho.features.auth.domain.model.RegistrationData()
    }
    
    // Handle state changes
    LaunchedEffect(state) {
        when (state) {
            is AuthState.ProfileCompleted -> {
                onProfileComplete()
            }
            is AuthState.Authenticated -> {
                onProfileComplete()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            ProfileCompletionTopBar(
                currentStep = currentStep,
                onNavigateBack = {
                    if (currentStep == ProfileStep.FIRST_NAME) {
                        onNavigateBack()
                    } else {
                        viewModel.handleIntent(AuthIntent.PreviousProfileStep)
                    }
                }
            )
        }
    ) { paddingValues ->
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                
                // Progress indicator
                ProfileProgressIndicator(
                    currentStep = currentStep,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Step content
                ProfileStepContent(
                    currentStep = currentStep,
                    registrationData = registrationData,
                    onDataChange = { field, value ->
                        when (field) {
                            "firstName" -> viewModel.handleIntent(AuthIntent.UpdateFirstName(value as String))
                            "lastName" -> viewModel.handleIntent(AuthIntent.UpdateLastName(value as String))
                            "dateOfBirth" -> viewModel.handleIntent(AuthIntent.UpdateDateOfBirth(value as java.util.Date))
                            "gender" -> viewModel.handleIntent(AuthIntent.UpdateGender(value as String))
                            "genderPreference" -> viewModel.handleIntent(AuthIntent.UpdateGenderPreference(value as String))
                            "bio" -> viewModel.handleIntent(AuthIntent.UpdateBio(value as String))
                            "school" -> viewModel.handleIntent(AuthIntent.UpdateSchool(value as String))
                            "photos" -> viewModel.handleIntent(AuthIntent.UpdateProfilePhotos(value as List<String>))
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Navigation buttons
                ProfileNavigationButtons(
                    currentStep = currentStep,
                    registrationData = registrationData,
                    isLoading = state is AuthState.ProfileCompletionLoading,
                    onPrevious = {
                        viewModel.handleIntent(AuthIntent.PreviousProfileStep)
                    },
                    onNext = {
                        viewModel.handleIntent(AuthIntent.NextProfileStep)
                    },
                    onComplete = {
                        viewModel.handleIntent(AuthIntent.CompleteProfile)
                    }
                )
                
                // Error handling
                if (state is AuthState.ProfileCompletionError) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ErrorMessage(
                        error = state.error.message,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileCompletionTopBar(
    currentStep: ProfileStep,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = { 
            Text(
                text = "Complete seu perfil",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            ) 
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar"
                )
            }
        },
        actions = {
            Text(
                text = "${currentStep.ordinal + 1}/8",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(end = 16.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun ProfileProgressIndicator(
    currentStep: ProfileStep,
    modifier: Modifier = Modifier
) {
    val totalSteps = ProfileStep.values().size - 1 // Exclude COMPLETION
    val currentStepIndex = currentStep.ordinal
    val progress = (currentStepIndex + 1).toFloat() / totalSteps.toFloat()
    
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Passo ${currentStepIndex + 1} de $totalSteps",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        )
    }
}

@Composable
private fun ProfileNavigationButtons(
    currentStep: ProfileStep,
    registrationData: com.rcdnc.cafezinho.features.auth.mvi.RegistrationData,
    isLoading: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onComplete: () -> Unit
) {
    val canProceed = registrationData.isStepValid(currentStep)
    val isLastStep = currentStep == ProfileStep.PHOTOS
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        
        // Previous button (only show if not first step)
        if (currentStep != ProfileStep.FIRST_NAME) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f),
                enabled = !isLoading,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Anterior")
            }
        }
        
        // Next/Complete button
        CafezinhoButton(
            text = if (isLastStep) "Finalizar" else "Próximo",
            onClick = if (isLastStep) onComplete else onNext,
            modifier = Modifier.weight(if (currentStep == ProfileStep.FIRST_NAME) 1f else 2f),
            enabled = canProceed && !isLoading,
            isLoading = isLoading,
            trailingIcon = if (!isLastStep) {
                {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else null
        )
    }
}

@Composable
private fun ErrorMessage(
    error: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = error,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileCompletionScreenPreview() {
    CafezinhoTheme {
        ProfileCompletionScreen()
    }
}