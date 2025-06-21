package com.rcdnc.cafezinho.features.auth.mvi

/**
 * All possible user actions in the authentication flow
 * Following MVI pattern for state management
 */
sealed class AuthIntent {
    
    // Social Login Actions
    object LoginWithGoogle : AuthIntent()
    object LoginWithFacebook : AuthIntent()
    data class HandleSocialLoginResult(val provider: String, val result: Map<String, Any>) : AuthIntent()
    
    // Phone Authentication Actions
    data class SendPhoneVerification(val phoneNumber: String) : AuthIntent()
    data class VerifyPhoneOtp(val phoneNumber: String, val otp: String) : AuthIntent()
    object ResendPhoneOtp : AuthIntent()
    
    // Traditional Login (if needed)
    data class LoginWithEmail(val email: String, val password: String) : AuthIntent()
    data class LoginWithPhone(val phone: String, val otp: String) : AuthIntent()
    
    // Registration Actions
    data class StartRegistration(val phoneNumber: String) : AuthIntent()
    data class CompleteRegistration(val userProfile: RegistrationData) : AuthIntent()
    
    // Profile Completion Actions
    data class UpdateFirstName(val firstName: String) : AuthIntent()
    data class UpdateLastName(val lastName: String) : AuthIntent()
    data class UpdateDateOfBirth(val dateOfBirth: java.util.Date) : AuthIntent()
    data class UpdateGender(val gender: String) : AuthIntent()
    data class UpdateGenderPreference(val preference: String) : AuthIntent()
    data class UpdateBio(val bio: String) : AuthIntent()
    data class UpdateSchool(val school: String) : AuthIntent()
    data class UpdateProfilePhotos(val photos: List<String>) : AuthIntent()
    data class UpdateInterests(val interests: List<String>) : AuthIntent()
    data class UpdateAdditionalInfo(val school: String?, val bio: String?) : AuthIntent()
    
    // Profile Navigation
    object NextProfileStep : AuthIntent()
    object PreviousProfileStep : AuthIntent()
    object SkipProfileStep : AuthIntent()
    object CompleteProfile : AuthIntent()
    
    // Session Management
    object CheckAuthState : AuthIntent()
    object RefreshSession : AuthIntent()
    object Logout : AuthIntent()
    object ClearAuthState : AuthIntent()
    
    // Error Handling
    object RetryLastAction : AuthIntent()
    object DismissError : AuthIntent()
    
    // Validation
    data class ValidatePhoneNumber(val phoneNumber: String) : AuthIntent()
    data class ValidateOtp(val otp: String) : AuthIntent()
    data class ValidateProfileField(val field: String, val value: String) : AuthIntent()
}

/**
 * Registration data container for profile completion
 */
data class RegistrationData(
    val phoneNumber: String,
    val firstName: String? = null,
    val dateOfBirth: Long? = null,
    val gender: String? = null,
    val genderPreference: String? = null,
    val interests: List<String> = emptyList(),
    val photos: List<String> = emptyList(),
    val school: String? = null,
    val bio: String? = null
)