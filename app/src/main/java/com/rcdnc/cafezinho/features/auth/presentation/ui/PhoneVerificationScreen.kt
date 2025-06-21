package com.rcdnc.cafezinho.features.auth.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcdnc.cafezinho.R
import com.rcdnc.cafezinho.features.auth.mvi.AuthIntent
import com.rcdnc.cafezinho.features.auth.mvi.AuthState
import com.rcdnc.cafezinho.features.auth.presentation.ui.components.OtpInputField
import com.rcdnc.cafezinho.features.auth.presentation.viewmodel.AuthViewModel
import com.rcdnc.cafezinho.ui.components.CafezinhoButton
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme
import kotlinx.coroutines.delay

/**
 * Phone verification screen with OTP input and resend functionality
 * Handles SMS verification code input with automatic validation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneVerificationScreen(
    phoneNumber: String,
    verificationId: String? = null,
    onNavigateBack: () -> Unit = {},
    onVerificationSuccess: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    
    var otpCode by remember { mutableStateOf("") }
    var timeRemaining by remember { mutableStateOf(60) }
    var canResend by remember { mutableStateOf(false) }
    
    // Handle state changes
    LaunchedEffect(state) {
        when (state) {
            is AuthState.PhoneVerificationSuccess -> {
                onVerificationSuccess()
            }
            is AuthState.Authenticated -> {
                onVerificationSuccess()
            }
            else -> {}
        }
    }
    
    // Countdown timer for resend
    LaunchedEffect(canResend) {
        if (!canResend && timeRemaining > 0) {
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining--
            }
            canResend = true
        }
    }
    
    // Auto-verify when OTP is complete
    LaunchedEffect(otpCode) {
        if (otpCode.length == 6 && verificationId != null) {
            viewModel.handleIntent(AuthIntent.VerifyPhoneOtp(phoneNumber, otpCode))
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Verificação",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Verification icon
                VerificationIcon()
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Title and description
                VerificationHeader(phoneNumber = phoneNumber)
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // OTP input
                OtpInputSection(
                    otpCode = otpCode,
                    onOtpChange = { otpCode = it },
                    isLoading = state is AuthState.PhoneVerificationLoading,
                    isError = state is AuthState.PhoneVerificationError
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Verify button
                CafezinhoButton(
                    text = stringResource(R.string.auth_continue),
                    onClick = {
                        focusManager.clearFocus()
                        if (verificationId != null) {
                            viewModel.handleIntent(AuthIntent.VerifyPhoneOtp(phoneNumber, otpCode))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = otpCode.length == 6 && !state.isLoading() && verificationId != null,
                    isLoading = state is AuthState.PhoneVerificationLoading
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Resend section
                ResendSection(
                    canResend = canResend,
                    timeRemaining = timeRemaining,
                    isLoading = state is AuthState.PhoneVerificationLoading,
                    onResend = {
                        viewModel.handleIntent(AuthIntent.ResendPhoneVerification(phoneNumber))
                        timeRemaining = 60
                        canResend = false
                        otpCode = ""
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Error handling
                if (state is AuthState.PhoneVerificationError) {
                    ErrorMessage(
                        error = state.error.message,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun VerificationIcon() {
    Card(
        modifier = Modifier.size(80.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📱",
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@Composable
private fun VerificationHeader(phoneNumber: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Código de verificação",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Enviamos um código de 6 dígitos para",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = phoneNumber,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun OtpInputSection(
    otpCode: String,
    onOtpChange: (String) -> Unit,
    isLoading: Boolean,
    isError: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OtpInputField(
            value = otpCode,
            onValueChange = onOtpChange,
            length = 6,
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            enabled = !isLoading
        )
        
        if (isError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Código inválido. Tente novamente.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun ResendSection(
    canResend: Boolean,
    timeRemaining: Int,
    isLoading: Boolean,
    onResend: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Não recebeu o código?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (canResend) {
            TextButton(
                onClick = onResend,
                enabled = !isLoading
            ) {
                Text(
                    text = stringResource(R.string.auth_resend_otp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        } else {
            Text(
                text = "Reenviar em ${timeRemaining}s",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        }
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

// Extension function to check loading states
private fun AuthState.isLoading(): Boolean {
    return this is AuthState.PhoneVerificationLoading ||
           this is AuthState.CheckingAuthState ||
           this is AuthState.GoogleSignInLoading ||
           this is AuthState.FacebookSignInLoading
}

@Preview(showBackground = true)
@Composable
fun PhoneVerificationScreenPreview() {
    CafezinhoTheme {
        PhoneVerificationScreen(
            phoneNumber = "(11) 99999-9999",
            verificationId = "test123"
        )
    }
}