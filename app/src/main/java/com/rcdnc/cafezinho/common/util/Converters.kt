package com.rcdnc.cafezinho.common.util

import com.rcdnc.binderstatic.Models.User.UserDetailsModel
import com.rcdnc.cafezinho.features.swipe.domain.model.UserProfile

object Converters {
    
    fun UserDetailsModel.toUserProfile(): UserProfile {
        // Calculate age from DOB
        val age = dob?.let { dobString ->
            try {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val birthDate = sdf.parse(dobString)
                val birthCalendar = java.util.Calendar.getInstance()
                birthCalendar.time = birthDate
                val today = java.util.Calendar.getInstance()
                var age = today.get(java.util.Calendar.YEAR) - birthCalendar.get(java.util.Calendar.YEAR)
                if (today.get(java.util.Calendar.DAY_OF_YEAR) < birthCalendar.get(java.util.Calendar.DAY_OF_YEAR)) {
                    age--
                }
                age
            } catch (e: Exception) {
                0
            }
        } ?: 0
        
        return UserProfile(
            id = this.id.toString(),
            name = this.firstName ?: "",
            age = age,
            photos = images?.mapNotNull { it.image } ?: listOfNotNull(this.image),
            bio = this.bio,
            distance = this.distance?.toFloatOrNull(),
            interests = passions?.map { it.title ?: "" } ?: emptyList(),
            isVerified = this.verified == 1,
            src = this.src
        )
    }
    
    fun UserProfile.toUserDetailsModel(): UserDetailsModel {
        // Create a basic UserDetailsModel - this is a simplified conversion
        // since UserDetailsModel has many required fields that UserProfile doesn't have
        return UserDetailsModel(
            id = this.id.toIntOrNull(),
            firstName = this.name,
            lastName = null,
            gender = null,
            genderShow = null,
            showOrientation = null,
            bio = this.bio,
            website = null,
            boostedLike = "0",
            dob = null, // TODO: Calculate from age
            socialId = null,
            email = null,
            phone = null,
            image = this.photos.firstOrNull(),
            role = null,
            username = null,
            social = null,
            deviceToken = null,
            token = null,
            active = null,
            lat = null,
            long = null,
            online = 0,
            verified = if (this.isVerified) 1 else 0,
            authToken = null,
            version = null,
            device = null,
            ip = null,
            countryId = null,
            jobTitle = null,
            company = null,
            school = null,
            schoolId = null,
            hideMe = null,
            hideAge = null,
            minAge = null,
            maxAge = null,
            hideLocation = null,
            radius = null,
            showMeGender = null,
            showMeDistanceIn = null,
            wallet = null,
            paypal = null,
            fake = null,
            boost = null,
            totalBoost = null,
            boostDatetime = null,
            totalSuperlike = null,
            totalLikePerDay = null,
            remainingSwipes = null,
            showGrid = null,
            tokenInstagram = null,
            userIdInstagram = null,
            mainInterest = null,
            secondaryInterest = null,
            personality = null,
            lastSeen = null,
            created = null,
            distance = this.distance?.toString(),
            superLike = "0",
            src = this.src,
            currentImageIndex = 0,
            like = null,
            passions = null,
            sexualOrientations = null,
            images = null,
            type = null
        )
    }
}