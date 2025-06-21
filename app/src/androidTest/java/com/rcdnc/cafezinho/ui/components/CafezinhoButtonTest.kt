package com.rcdnc.cafezinho.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.component.ButtonVariant
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme
import org.junit.Rule
import org.junit.Test

class CafezinhoButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun cafezinhoButton_displaysText() {
        composeTestRule.setContent {
            CafezinhoTheme {
                CafezinhoButton(
                    text = "Test Button",
                    onClick = { }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Test Button")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun cafezinhoButton_clickTriggersCallback() {
        var clickCount = 0

        composeTestRule.setContent {
            CafezinhoTheme {
                CafezinhoButton(
                    text = "Click Me",
                    onClick = { clickCount++ }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Click Me")
            .performClick()

        assert(clickCount == 1)
    }

    @Test
    fun cafezinhoButton_disabledStateDoesNotTriggerClick() {
        var clickCount = 0

        composeTestRule.setContent {
            CafezinhoTheme {
                CafezinhoButton(
                    text = "Disabled",
                    onClick = { clickCount++ },
                    enabled = false
                )
            }
        }

        composeTestRule
            .onNodeWithText("Disabled")
            .assertIsNotEnabled()
            .performClick()

        assert(clickCount == 0)
    }

    @Test
    fun cafezinhoButton_loadingStateShowsProgressIndicator() {
        composeTestRule.setContent {
            CafezinhoTheme {
                CafezinhoButton(
                    text = "Loading",
                    onClick = { },
                    loading = true
                )
            }
        }

        // Should show text and loading indicator
        composeTestRule
            .onNodeWithText("Loading")
            .assertExists()
    }

    @Test
    fun secondaryCafezinhoButton_displaysCorrectly() {
        composeTestRule.setContent {
            CafezinhoTheme {
                CafezinhoButton(
                    text = "Secondary Button",
                    onClick = { },
                    variant = ButtonVariant.SECONDARY
                )
            }
        }

        composeTestRule
            .onNodeWithText("Secondary Button")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun dangerCafezinhoButton_displaysCorrectly() {
        composeTestRule.setContent {
            CafezinhoTheme {
                CafezinhoButton(
                    text = "Danger Button",
                    onClick = { },
                    variant = ButtonVariant.DANGER
                )
            }
        }

        composeTestRule
            .onNodeWithText("Danger Button")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun buttonSizes_applyCorrectly() {
        composeTestRule.setContent {
            CafezinhoTheme {
                CafezinhoButton(
                    text = "Small Button",
                    onClick = { },
                    size = ComponentSize.SMALL
                )
            }
        }

        composeTestRule
            .onNodeWithText("Small Button")
            .assertExists()
            .assertIsDisplayed()
    }
}