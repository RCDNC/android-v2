package com.rcdnc.cafezinho.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcdnc.cafezinho.features.auth.domain.repository.AuthRepository
import com.rcdnc.cafezinho.features.auth.mvi.AuthIntent
import com.rcdnc.cafezinho.features.auth.mvi.AuthState
import com.rcdnc.cafezinho.features.auth.mvi.AuthError
import com.rcdnc.cafezinho.features.auth.mvi.ProfileStep
import com.rcdnc.cafezinho.features.auth.mvi.RegistrationData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AuthViewModel implementing MVI pattern
 * Handles all authentication logic and state management
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private var currentRegistrationData = RegistrationData(phoneNumber = "")
    private var lastVerificationId: String? = null
    private var lastPhoneNumber: String? = null

    /**
     * Main entry point for handling all user intents
     */
    fun handleIntent(intent: AuthIntent) {
        viewModelScope.launch {
            when (intent) {
                // Social Login Intents
                is AuthIntent.LoginWithGoogle -> handleGoogleSignIn()
                is AuthIntent.LoginWithFacebook -> handleFacebookSignIn()
                is AuthIntent.HandleSocialLoginResult -> handleSocialLoginResult(intent.provider, intent.result)

                // Phone Authentication Intents
                is AuthIntent.SendPhoneVerification -> handleSendPhoneVerification(intent.phoneNumber)
                is AuthIntent.VerifyPhoneOtp -> handleVerifyPhoneOtp(intent.phoneNumber, intent.otp)
                is AuthIntent.ResendPhoneOtp -> handleResendPhoneOtp()

                // Traditional Login Intents
                is AuthIntent.LoginWithEmail -> handleEmailLogin(intent.email, intent.password)
                is AuthIntent.LoginWithPhone -> handlePhoneLogin(intent.phone, intent.otp)

                // Registration Intents
                is AuthIntent.StartRegistration -> handleStartRegistration(intent.phoneNumber)
                is AuthIntent.CompleteRegistration -> handleCompleteRegistration(intent.userProfile)

                // Profile Completion Intents
                is AuthIntent.UpdateFirstName -> handleUpdateFirstName(intent.firstName)
                is AuthIntent.UpdateDateOfBirth -> handleUpdateDateOfBirth(intent.dateOfBirth)
                is AuthIntent.UpdateGender -> handleUpdateGender(intent.gender)
                is AuthIntent.UpdateGenderPreference -> handleUpdateGenderPreference(intent.preference)
                is AuthIntent.UpdateInterests -> handleUpdateInterests(intent.interests)
                is AuthIntent.UpdatePhotos -> handleUpdatePhotos(intent.photos)
                is AuthIntent.UpdateAdditionalInfo -> handleUpdateAdditionalInfo(intent.school, intent.bio)

                // Profile Navigation Intents
                is AuthIntent.NextProfileStep -> handleNextProfileStep()
                is AuthIntent.PreviousProfileStep -> handlePreviousProfileStep()
                is AuthIntent.SkipProfileStep -> handleSkipProfileStep()
                is AuthIntent.CompleteProfile -> handleCompleteProfile()

                // Session Management Intents
                is AuthIntent.CheckAuthState -> handleCheckAuthState()
                is AuthIntent.RefreshSession -> handleRefreshSession()
                is AuthIntent.Logout -> handleLogout()
                is AuthIntent.ClearAuthState -> handleClearAuthState()

                // Error Handling Intents
                is AuthIntent.RetryLastAction -> handleRetryLastAction()
                is AuthIntent.DismissError -> handleDismissError()

                // Validation Intents
                is AuthIntent.ValidatePhoneNumber -> handleValidatePhoneNumber(intent.phoneNumber)
                is AuthIntent.ValidateOtp -> handleValidateOtp(intent.otp)
                is AuthIntent.ValidateProfileField -> handleValidateProfileField(intent.field, intent.value)
            }
        }
    }

    // Social Login Handlers
    private suspend fun handleGoogleSignIn() {
        _state.value = AuthState.GoogleSignInLoading
        val result = authRepository.signInWithGoogle()
        
        result.fold(
            onSuccess = { user ->
                _state.value = AuthState.SocialSignInSuccess(user, "Google")
            },
            onFailure = { error ->
                _state.value = AuthState.SocialSignInError(mapToAuthError(error), "Google")
            }
        )
    }

    private suspend fun handleFacebookSignIn() {
        _state.value = AuthState.FacebookSignInLoading
        val result = authRepository.signInWithFacebook()
        
        result.fold(
            onSuccess = { user ->
                _state.value = AuthState.SocialSignInSuccess(user, "Facebook")
            },
            onFailure = { error ->
                _state.value = AuthState.SocialSignInError(mapToAuthError(error), "Facebook")
            }
        )
    }

    private suspend fun handleSocialLoginResult(provider: String, resultData: Map<String, Any>) {
        // TODO: Handle social login result processing
        _state.value = AuthState.Loading("Processando login...")
    }

    // Phone Authentication Handlers
    private suspend fun handleSendPhoneVerification(phoneNumber: String) {
        _state.value = AuthState.PhoneVerificationLoading
        lastPhoneNumber = phoneNumber
        
        val result = authRepository.sendPhoneVerification(phoneNumber)
        
        result.fold(
            onSuccess = { verificationId ->
                lastVerificationId = verificationId
                _state.value = AuthState.PhoneVerificationSent(phoneNumber, verificationId)
            },
            onFailure = { error ->
                _state.value = AuthState.Error(mapToAuthError(error))
            }
        )
    }

    private suspend fun handleVerifyPhoneOtp(phoneNumber: String, otp: String) {
        val verificationId = lastVerificationId
        if (verificationId == null) {
            _state.value = AuthState.Error(AuthError.CustomError("Verification ID not found"))
            return
        }

        _state.value = AuthState.OtpVerificationLoading
        val result = authRepository.verifyPhoneOtp(verificationId, otp)
        
        result.fold(
            onSuccess = { user ->
                _state.value = AuthState.OtpVerificationSuccess(user)
            },
            onFailure = { error ->
                _state.value = AuthState.OtpVerificationError(mapToAuthError(error), phoneNumber)
            }
        )
    }

    private suspend fun handleResendPhoneOtp() {
        val phoneNumber = lastPhoneNumber
        if (phoneNumber == null) {
            _state.value = AuthState.Error(AuthError.CustomError("Phone number not found"))
            return
        }

        _state.value = AuthState.ResendingOtp
        val result = authRepository.resendPhoneVerification(phoneNumber)
        
        result.fold(
            onSuccess = { verificationId ->
                lastVerificationId = verificationId
                _state.value = AuthState.OtpResent(phoneNumber)
            },
            onFailure = { error ->
                _state.value = AuthState.Error(mapToAuthError(error))
            }
        )
    }

    // Traditional Login Handlers
    private suspend fun handleEmailLogin(email: String, password: String) {
        _state.value = AuthState.Loading("Fazendo login...")
        val result = authRepository.signInWithEmail(email, password)
        
        result.fold(
            onSuccess = { user ->
                _state.value = AuthState.Success(user, "Login realizado com sucesso")
            },
            onFailure = { error ->
                _state.value = AuthState.Error(mapToAuthError(error))
            }
        )
    }

    private suspend fun handlePhoneLogin(phone: String, otp: String) {
        _state.value = AuthState.Loading("Verificando código...")
        val result = authRepository.signInWithPhone(phone, otp)
        
        result.fold(
            onSuccess = { user ->
                _state.value = AuthState.Success(user, "Login realizado com sucesso")
            },
            onFailure = { error ->
                _state.value = AuthState.Error(mapToAuthError(error))
            }
        )
    }

    // Registration Handlers
    private suspend fun handleStartRegistration(phoneNumber: String) {
        currentRegistrationData = RegistrationData(phoneNumber = phoneNumber)
        handleSendPhoneVerification(phoneNumber)
    }

    private suspend fun handleCompleteRegistration(userProfile: RegistrationData) {
        _state.value = AuthState.RegistrationLoading
        val result = authRepository.completeRegistration(userProfile)
        
        result.fold(
            onSuccess = { user ->
                _state.value = AuthState.RegistrationSuccess(user)
            },
            onFailure = { error ->
                _state.value = AuthState.RegistrationError(mapToAuthError(error))
            }
        )
    }

    // Profile Completion Handlers
    private fun handleUpdateFirstName(firstName: String) {
        currentRegistrationData = currentRegistrationData.copy(firstName = firstName)
        updateProfileCompletionState(ProfileStep.FIRST_NAME)
    }

    private fun handleUpdateDateOfBirth(dateOfBirth: Long) {
        currentRegistrationData = currentRegistrationData.copy(dateOfBirth = dateOfBirth)
        updateProfileCompletionState(ProfileStep.DATE_OF_BIRTH)
    }

    private fun handleUpdateGender(gender: String) {
        currentRegistrationData = currentRegistrationData.copy(gender = gender)
        updateProfileCompletionState(ProfileStep.GENDER)
    }

    private fun handleUpdateGenderPreference(preference: String) {
        currentRegistrationData = currentRegistrationData.copy(genderPreference = preference)
        updateProfileCompletionState(ProfileStep.GENDER_PREFERENCE)
    }

    private fun handleUpdateInterests(interests: List<String>) {
        currentRegistrationData = currentRegistrationData.copy(interests = interests)
        updateProfileCompletionState(ProfileStep.INTERESTS)
    }

    private fun handleUpdatePhotos(photos: List<String>) {
        currentRegistrationData = currentRegistrationData.copy(photos = photos)
        updateProfileCompletionState(ProfileStep.PHOTOS)
    }

    private fun handleUpdateAdditionalInfo(school: String?, bio: String?) {
        currentRegistrationData = currentRegistrationData.copy(school = school, bio = bio)
        updateProfileCompletionState(ProfileStep.ADDITIONAL_INFO)
    }

    private fun updateProfileCompletionState(currentStep: ProfileStep) {
        _state.value = AuthState.ProfileCompletion(
            currentStep = currentStep,
            registrationData = currentRegistrationData
        )
    }

    // Profile Navigation Handlers
    private fun handleNextProfileStep() {
        val currentState = _state.value
        if (currentState is AuthState.ProfileCompletion) {
            val nextStep = currentState.currentStep.next()
            if (nextStep != null) {
                _state.value = currentState.copy(currentStep = nextStep)
            } else {
                viewModelScope.launch {
                    handleCompleteProfile()
                }
            }
        }
    }

    private fun handlePreviousProfileStep() {
        val currentState = _state.value
        if (currentState is AuthState.ProfileCompletion) {
            val previousStep = currentState.currentStep.previous()
            if (previousStep != null) {
                _state.value = currentState.copy(currentStep = previousStep)
            }
        }
    }

    private fun handleSkipProfileStep() {
        handleNextProfileStep()
    }

    private suspend fun handleCompleteProfile() {
        _state.value = AuthState.ProfileCompletionLoading
        val result = authRepository.completeRegistration(currentRegistrationData)
        
        result.fold(
            onSuccess = { user ->
                _state.value = AuthState.ProfileCompleted(user)
            },
            onFailure = { error ->
                val currentState = _state.value
                val step = if (currentState is AuthState.ProfileCompletion) {
                    currentState.currentStep
                } else {
                    ProfileStep.COMPLETION
                }
                _state.value = AuthState.ProfileCompletionError(mapToAuthError(error), step)
            }
        )
    }

    // Session Management Handlers
    private suspend fun handleCheckAuthState() {
        _state.value = AuthState.CheckingAuthState
        val currentUser = authRepository.getCurrentUser()
        
        if (currentUser != null) {
            val isProfileComplete = authRepository.getProfileCompletionStatus(currentUser.id)
                .getOrNull() ?: false
            _state.value = AuthState.Authenticated(currentUser, isProfileComplete)
        } else {
            _state.value = AuthState.LoggedOut
        }
    }

    private suspend fun handleRefreshSession() {
        _state.value = AuthState.SessionRefreshing
        val result = authRepository.refreshAuthToken()
        
        result.fold(
            onSuccess = {
                handleCheckAuthState()
            },
            onFailure = {
                _state.value = AuthState.SessionExpired
            }
        )
    }

    private suspend fun handleLogout() {
        _state.value = AuthState.Loading("Fazendo logout...")
        val result = authRepository.signOut()
        
        result.fold(
            onSuccess = {
                currentRegistrationData = RegistrationData(phoneNumber = "")
                lastVerificationId = null
                lastPhoneNumber = null
                _state.value = AuthState.LoggedOut
            },
            onFailure = { error ->
                _state.value = AuthState.Error(mapToAuthError(error))
            }
        )
    }

    private fun handleClearAuthState() {
        _state.value = AuthState.Idle
        currentRegistrationData = RegistrationData(phoneNumber = "")
        lastVerificationId = null
        lastPhoneNumber = null
    }

    // Error and Validation Handlers
    private fun handleRetryLastAction() {
        // TODO: Implement retry logic based on last action
        _state.value = AuthState.Idle
    }

    private fun handleDismissError() {
        _state.value = AuthState.Idle
    }

    private suspend fun handleValidatePhoneNumber(phoneNumber: String) {
        val result = authRepository.validatePhoneNumber(phoneNumber)
        
        result.fold(
            onSuccess = { formattedPhone ->
                _state.value = AuthState.PhoneValidationResult(true, formattedPhone)
            },
            onFailure = {
                _state.value = AuthState.PhoneValidationResult(false)
            }
        )
    }

    private fun handleValidateOtp(otp: String) {
        val isValid = otp.length == 6 && otp.all { it.isDigit() }
        _state.value = AuthState.OtpValidationResult(isValid)
    }

    private fun handleValidateProfileField(field: String, value: String) {
        // TODO: Implement field-specific validation
    }

    // Helper Methods
    private fun mapToAuthError(throwable: Throwable): AuthError {
        return when (throwable) {
            is NotImplementedError -> AuthError.CustomError("Funcionalidade ainda não implementada")
            is IllegalArgumentException -> AuthError.ValidationError("argument", throwable.message ?: "Argumento inválido")
            else -> AuthError.UnknownError(throwable)
        }
    }

    // Public helper methods for UI
    fun getCurrentRegistrationData(): RegistrationData = currentRegistrationData
    
    fun getProfileCompletionProgress(): Float {
        val currentState = _state.value
        return if (currentState is AuthState.ProfileCompletion) {
            currentState.currentStep.progress()
        } else {
            0f
        }
    }
}