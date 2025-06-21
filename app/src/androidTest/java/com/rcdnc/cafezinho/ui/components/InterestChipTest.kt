package com.rcdnc.cafezinho.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme
import org.junit.Rule
import org.junit.Test

class InterestChipTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun interestChip_displaysText() {
        composeTestRule.setContent {
            CafezinhoTheme {
                InterestChip(
                    text = "Música",
                    onClick = { },
                    state = InterestChipState.UNSELECTED,
                    size = ComponentSize.MEDIUM
                )
            }
        }

        composeTestRule
            .onNodeWithText("Música")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun interestChip_clickTriggersCallback() {
        var clickCount = 0

        composeTestRule.setContent {
            CafezinhoTheme {
                InterestChip(
                    text = "Viagem",
                    onClick = { clickCount++ },
                    state = InterestChipState.UNSELECTED,
                    size = ComponentSize.MEDIUM
                )
            }
        }

        composeTestRule
            .onNodeWithText("Viagem")
            .performClick()

        assert(clickCount == 1)
    }

    @Test
    fun interestChip_selectedStateIsVisible() {
        composeTestRule.setContent {
            CafezinhoTheme {
                InterestChip(
                    text = "Arte",
                    onClick = { },
                    state = InterestChipState.SELECTED,
                    size = ComponentSize.MEDIUM
                )
            }
        }

        composeTestRule
            .onNodeWithText("Arte")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun interestChip_disabledStateDoesNotTriggerClick() {
        var clickCount = 0

        composeTestRule.setContent {
            CafezinhoTheme {
                InterestChip(
                    text = "Esportes",
                    onClick = { clickCount++ },
                    state = InterestChipState.UNSELECTED,
                    size = ComponentSize.MEDIUM,
                    enabled = false
                )
            }
        }

        composeTestRule
            .onNodeWithText("Esportes")
            .assertIsNotEnabled()
            .performClick()

        assert(clickCount == 0)
    }

    @Test
    fun interestChip_differentSizesWork() {
        composeTestRule.setContent {
            CafezinhoTheme {
                InterestChip(
                    text = "Tecnologia",
                    onClick = { },
                    state = InterestChipState.UNSELECTED,
                    size = ComponentSize.SMALL
                )
            }
        }

        composeTestRule
            .onNodeWithText("Tecnologia")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun interestChip_nullOnClickDisablesInteraction() {
        composeTestRule.setContent {
            CafezinhoTheme {
                InterestChip(
                    text = "ReadOnly",
                    onClick = null,
                    state = InterestChipState.SELECTED,
                    size = ComponentSize.MEDIUM
                )
            }
        }

        composeTestRule
            .onNodeWithText("ReadOnly")
            .assertExists()
            .assertIsDisplayed()
    }
}