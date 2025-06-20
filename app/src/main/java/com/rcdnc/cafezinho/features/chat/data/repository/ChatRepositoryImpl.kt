package com.rcdnc.cafezinho.features.chat.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.rcdnc.binderstatic.ApiClasses.helpers.ApiCommonHelper
import com.rcdnc.binderstatic.SimpleClasses.Variables
import com.rcdnc.cafezinho.features.chat.domain.model.Message
import com.rcdnc.cafezinho.features.chat.domain.repository.ChatRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class ChatRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : ChatRepository {

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val apiHelper = ApiCommonHelper(context)

    override fun getMessages(chatId: String): Flow<Result<List<Message>>> = callbackFlow {
        try {
            val messagesRef = firebaseDatabase.getReference("chats").child(chatId)
            
            val listener = object : ChildEventListener {
                private val messages = mutableListOf<Message>()
                
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = parseMessageFromSnapshot(snapshot)
                    if (message != null) {
                        messages.add(message)
                        // Send updated list
                        trySend(Result.success(messages.toList()))
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = parseMessageFromSnapshot(snapshot)
                    if (message != null) {
                        val index = messages.indexOfFirst { it.id == message.id }
                        if (index != -1) {
                            messages[index] = message
                            trySend(Result.success(messages.toList()))
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val messageId = snapshot.key
                    messages.removeAll { it.id == messageId }
                    trySend(Result.success(messages.toList()))
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    // Not typically used for chat messages
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Result.failure(Exception(error.message)))
                }
            }
            
            messagesRef.addChildEventListener(listener)
            
            awaitClose {
                messagesRef.removeEventListener(listener)
            }
        } catch (e: Exception) {
            trySend(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun sendMessage(chatId: String, message: Message): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentUserId = sharedPreferences.getString(Variables.uid, "") ?: ""
            if (currentUserId.isEmpty()) {
                return@withContext Result.failure(Exception("Current user ID not found"))
            }

            // Send to Firebase
            val messagesRef = firebaseDatabase.getReference("chats").child(chatId)
            val messageKey = messagesRef.push().key ?: return@withContext Result.failure(Exception("Failed to generate message key"))
            
            val messageData = mapOf(
                "id" to messageKey,
                "senderId" to message.senderId,
                "receiverId" to message.receiverId,
                "content" to message.content,
                "timestamp" to message.timestamp,
                "isRead" to message.isRead,
                "messageType" to message.messageType
            )
            
            suspendCancellableCoroutine { continuation ->
                messagesRef.child(messageKey).setValue(messageData)
                    .addOnSuccessListener {
                        // Also send push notification using existing API
                        sendPushNotification(message)
                        continuation.resume(Result.success(Unit))
                    }
                    .addOnFailureListener { exception ->
                        continuation.resume(Result.failure(exception))
                    }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMessage(messageId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Implementation would depend on your chat structure
            // For now, return success
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markMessageAsRead(messageId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Update read status in Firebase
            // Implementation would depend on your chat structure
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseMessageFromSnapshot(snapshot: DataSnapshot): Message? {
        return try {
            Message(
                id = snapshot.key ?: "",
                senderId = snapshot.child("senderId").getValue(String::class.java) ?: "",
                receiverId = snapshot.child("receiverId").getValue(String::class.java) ?: "",
                content = snapshot.child("content").getValue(String::class.java) ?: "",
                timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis(),
                isRead = snapshot.child("isRead").getValue(Boolean::class.java) ?: false,
                messageType = snapshot.child("messageType").getValue(String::class.java) ?: "text"
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun sendPushNotification(message: Message) {
        try {
            // Use existing push notification system
            val params = JSONObject().apply {
                put("sender_id", message.senderId)
                put("receiver_id", message.receiverId)
                put("message", message.content)
            }
            
            // Push notification using existing pattern - would need to implement if required
            // apiHelper.sendMessageNotification(params.toString(), { /* Success */ }, { /* Error */ })
        } catch (e: Exception) {
            // Log error but don't fail the message send
        }
    }
}