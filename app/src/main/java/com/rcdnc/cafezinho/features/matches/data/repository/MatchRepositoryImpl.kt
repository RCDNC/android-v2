package com.rcdnc.cafezinho.features.matches.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.rcdnc.binderstatic.ApiClasses.helpers.ApiCommonHelper
import com.rcdnc.binderstatic.SimpleClasses.Variables
import com.rcdnc.cafezinho.features.matches.domain.model.MatchData
import com.rcdnc.cafezinho.features.matches.domain.repository.MatchRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class MatchRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : MatchRepository {

    private val apiHelper = ApiCommonHelper(context)

    override fun getMatches(): Flow<Result<List<MatchData>>> = flow {
        try {
            val userId = sharedPreferences.getString(Variables.uid, "") ?: ""
            if (userId.isEmpty()) {
                emit(Result.failure(Exception("User ID not found")))
                return@flow
            }

            val result = suspendCancellableCoroutine { continuation ->
                // Using the real API call pattern from your codebase
                apiHelper.showUserMatchs(
                    userId,
                    { response ->
                        try {
                            val jsonResponse = response.body
                            val success = jsonResponse.optBoolean("success", false)
                            
                            if (success) {
                                val matchesArray = jsonResponse.optJSONArray("matches")
                                val matches = mutableListOf<MatchData>()
                                
                                if (matchesArray != null) {
                                    for (i in 0 until matchesArray.length()) {
                                        val matchJson = matchesArray.getJSONObject(i)
                                        val match = parseMatchFromJson(matchJson)
                                        matches.add(match)
                                    }
                                }
                                
                                continuation.resume(Result.success(matches))
                            } else {
                                val message = jsonResponse.optString("message", "Failed to fetch matches")
                                continuation.resume(Result.failure(Exception(message)))
                            }
                        } catch (e: Exception) {
                            continuation.resume(Result.failure(e))
                        }
                    },
                    { error ->
                        continuation.resume(Result.failure(Exception(error.message ?: "API call failed")))
                    }
                )
            }
            emit(result)
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getMatchDetails(matchId: String): Result<MatchData> = withContext(Dispatchers.IO) {
        try {
            // For match details, since the current API doesn't have a specific endpoint for single match details,
            // we get all matches and filter for the specific one
            suspendCancellableCoroutine { continuation ->
                val userId = sharedPreferences.getString(Variables.uid, "") ?: ""
                
                apiHelper.showUserMatchs(
                    userId,
                    { response ->
                        try {
                            val jsonResponse = response.body
                            val success = jsonResponse.optBoolean("success", false)
                            
                            if (success) {
                                val matchesArray = jsonResponse.optJSONArray("matches")
                                
                                if (matchesArray != null) {
                                    for (i in 0 until matchesArray.length()) {
                                        val matchJson = matchesArray.getJSONObject(i)
                                        val match = parseMatchFromJson(matchJson)
                                        
                                        if (match.matchId == matchId) {
                                            continuation.resume(Result.success(match))
                                            return@showUserMatchs
                                        }
                                    }
                                }
                                
                                continuation.resume(Result.failure(Exception("Match not found")))
                            } else {
                                val message = jsonResponse.optString("message", "Failed to fetch match details")
                                continuation.resume(Result.failure(Exception(message)))
                            }
                        } catch (e: Exception) {
                            continuation.resume(Result.failure(e))
                        }
                    },
                    { error ->
                        continuation.resume(Result.failure(Exception(error.message ?: "API call failed")))
                    }
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMatch(matchId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = sharedPreferences.getString(Variables.uid, "") ?: ""
            if (userId.isEmpty()) {
                return@withContext Result.failure(Exception("User ID not found"))
            }

            suspendCancellableCoroutine { continuation ->
                // Using the real deleteMatch API call pattern from your codebase
                apiHelper.deleteMatch(
                    userId,
                    matchId, // otherUserId in the API
                    { response ->
                        try {
                            val jsonResponse = response.body
                            val success = jsonResponse.optBoolean("success", false)
                            
                            if (success) {
                                continuation.resume(Result.success(Unit))
                            } else {
                                val message = jsonResponse.optString("message", "Failed to delete match")
                                continuation.resume(Result.failure(Exception(message)))
                            }
                        } catch (e: Exception) {
                            continuation.resume(Result.failure(e))
                        }
                    }
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseMatchFromJson(matchJson: JSONObject): MatchData {
        // Based on the actual MatchModel structure in your codebase
        return MatchData(
            matchId = matchJson.optString("id", ""),
            userId = matchJson.optString("user_id", ""),
            userName = matchJson.optString("firstName", ""),
            userPhoto = matchJson.optString("image"),
            timestamp = matchJson.optLong("created_at", System.currentTimeMillis()),
            lastMessage = matchJson.optString("last_message"),
            isNewMatch = matchJson.optBoolean("is_new", false)
        )
    }
}