package com.rcdnc.cafezinho.features.profile.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.rcdnc.binderstatic.ApiClasses.ApiUrl
import com.rcdnc.binderstatic.ApiClasses.helpers.ApiCommonHelper
import com.rcdnc.binderstatic.SimpleClasses.Variables
import com.rcdnc.cafezinho.features.profile.domain.model.ProfileData
import com.rcdnc.cafezinho.features.profile.domain.repository.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : ProfileRepository {

    private val apiHelper = ApiCommonHelper(context)

    override suspend fun getProfile(userId: String): Result<ProfileData> = withContext(Dispatchers.IO) {
        try {
            suspendCancellableCoroutine { continuation ->
                // Using existing API to get user details - simplified for now
                // Note: Actual implementation would use existing user API calls
                try {
                    // For now, create basic profile from SharedPreferences if it's current user
                    val currentUserId = sharedPreferences.getString(Variables.uid, "") ?: ""
                    if (userId == currentUserId) {
                        val profile = ProfileData(
                            id = currentUserId,
                            firstName = sharedPreferences.getString(Variables.fName, "") ?: "",
                            lastName = sharedPreferences.getString(Variables.lName, "") ?: "",
                            age = 0, // Would need to calculate from DOB
                            bio = sharedPreferences.getString("bio", "") ?: "",
                            website = sharedPreferences.getString("website", "") ?: "",
                            jobTitle = sharedPreferences.getString("jobTitle", "") ?: "",
                            company = sharedPreferences.getString("company", "") ?: "",
                            school = sharedPreferences.getString("school", "") ?: ""
                        )
                        continuation.resume(Result.success(profile))
                    } else {
                        // For other users, would need API call
                        continuation.resume(Result.failure(Exception("API call for other users not implemented yet")))
                    }
                } catch (e: Exception) {
                    continuation.resume(Result.failure(e))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(profile: ProfileData): Result<ProfileData> = withContext(Dispatchers.IO) {
        try {
            val currentUserId = sharedPreferences.getString(Variables.uid, "") ?: ""
            if (currentUserId.isEmpty()) {
                return@withContext Result.failure(Exception("Current user ID not found"))
            }

            // For now, just update SharedPreferences
            // Real implementation would use API call
            updateSharedPreferences(profile)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfileImage(imageBytes: ByteArray): Result<String> = withContext(Dispatchers.IO) {
        try {
            val currentUserId = sharedPreferences.getString(Variables.uid, "") ?: ""
            if (currentUserId.isEmpty()) {
                return@withContext Result.failure(Exception("Current user ID not found"))
            }

            // For now, return mock URL - real implementation would upload to server
            Result.success("https://example.com/uploaded-image.jpg")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProfileImage(imageUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentUserId = sharedPreferences.getString(Variables.uid, "") ?: ""
            if (currentUserId.isEmpty()) {
                return@withContext Result.failure(Exception("Current user ID not found"))
            }

            // For now, return success - real implementation would delete from server
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseProfileFromJson(userData: JSONObject): ProfileData {
        return ProfileData(
            id = userData.optString("id", ""),
            firstName = userData.optString("firstName", ""),
            lastName = userData.optString("lastName", ""),
            bio = userData.optString("bio", ""),
            website = userData.optString("website", ""),
            jobTitle = userData.optString("jobTitle", ""),
            company = userData.optString("company", ""),
            school = userData.optString("school", ""),
            age = calculateAgeFromDob(userData.optString("dob", "")),
            photos = parsePhotosFromJson(userData),
            interests = parseInterestsFromJson(userData)
        )
    }

    private fun updateSharedPreferences(profile: ProfileData) {
        sharedPreferences.edit().apply {
            putString(Variables.fName, profile.firstName)
            putString(Variables.lName, profile.lastName)
            // Update other relevant SharedPreferences
            apply()
        }
    }

    private fun calculateAgeFromDob(dob: String): Int {
        return try {
            if (dob.isNotEmpty()) {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val birthDate = sdf.parse(dob)
                val birthCalendar = java.util.Calendar.getInstance()
                birthCalendar.time = birthDate
                val today = java.util.Calendar.getInstance()
                var age = today.get(java.util.Calendar.YEAR) - birthCalendar.get(java.util.Calendar.YEAR)
                if (today.get(java.util.Calendar.DAY_OF_YEAR) < birthCalendar.get(java.util.Calendar.DAY_OF_YEAR)) {
                    age--
                }
                age
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    private fun parsePhotosFromJson(userData: JSONObject): List<String> {
        val photos = mutableListOf<String>()
        
        // Add main profile image
        val mainImage = userData.optString("image", "")
        if (mainImage.isNotEmpty()) {
            photos.add(mainImage)
        }
        
        // Add additional images from images array
        val imagesArray = userData.optJSONArray("images")
        if (imagesArray != null) {
            for (i in 0 until imagesArray.length()) {
                val imageObj = imagesArray.optJSONObject(i)
                val imageUrl = imageObj?.optString("image", "")
                if (!imageUrl.isNullOrEmpty()) {
                    photos.add(imageUrl)
                }
            }
        }
        
        return photos
    }

    private fun parseInterestsFromJson(userData: JSONObject): List<String> {
        val interests = mutableListOf<String>()
        
        val passionsArray = userData.optJSONArray("passions")
        if (passionsArray != null) {
            for (i in 0 until passionsArray.length()) {
                val passionObj = passionsArray.optJSONObject(i)
                val title = passionObj?.optString("title", "")
                if (!title.isNullOrEmpty()) {
                    interests.add(title)
                }
            }
        }
        
        return interests
    }

    private fun extractImageIdFromUrl(imageUrl: String): String {
        // Extract image ID from URL - implementation depends on your URL structure
        // For now, return the last part of the URL
        return imageUrl.substringAfterLast("/").substringBefore(".")
    }
}