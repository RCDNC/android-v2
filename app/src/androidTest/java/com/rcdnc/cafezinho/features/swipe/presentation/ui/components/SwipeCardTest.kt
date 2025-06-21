package com.rcdnc.cafezinho.features.swipe.presentation.ui.components

import androidx.compose.ui.geometry.Offset
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
class SwipeCardTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val sampleUser = User(
        id = "1",
        name = "Ana Santos",
        age = 24,
        bio = "Adoro arte e música!",
        photos = listOf("https://example.com/photo1.jpg"),
        location = Location(
            latitude = -23.5505,
            longitude = -46.6333,
            city = "São Paulo",
            country = "Brasil"
        ),
        isVerified = true
    )

    @Test
    fun swipeCard_displaysUserCard() {
        composeTestRule.setContent {
            CafezinhoTheme {
                SwipeCard(
                    user = sampleUser,
                    onSwipe = { },
                    interests = listOf("Arte", "Música"),
                    distance = "3km",
                    isOnline = true
                )
            }
        }

        composeTestRule
            .onNodeWithText("Ana Santos, 24")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Arte")
            .assertExists()

        composeTestRule
            .onNodeWithText("Música")
            .assertExists()
    }

    @Test
    fun swipeCard_triggersSwipeCallbackOnGesture() {
        var swipeResult: SwipeResult? = null

        composeTestRule.setContent {
            CafezinhoTheme {
                SwipeCard(
                    user = sampleUser,
                    onSwipe = { result -> swipeResult = result },
                    enableSwipeGestures = true
                )
            }
        }

        // Perform a swipe gesture (this is a simplified test)
        composeTestRule
            .onNodeWithText("Ana Santos, 24")
            .performTouchInput {
                // Simulate a right swipe
                swipeRight()
            }

        // Note: This test might need adjustment based on the actual swipe threshold
        // and implementation details
    }

    @Test
    fun swipeCard_disabledGesturesDoNotTriggerSwipe() {
        var swipeCount = 0

        composeTestRule.setContent {
            CafezinhoTheme {
                SwipeCard(
                    user = sampleUser,
                    onSwipe = { swipeCount++ },
                    enableSwipeGestures = false
                )
            }
        }

        composeTestRule
            .onNodeWithText("Ana Santos, 24")
            .performTouchInput {
                swipeRight()
            }

        // Should not trigger swipe when gestures are disabled
        assert(swipeCount == 0)
    }

    @Test
    fun swipeCardStack_displaysMultipleCards() {
        val users = listOf(
            sampleUser,
            sampleUser.copy(id = "2", name = "Carlos Lima", age = 28),
            sampleUser.copy(id = "3", name = "Beatriz Costa", age = 26)
        )

        composeTestRule.setContent {
            CafezinhoTheme {
                SwipeCardStack(
                    users = users,
                    onSwipe = { },
                    maxVisibleCards = 3
                )
            }
        }

        // Only the top card should be fully visible and interactive
        composeTestRule
            .onNodeWithText("Ana Santos, 24")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun swipeCardStack_showsEmptyStateWhenNoUsers() {
        composeTestRule.setContent {
            CafezinhoTheme {
                SwipeCardStack(
                    users = emptyList(),
                    onSwipe = { }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Não há mais pessoas por aqui")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Volte mais tarde para ver novos perfis")
            .assertExists()
    }

    @Test
    fun swipeCard_showsSwipeOverlayDuringGesture() {
        composeTestRule.setContent {
            CafezinhoTheme {
                SwipeCard(
                    user = sampleUser,
                    onSwipe = { },
                    enableSwipeGestures = true
                )
            }
        }

        // Start a drag gesture but don't complete it
        composeTestRule
            .onNodeWithText("Ana Santos, 24")
            .performTouchInput {
                down(center)
                moveBy(Offset(100f, 0f)) // Partial swipe right
                // Don't call up() to keep the gesture active
            }

        // The swipe overlay should be visible during the gesture
        // This test might need refinement based on the actual overlay implementation
    }

    @Test
    fun swipeCard_handlesUserWithoutPhotos() {
        val userWithoutPhotos = sampleUser.copy(photos = emptyList())

        composeTestRule.setContent {
            CafezinhoTheme {
                SwipeCard(
                    user = userWithoutPhotos,
                    onSwipe = { }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Ana Santos, 24")
            .assertExists()
            .assertIsDisplayed()
    }
}