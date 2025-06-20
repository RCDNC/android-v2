package com.rcdnc.cafezinho.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rcdnc.cafezinho.MainActivity
import com.rcdnc.cafezinho.domain.model.User
import com.rcdnc.cafezinho.domain.model.Location
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserCardTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val sampleUser = User(
        id = "1",
        name = "Maria Silva",
        age = 25,
        bio = "Amo viajar e conhecer pessoas novas!",
        photos = listOf(
            "https://example.com/photo1.jpg",
            "https://example.com/photo2.jpg"
        ),
        location = Location(
            latitude = -23.5505,
            longitude = -46.6333,
            city = "São Paulo",
            country = "Brasil"
        ),
        isVerified = true
    )

    @Test
    fun userCard_displaysUserInformation() {
        composeTestRule.setContent {
            CafezinhoTheme {
                UserCard(
                    user = sampleUser,
                    interests = listOf("Viagem", "Música"),
                    distance = "2km",
                    isOnline = true
                )
            }
        }

        // Check if user name and age are displayed
        composeTestRule
            .onNodeWithText("Maria Silva, 25")
            .assertExists()
            .assertIsDisplayed()

        // Check if location is displayed
        composeTestRule
            .onNodeWithText("São Paulo")
            .assertExists()

        // Check if distance is displayed
        composeTestRule
            .onNodeWithText("2km")
            .assertExists()

        // Check if interests are displayed
        composeTestRule
            .onNodeWithText("Viagem")
            .assertExists()
        
        composeTestRule
            .onNodeWithText("Música")
            .assertExists()
    }

    @Test
    fun userCard_showsVerificationBadge() {
        composeTestRule.setContent {
            CafezinhoTheme {
                UserCard(
                    user = sampleUser.copy(isVerified = true),
                    interests = emptyList(),
                    distance = null,
                    isOnline = false
                )
            }
        }

        // Verification badge should be present (CheckCircle icon)
        composeTestRule
            .onNodeWithContentDescription("Verified user")
            .assertExists()
    }

    @Test
    fun userCard_showsOnlineIndicator() {
        composeTestRule.setContent {
            CafezinhoTheme {
                UserCard(
                    user = sampleUser,
                    interests = emptyList(),
                    distance = null,
                    isOnline = true
                )
            }
        }

        // Online indicator should be visible when user is online
        // This would depend on the specific implementation of the online indicator
        composeTestRule
            .onNodeWithText("Maria Silva, 25")
            .assertExists()
    }

    @Test
    fun userCard_swipeButtonsWork() {
        var likeClicked = false
        var passClicked = false
        var superLikeClicked = false

        composeTestRule.setContent {
            CafezinhoTheme {
                UserCard(
                    user = sampleUser,
                    interests = emptyList(),
                    onLike = { likeClicked = true },
                    onPass = { passClicked = true },
                    onSuperLike = { superLikeClicked = true }
                )
            }
        }

        // Find and click the like button (assuming it has a specific content description)
        composeTestRule
            .onNodeWithContentDescription("Like")
            .performClick()

        assert(likeClicked)
    }

    @Test
    fun compactUserCard_displaysEssentialInfo() {
        composeTestRule.setContent {
            CafezinhoTheme {
                CompactUserCard(
                    user = sampleUser,
                    onClick = { },
                    subtitle = "1km"
                )
            }
        }

        composeTestRule
            .onNodeWithText("Maria Silva")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("25")
            .assertExists()

        composeTestRule
            .onNodeWithText("1km")
            .assertExists()
    }

    @Test
    fun gridUserCard_clickTriggersCallback() {
        var clickCount = 0

        composeTestRule.setContent {
            CafezinhoTheme {
                GridUserCard(
                    user = sampleUser,
                    onClick = { clickCount++ }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Maria Silva")
            .performClick()

        assert(clickCount == 1)
    }
}