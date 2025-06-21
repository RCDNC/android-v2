package com.rcdnc.cafezinho.features.auth.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rcdnc.cafezinho.R
import com.rcdnc.cafezinho.features.auth.mvi.AuthIntent
import com.rcdnc.cafezinho.features.auth.mvi.AuthState
import com.rcdnc.cafezinho.features.auth.presentation.viewmodel.AuthViewModel
import com.rcdnc.cafezinho.ui.components.CafezinhoButton
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme

/**
 * Simple Login screen demonstrating auth flow
 * No external dependencies - pure Compose UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit = {},
    viewModel: AuthViewModel? = null
) {
    val focusManager = LocalFocusManager.current
    
    var phoneNumber by remember { mutableStateOf("") }
    var isPhoneValid by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    
    // Simple phone validation
    LaunchedEffect(phoneNumber) {
        isPhoneValid = phoneNumber.length >= 10 && phoneNumber.all { it.isDigit() || it == '+' || it == '(' || it == ')' || it == '-' || it == ' ' }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Logo and welcome text
            LogoSection()
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Social login buttons
            SocialLoginSection(
                onGoogleSignIn = { 
                    isLoading = true
                    viewModel?.handleIntent(AuthIntent.LoginWithGoogle)
                    // For demo, navigate immediately after a delay
                    onNavigateToMain()
                },
                onFacebookSignIn = { 
                    isLoading = true
                    viewModel?.handleIntent(AuthIntent.LoginWithFacebook)
                    // For demo, navigate immediately after a delay
                    onNavigateToMain()
                },
                isLoading = isLoading
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Divider with "ou" text
            DividerWithText(text = "ou")
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Phone number input
            PhoneInputSection(
                phoneNumber = phoneNumber,
                onPhoneNumberChange = { phoneNumber = it },
                isValid = isPhoneValid,
                isLoading = isLoading,
                onContinue = {
                    focusManager.clearFocus()
                    isLoading = true
                    viewModel?.handleIntent(AuthIntent.SendPhoneVerification(phoneNumber))
                    // For demo, navigate immediately
                    onNavigateToMain()
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Error handling
            if (showError) {
                ErrorMessage(
                    error = "Erro demonstrativo - tudo funciona!",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Terms and privacy
            TermsAndPrivacyText()
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo placeholder
        Card(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "☕",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Cafezinho",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Conecte-se e encontre pessoas incríveis",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SocialLoginSection(
    onGoogleSignIn: () -> Unit,
    onFacebookSignIn: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Google Sign-In Button
        CafezinhoButton(
            text = "Entrar com Google",
            onClick = onGoogleSignIn,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        
        // Facebook Sign-In Button  
        CafezinhoButton(
            text = "Entrar com Facebook",
            onClick = onFacebookSignIn,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
    }
}

@Composable
private fun DividerWithText(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
        
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhoneInputSection(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    isValid: Boolean,
    isLoading: Boolean,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        Text(
            text = "Digite seu telefone",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("(11) 99999-9999")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (isValid) onContinue() }
            ),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        
        CafezinhoButton(
            text = "Continuar",
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            enabled = isValid && !isLoading
        )
    }
}

@Composable
private fun ErrorMessage(
    error: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = error,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
private fun TermsAndPrivacyText() {
    Text(
        text = "Ao continuar, você concorda com nossos Termos de Uso e Política de Privacidade",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    CafezinhoTheme {
        LoginScreen()
    }
}