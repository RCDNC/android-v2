package com.rcdnc.cafezinho.features.auth.data.model

import com.google.gson.annotations.SerializedName

/**
 * API request/response models for authentication
 */

// Login Request
data class LoginRequest(
    @SerializedName("social_id")
    val socialId: String,
    
    @SerializedName("social")
    val social: String, // "google" or "facebook"
    
    @SerializedName("email")
    val email: String? = null,
    
    @SerializedName("web_id")
    val webId: String? = null, // Required for Google
    
    @SerializedName("auth_token")
    val authToken: String? = null // Required for Google
)

// Register Request
data class RegisterRequest(
    @SerializedName("social")
    val social: String,
    
    @SerializedName("social_id")
    val socialId: String,
    
    @SerializedName("school_id")
    val schoolId: Int,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("phone")
    val phone: String? = null,
    
    @SerializedName("dob")
    val dob: String,
    
    @SerializedName("first_name")
    val firstName: String,
    
    @SerializedName("last_name")
    val lastName: String? = null,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("gender")
    val gender: String,
    
    @SerializedName("gender_show")
    val genderShow: Int,
    
    @SerializedName("show_me_gender")
    val showMeGender: String,
    
    @SerializedName("main_interest")
    val mainInterest: Int? = null,
    
    @SerializedName("secondary_interest")
    val secondaryInterest: Int? = null,
    
    @SerializedName("user_sexual_orientation")
    val userSexualOrientation: List<Int>? = null,
    
    @SerializedName("show_orientation")
    val showOrientation: Int? = null,
    
    @SerializedName("user_passion")
    val userPassion: List<Int>? = null,
    
    @SerializedName("utm_source")
    val utmSource: String = "android_v2",
    
    @SerializedName("utm_medium")
    val utmMedium: String = "mobile_app",
    
    @SerializedName("utm_campaign")
    val utmCampaign: String = "organic"
)

// Auth Response
data class AuthResponse(
    @SerializedName("msg")
    val msg: AuthResponseData
)

data class AuthResponseData(
    @SerializedName("User")
    val user: ApiUser,
    
    @SerializedName("UserImage")
    val userImages: List<ApiUserImage>? = null,
    
    @SerializedName("UserPassion")
    val userPassions: List<ApiUserPassion>? = null,
    
    @SerializedName("School")
    val school: ApiSchool? = null
)

// API User Model
data class ApiUser(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("first_name")
    val firstName: String? = null,
    
    @SerializedName("last_name")
    val lastName: String? = null,
    
    @SerializedName("email")
    val email: String? = null,
    
    @SerializedName("phone")
    val phone: String? = null,
    
    @SerializedName("gender")
    val gender: String? = null,
    
    @SerializedName("show_me_gender")
    val showMeGender: String? = null,
    
    @SerializedName("main_interest")
    val mainInterest: Int? = null,
    
    @SerializedName("secondary_interest")
    val secondaryInterest: Int? = null,
    
    @SerializedName("bio")
    val bio: String? = null,
    
    @SerializedName("dob")
    val dob: String? = null,
    
    @SerializedName("package")
    val packageType: String? = null,
    
    @SerializedName("rewind")
    val rewind: Int? = null,
    
    @SerializedName("radius")
    val radius: Int? = null,
    
    @SerializedName("hide_me")
    val hideMe: Int? = null,
    
    @SerializedName("hide_age")
    val hideAge: Int? = null,
    
    @SerializedName("hide_location")
    val hideLocation: Int? = null,
    
    @SerializedName("role")
    val role: String? = null,
    
    @SerializedName("auth_token")
    val authToken: String,
    
    @SerializedName("created")
    val created: String? = null,
    
    @SerializedName("total_boost")
    val totalBoost: Int? = null,
    
    @SerializedName("boost")
    val boost: Int? = null,
    
    @SerializedName("wallet")
    val wallet: Int? = null,
    
    @SerializedName("personality")
    val personality: String? = null
)

// Supporting models
data class ApiUserImage(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("image_url")
    val imageUrl: String
)

data class ApiUserPassion(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String
)

data class ApiSchool(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("name")
    val name: String
)