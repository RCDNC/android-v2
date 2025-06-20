package com.rcdnc.cafezinho.features.swipe.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.rcdnc.binderstatic.ApiClasses.helpers.ApiCommonHelper
import com.rcdnc.binderstatic.SimpleClasses.Variables
import com.rcdnc.cafezinho.features.swipe.domain.model.Match
import com.rcdnc.cafezinho.features.swipe.domain.model.UserProfile
import com.rcdnc.cafezinho.features.swipe.domain.repository.UserRepository
import com.rcdnc.cafezinho.common.util.Converters.toUserProfile
import com.rcdnc.cafezinho.simpleClasses.UsersSwipeLegacy
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class UserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : UserRepository {

    private val legacySwipe = UsersSwipeLegacy(context)
    private val apiHelper = ApiCommonHelper(context)

    override suspend fun getRecommendedUsers(): Result<List<UserProfile>> = withContext(Dispatchers.IO) {
        try {
            suspendCancellableCoroutine { continuation ->
                legacySwipe.populateSwipe(object : UsersSwipeLegacy.INotify {
                    override fun notifyFragment(sizePopulated: Int) {
                        val users = legacySwipe.usersList.map { it.toUserProfile() }
                        continuation.resume(Result.success(users))
                    }
                })
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun likeUser(userId: String): Result<Match?> = withContext(Dispatchers.IO) {
        try {
            val currentUserId = sharedPreferences.getString(Variables.uid, "") ?: ""
            if (currentUserId.isEmpty()) {
                return@withContext Result.failure(Exception("Current user ID not found"))
            }

            suspendCancellableCoroutine { continuation ->
                val params = JSONObject().apply {
                    put("user_id", currentUserId)
                    put("like", 1)
                    put("super_like", 0)
                    put("other_user_id", userId)
                }

                // Use ApiCommonHelper directly to get detailed response
                apiHelper.likeUser(JSONObject::class.java, params.toString(),
                    { response ->
                        try {
                            val jsonResponse = response.body
                            
                            // Check if it's a match (status 201 typically indicates match)
                            if (response.status == HttpStatusCode.Created) {
                                val match = Match(
                                    matchId = jsonResponse.optString("match_id", ""),
                                    otherUserId = userId,
                                    timestamp = System.currentTimeMillis()
                                )
                                continuation.resume(Result.success(match))
                            } else {
                                // Like successful but no match
                                continuation.resume(Result.success(null))
                            }
                        } catch (e: Exception) {
                            continuation.resume(Result.failure(e))
                        }
                    },
                    { error ->
                        continuation.resume(Result.failure(Exception(error.message ?: "Like failed")))
                    }
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun dislikeUser(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            suspendCancellableCoroutine { continuation ->
                legacySwipe.createLikeOrDislike(0, object : UsersSwipeLegacy.INotify {
                    override fun notifyFragment(sizePopulated: Int) {
                        continuation.resume(Result.success(Unit))
                    }
                })
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun superLikeUser(userId: String): Result<Match?> = withContext(Dispatchers.IO) {
        try {
            val currentUserId = sharedPreferences.getString(Variables.uid, "") ?: ""
            if (currentUserId.isEmpty()) {
                return@withContext Result.failure(Exception("Current user ID not found"))
            }

            suspendCancellableCoroutine { continuation ->
                val params = JSONObject().apply {
                    put("user_id", currentUserId)
                    put("like", 0)
                    put("super_like", 1)
                    put("other_user_id", userId)
                }

                // Use ApiCommonHelper directly to get detailed response
                apiHelper.likeUser(JSONObject::class.java, params.toString(),
                    { response ->
                        try {
                            val jsonResponse = response.body
                            
                            // Check if it's a match (status 201 typically indicates match)
                            if (response.status == HttpStatusCode.Created) {
                                val match = Match(
                                    matchId = jsonResponse.optString("match_id", ""),
                                    otherUserId = userId,
                                    timestamp = System.currentTimeMillis()
                                )
                                continuation.resume(Result.success(match))
                            } else {
                                // Super like successful but no match
                                continuation.resume(Result.success(null))
                            }
                        } catch (e: Exception) {
                            continuation.resume(Result.failure(e))
                        }
                    },
                    { error ->
                        continuation.resume(Result.failure(Exception(error.message ?: "Super like failed")))
                    }
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rewindLastAction(): Result<UserProfile?> = withContext(Dispatchers.IO) {
        try {
            suspendCancellableCoroutine { continuation ->
                legacySwipe.rewindLike(object : UsersSwipeLegacy.INotify {
                    override fun notifyFragment(sizePopulated: Int) {
                        // TODO: Return rewound user
                        continuation.resume(Result.success(null))
                    }
                })
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}