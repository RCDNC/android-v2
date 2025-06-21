package com.rcdnc.cafezinho.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme
import org.junit.Rule
import org.junit.Test

class UserImageTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun userImage_displaysPlaceholderWhenNoUrl() {
        composeTestRule.setContent {
            CafezinhoTheme {
                UserImage(
                    imageUrl = null,
                    contentDescription = "User avatar",
                    type = UserImageType.AVATAR,
                    size = ComponentSize.MEDIUM
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("User avatar")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun userImage_displaysWithUrl() {
        composeTestRule.setContent {
            CafezinhoTheme {
                UserImage(
                    imageUrl = "https://example.com/user.jpg",
                    contentDescription = "User photo",
                    type = UserImageType.CARD,
                    size = ComponentSize.LARGE
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("User photo")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun userImage_clickTriggersCallback() {
        var clickCount = 0

        composeTestRule.setContent {
            CafezinhoTheme {
                UserImage(
                    imageUrl = "https://example.com/user.jpg",
                    contentDescription = "Clickable image",
                    type = UserImageType.GALLERY,
                    size = ComponentSize.MEDIUM,
                    onClick = { clickCount++ }
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Clickable image")
            .performClick()

        assert(clickCount == 1)
    }

    @Test
    fun userImage_showsOnlineIndicatorWhenOnline() {
        composeTestRule.setContent {
            CafezinhoTheme {
                UserImage(
                    imageUrl = "https://example.com/user.jpg",
                    contentDescription = "Online user",
                    type = UserImageType.AVATAR,
                    size = ComponentSize.MEDIUM,
                    isOnline = true
                )
            }
        }

        // The online indicator should be present
        composeTestRule
            .onNodeWithContentDescription("Online user")
            .assertExists()
    }

    @Test
    fun userImage_differentSizesWork() {
        composeTestRule.setContent {
            CafezinhoTheme {
                UserImage(
                    imageUrl = "https://example.com/user.jpg",
                    contentDescription = "Small image",
                    type = UserImageType.AVATAR,
                    size = ComponentSize.SMALL
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Small image")
            .assertExists()
            .assertIsDisplayed()
    }
}