package com.rcdnc.cafezinho.integration

import com.rcdnc.cafezinho.core.network.ApiConfig
import com.rcdnc.cafezinho.features.swipe.data.remote.SwipeApiService
import com.rcdnc.cafezinho.features.swipe.data.remote.dto.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.MockResponse
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Testes de integração para SwipeApiService
 * Testa integração real com endpoints da API Laravel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SwipeApiIntegrationTest {
    
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: SwipeApiService
    
    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/api/"))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        apiService = retrofit.create(SwipeApiService::class.java)
    }
    
    @Test
    fun `getNearbyUsers should parse response correctly`() = runTest {
        // Given - mock server response
        val responseJson = """
            {
                "success": true,
                "message": "Users found",
                "data": [
                    {
                        "id": 1,
                        "first_name": "Ana",
                        "last_name": "Silva",
                        "age": 25,
                        "bio": "Adoro viajar!",
                        "location": "São Paulo, SP",
                        "distance": "2 km",
                        "photos": [
                            {
                                "id": "1",
                                "url": "https://example.com/photo1.jpg",
                                "order_sequence": 0,
                                "is_main_photo": true
                            }
                        ],
                        "interests": ["Viajar", "Música"],
                        "job_title": "Designer",
                        "is_verified": true,
                        "is_premium": false,
                        "is_online": true,
                        "rating": 4.5,
                        "profile_completion": 85
                    }
                ]
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .setHeader("Content-Type", "application/json")
        )
        
        // When - calling API
        val response = apiService.getNearbyUsers(
            userId = 123,
            minAge = 20,
            maxAge = 30,
            radius = 50
        )
        
        // Then - should parse response correctly
        assertTrue(response.isSuccessful)
        val body = response.body()!!
        assertTrue(body.success)
        assertEquals("Users found", body.message)
        assertEquals(1, body.data.size)
        
        val user = body.data[0]
        assertEquals(1, user.id)
        assertEquals("Ana", user.firstName)
        assertEquals("Silva", user.lastName)
        assertEquals(25, user.age)
        assertEquals("Adoro viajar!", user.bio)
        assertEquals("São Paulo, SP", user.location)
        assertEquals("2 km", user.distance)
        assertEquals(1, user.photos?.size)
        assertEquals(2, user.interests?.size)
        assertEquals("Designer", user.jobTitle)
        assertEquals(true, user.isVerified)
        assertEquals(false, user.isPremium)
        assertEquals(true, user.isOnline)
        assertEquals(4.5, user.rating ?: 0.0, 0.1)
        assertEquals(85, user.profileCompletion)
        
        // Verify photo details
        val photo = user.photos!![0]
        assertEquals("1", photo.id)
        assertEquals("https://example.com/photo1.jpg", photo.url)
        assertEquals(0, photo.orderSequence)
        assertEquals(true, photo.isMainPhoto)
        
        // Verify request was made correctly
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertTrue(request.path!!.contains("/api/user/showNearByUsers/123"))
        assertTrue(request.path!!.contains("min_age=20"))
        assertTrue(request.path!!.contains("max_age=30"))
        assertTrue(request.path!!.contains("radius=50"))
    }
    
    @Test
    fun `performLikeAction should send correct request and parse response`() = runTest {
        // Given - mock server response for like action
        val responseJson = """
            {
                "success": true,
                "message": "Like sent successfully",
                "data": {
                    "is_match": true,
                    "match_id": "match123",
                    "match_message": "É um match!",
                    "likes_remaining": 95,
                    "super_likes_remaining": 5
                }
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .setHeader("Content-Type", "application/json")
        )
        
        // When - performing like action
        val request = LikeActionRequest(
            userId = 123,
            targetUserId = 456,
            action = "like"
        )
        
        val response = apiService.performLikeAction(request)
        
        // Then - should parse response correctly
        assertTrue(response.isSuccessful)
        val body = response.body()!!
        assertTrue(body.success)
        assertEquals("Like sent successfully", body.message)
        
        val data = body.data!!
        assertTrue(data.isMatch)
        assertEquals("match123", data.matchId)
        assertEquals("É um match!", data.matchMessage)
        assertEquals(95, data.likesRemaining)
        assertEquals(5, data.superLikesRemaining)
        
        // Verify request was sent correctly
        val sentRequest = mockWebServer.takeRequest()
        assertEquals("POST", sentRequest.method)
        assertTrue(sentRequest.path!!.contains("/api/consumable/like"))
        assertEquals("application/json; charset=utf-8", sentRequest.getHeader("Content-Type"))
        
        // Verify request body contains correct data
        val requestBody = sentRequest.body.readUtf8()
        assertTrue(requestBody.contains("\"user_id\":123"))
        assertTrue(requestBody.contains("\"target_user_id\":456"))
        assertTrue(requestBody.contains("\"action\":\"like\""))
    }
    
    @Test
    fun `getUserConsumables should parse metrics correctly`() = runTest {
        // Given - mock server response for user consumables
        val responseJson = """
            {
                "success": true,
                "message": "Consumables retrieved",
                "data": {
                    "daily_likes_used": 15,
                    "daily_likes_limit": 100,
                    "super_likes_used": 2,
                    "super_likes_limit": 5,
                    "rewinds_used": 1,
                    "rewinds_limit": 3,
                    "is_premium": true,
                    "can_use_rewind": true,
                    "can_use_super_like": true
                }
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .setHeader("Content-Type", "application/json")
        )
        
        // When - getting user consumables
        val response = apiService.getUserConsumables(123)
        
        // Then - should parse response correctly
        assertTrue(response.isSuccessful)
        val body = response.body()!!
        assertTrue(body.success)
        assertEquals("Consumables retrieved", body.message)
        
        val data = body.data!!
        assertEquals(15, data.dailyLikesUsed)
        assertEquals(100, data.dailyLikesLimit)
        assertEquals(2, data.superLikesUsed)
        assertEquals(5, data.superLikesLimit)
        assertEquals(1, data.rewindsUsed)
        assertEquals(3, data.rewindsLimit)
        assertTrue(data.isPremium)
        assertTrue(data.canUseRewind)
        assertTrue(data.canUseSuperLike)
        
        // Verify request path
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertTrue(request.path!!.contains("/api/consumable/user/123"))
    }
    
    @Test
    fun `updateUserPreferences should send correct request`() = runTest {
        // Given - mock server response
        val responseJson = """
            {
                "success": true,
                "message": "Preferences updated successfully",
                "data": null
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .setHeader("Content-Type", "application/json")
        )
        
        // When - updating user preferences
        val preferences = UserPreferencesDto(
            minAge = 22,
            maxAge = 35,
            maxDistance = 75,
            genderPreference = "female",
            showOnlineOnly = true,
            showVerifiedOnly = false,
            requiredInterests = listOf("Viajar", "Música", "Esportes")
        )
        
        val response = apiService.updateUserPreferences(123, preferences)
        
        // Then - should receive success response
        assertTrue(response.isSuccessful)
        val body = response.body()!!
        assertTrue(body.success)
        assertEquals("Preferences updated successfully", body.message)
        
        // Verify request was sent correctly
        val request = mockWebServer.takeRequest()
        assertEquals("PUT", request.method)
        assertTrue(request.path!!.contains("/api/user/preferences/123"))
        
        // Verify request body
        val requestBody = request.body.readUtf8()
        assertTrue(requestBody.contains("\"min_age\":22"))
        assertTrue(requestBody.contains("\"max_age\":35"))
        assertTrue(requestBody.contains("\"max_distance\":75"))
        assertTrue(requestBody.contains("\"gender_preference\":\"female\""))
        assertTrue(requestBody.contains("\"show_online_only\":true"))
        assertTrue(requestBody.contains("\"show_verified_only\":false"))
        assertTrue(requestBody.contains("\"required_interests\":[\"Viajar\",\"Música\",\"Esportes\"]"))
    }
    
    @Test
    fun `rewindAction should handle DELETE request correctly`() = runTest {
        // Given - mock server response for rewind
        val responseJson = """
            {
                "success": true,
                "message": "Action reverted successfully",
                "data": {
                    "id": 456,
                    "first_name": "Bruno",
                    "last_name": "Costa",
                    "age": 28,
                    "bio": "Gosto de esportes",
                    "photos": []
                }
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .setHeader("Content-Type", "application/json")
        )
        
        // When - performing rewind action
        val response = apiService.rewindAction(123, 456)
        
        // Then - should parse response correctly
        assertTrue(response.isSuccessful)
        val body = response.body()!!
        assertTrue(body.success)
        assertEquals("Action reverted successfully", body.message)
        
        val userData = body.data!!
        assertEquals(456, userData.id)
        assertEquals("Bruno", userData.firstName)
        assertEquals("Costa", userData.lastName)
        assertEquals(28, userData.age)
        assertEquals("Gosto de esportes", userData.bio)
        
        // Verify DELETE request was sent correctly
        val request = mockWebServer.takeRequest()
        assertEquals("DELETE", request.method)
        assertTrue(request.path!!.contains("/api/consumable/like/123/456"))
    }
    
    @Test
    fun `API error response should be handled correctly`() = runTest {
        // Given - mock server error response
        val responseJson = """
            {
                "success": false,
                "message": "User not found",
                "data": null
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody(responseJson)
                .setHeader("Content-Type", "application/json")
        )
        
        // When - calling API
        val response = apiService.getNearbyUsers(999)
        
        // Then - should handle error response
        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
    }
    
    @Test
    fun `network timeout should be handled`() = runTest {
        // Given - server that doesn't respond (timeout)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBodyDelay(2, TimeUnit.SECONDS) // Longer than client timeout
        )
        
        // When/Then - should throw exception due to timeout
        try {
            apiService.getNearbyUsers(123)
            fail("Expected timeout exception")
        } catch (e: Exception) {
            // Expected timeout exception
            assertTrue(e.message?.contains("timeout") == true || 
                      e is java.net.SocketTimeoutException ||
                      e is java.io.IOException)
        }
    }
    
    @Test
    fun `malformed JSON response should be handled`() = runTest {
        // Given - malformed JSON response
        val malformedJson = """
            {
                "success": true,
                "message": "Success",
                "data": [
                    {
                        "id": "not_a_number",
                        "first_name": 123,
                        // Missing closing bracket
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(malformedJson)
                .setHeader("Content-Type", "application/json")
        )
        
        // When/Then - should handle JSON parsing error
        try {
            val response = apiService.getNearbyUsers(123)
            if (response.isSuccessful) {
                response.body() // This should throw parsing exception
            }
            fail("Expected JSON parsing exception")
        } catch (e: Exception) {
            // Expected JSON parsing exception
            assertTrue(e is com.google.gson.JsonSyntaxException || 
                      e is java.lang.IllegalStateException ||
                      e.message?.contains("JSON") == true)
        }
    }
    
    @Test
    fun `markUserAsViewed should send POST request correctly`() = runTest {
        // Given - mock server response
        val responseJson = """
            {
                "success": true,
                "message": "User marked as viewed",
                "data": null
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .setHeader("Content-Type", "application/json")
        )
        
        // When - marking user as viewed
        val request = ViewUserRequest(
            viewerId = 123,
            viewedUserId = 456
        )
        
        val response = apiService.markUserAsViewed(request)
        
        // Then - should receive success response
        assertTrue(response.isSuccessful)
        val body = response.body()!!
        assertTrue(body.success)
        assertEquals("User marked as viewed", body.message)
        
        // Verify request
        val sentRequest = mockWebServer.takeRequest()
        assertEquals("POST", sentRequest.method)
        assertTrue(sentRequest.path!!.contains("/api/user/view"))
        
        val requestBody = sentRequest.body.readUtf8()
        assertTrue(requestBody.contains("\"viewer_id\":123"))
        assertTrue(requestBody.contains("\"viewed_user_id\":456"))
    }
}