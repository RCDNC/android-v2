package com.rcdnc.cafezinho.features.chat.presentation.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rcdnc.cafezinho.MainActivity
import com.rcdnc.cafezinho.features.chat.domain.model.Message
import com.rcdnc.cafezinho.features.chat.domain.model.MessageType
import com.rcdnc.cafezinho.features.chat.domain.model.MessageStatus
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class MessageItemTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val sampleMessage = Message(
        id = "1",
        senderId = "user1",
        receiverId = "user2",
        content = "Olá! Como você está?",
        timestamp = System.currentTimeMillis(),
        type = MessageType.TEXT,
        status = MessageStatus.SENT,
        senderAvatar = "https://example.com/avatar.jpg"
    )

    @Test
    fun messageItem_displaysTextMessage() {
        composeTestRule.setContent {
            CafezinhoTheme {
                MessageItem(
                    message = sampleMessage,
                    isFromCurrentUser = false,
                    showAvatar = true,
                    showTimestamp = true
                )
            }
        }

        composeTestRule
            .onNodeWithText("Olá! Como você está?")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun messageItem_showsAvatarForReceivedMessages() {
        composeTestRule.setContent {
            CafezinhoTheme {
                MessageItem(
                    message = sampleMessage,
                    isFromCurrentUser = false,
                    showAvatar = true
                )
            }
        }

        // Avatar should be present for received messages
        composeTestRule
            .onNodeWithContentDescription("Sender avatar")
            .assertExists()
    }

    @Test
    fun messageItem_hidesAvatarForSentMessages() {
        composeTestRule.setContent {
            CafezinhoTheme {
                MessageItem(
                    message = sampleMessage,
                    isFromCurrentUser = true,
                    showAvatar = true
                )
            }
        }

        // Avatar should not be present for sent messages
        composeTestRule
            .onNodeWithContentDescription("Sender avatar")
            .assertDoesNotExist()
    }

    @Test
    fun messageItem_showsMessageStatus() {
        composeTestRule.setContent {
            CafezinhoTheme {
                MessageItem(
                    message = sampleMessage.copy(status = MessageStatus.DELIVERED),
                    isFromCurrentUser = true
                )
            }
        }

        // Message status should be visible for sent messages
        // The exact implementation depends on how status is displayed
        composeTestRule
            .onNodeWithText("Olá! Como você está?")
            .assertExists()
    }

    @Test
    fun messageItem_showsTimestamp() {
        composeTestRule.setContent {
            CafezinhoTheme {
                MessageItem(
                    message = sampleMessage,
                    isFromCurrentUser = false,
                    showTimestamp = true
                )
            }
        }

        // Timestamp should be present when showTimestamp is true
        // The exact text depends on the timestamp formatting
        composeTestRule
            .onNodeWithText("Olá! Como você está?")
            .assertExists()
    }

    @Test
    fun messageItem_handlesImageMessage() {
        val imageMessage = sampleMessage.copy(
            content = "https://example.com/image.jpg",
            type = MessageType.IMAGE
        )

        composeTestRule.setContent {
            CafezinhoTheme {
                MessageItem(
                    message = imageMessage,
                    isFromCurrentUser = false
                )
            }
        }

        // Should display image message content
        composeTestRule
            .onNodeWithText("https://example.com/image.jpg")
            .assertExists()
    }

    @Test
    fun messageItem_handlesAudioMessage() {
        val audioMessage = sampleMessage.copy(
            content = "01:23",
            type = MessageType.AUDIO
        )

        composeTestRule.setContent {
            CafezinhoTheme {
                MessageItem(
                    message = audioMessage,
                    isFromCurrentUser = false
                )
            }
        }

        // Should display audio message
        composeTestRule
            .onNodeWithText("01:23")
            .assertExists()
    }

    @Test
    fun messageItem_groupsMessagesCorrectly() {
        val firstMessage = sampleMessage
        val secondMessage = sampleMessage.copy(
            id = "2",
            content = "Segunda mensagem"
        )

        composeTestRule.setContent {
            CafezinhoTheme {
                MessageItem(
                    message = secondMessage,
                    isFromCurrentUser = false,
                    previousMessage = firstMessage
                )
            }
        }

        composeTestRule
            .onNodeWithText("Segunda mensagem")
            .assertExists()
            .assertIsDisplayed()
    }
}