package com.rcdnc.cafezinho.features.profile.presentation.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.rcdnc.cafezinho.ui.component.ComponentSize
import com.rcdnc.cafezinho.ui.theme.CafezinhoTheme
import org.junit.Rule
import org.junit.Test

class InterestSelectorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val availableInterests = listOf(
        "Música", "Viagem", "Esportes", "Culinária", "Arte", "Tecnologia"
    )

    @Test
    fun interestSelector_displaysHeader() {
        composeTestRule.setContent {
            CafezinhoTheme {
                InterestSelector(
                    selectedInterests = emptyList(),
                    availableInterests = availableInterests,
                    onInterestToggle = { },
                    maxSelections = 5
                )
            }
        }

        composeTestRule
            .onNodeWithText("Interesses")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("0/5")
            .assertExists()
    }

    @Test
    fun interestSelector_displaysAvailableInterests() {
        composeTestRule.setContent {
            CafezinhoTheme {
                InterestSelector(
                    selectedInterests = emptyList(),
                    availableInterests = availableInterests,
                    onInterestToggle = { }
                )
            }
        }

        // Check that available interests are displayed
        composeTestRule
            .onNodeWithText("Música")
            .assertExists()

        composeTestRule
            .onNodeWithText("Viagem")
            .assertExists()

        composeTestRule
            .onNodeWithText("Esportes")
            .assertExists()
    }

    @Test
    fun interestSelector_showsSelectedInterests() {
        val selectedInterests = listOf("Música", "Arte")

        composeTestRule.setContent {
            CafezinhoTheme {
                InterestSelector(
                    selectedInterests = selectedInterests,
                    availableInterests = availableInterests,
                    onInterestToggle = { }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Selecionados")
            .assertExists()

        // Selected interests should still be visible
        composeTestRule
            .onNodeWithText("Música")
            .assertExists()

        composeTestRule
            .onNodeWithText("Arte")
            .assertExists()
    }

    @Test
    fun interestSelector_updatesCountWhenSelectionChanges() {
        val selectedInterests = listOf("Música", "Viagem", "Arte")

        composeTestRule.setContent {
            CafezinhoTheme {
                InterestSelector(
                    selectedInterests = selectedInterests,
                    availableInterests = availableInterests,
                    onInterestToggle = { },
                    maxSelections = 5
                )
            }
        }

        composeTestRule
            .onNodeWithText("3/5")
            .assertExists()
    }

    @Test
    fun interestSelector_showsMaxReachedState() {
        val selectedInterests = listOf("Música", "Viagem", "Arte", "Esportes", "Culinária")

        composeTestRule.setContent {
            CafezinhoTheme {
                InterestSelector(
                    selectedInterests = selectedInterests,
                    availableInterests = availableInterests,
                    onInterestToggle = { },
                    maxSelections = 5
                )
            }
        }

        // Count should be shown in error color when max is reached
        composeTestRule
            .onNodeWithText("5/5")
            .assertExists()
    }

    @Test
    fun interestSelector_allowsCustomInterestAddition() {
        composeTestRule.setContent {
            CafezinhoTheme {
                InterestSelector(
                    selectedInterests = emptyList(),
                    availableInterests = availableInterests,
                    onInterestToggle = { },
                    allowCustomInterests = true,
                    onAddCustomInterest = { }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Adicionar interesse personalizado")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun interestSelector_expandsCustomInterestField() {
        composeTestRule.setContent {
            CafezinhoTheme {
                InterestSelector(
                    selectedInterests = emptyList(),
                    availableInterests = availableInterests,
                    onInterestToggle = { },
                    allowCustomInterests = true,
                    onAddCustomInterest = { }
                )
            }
        }

        // Click the add custom interest button
        composeTestRule
            .onNodeWithText("Adicionar interesse personalizado")
            .performClick()

        // Text field should appear
        composeTestRule
            .onNodeWithText("Novo interesse")
            .assertExists()
    }

    @Test
    fun searchableInterestSelector_displaysSearchField() {
        val interestCategories = mapOf(
            "Esporte" to listOf("Futebol", "Basquete", "Tênis"),
            "Arte" to listOf("Pintura", "Música", "Teatro")
        )

        composeTestRule.setContent {
            CafezinhoTheme {
                SearchableInterestSelector(
                    selectedInterests = emptyList(),
                    interestCategories = interestCategories,
                    onInterestToggle = { }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Buscar interesses")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun searchableInterestSelector_showsCategoryTabs() {
        val interestCategories = mapOf(
            "Esporte" to listOf("Futebol", "Basquete"),
            "Arte" to listOf("Pintura", "Música")
        )

        composeTestRule.setContent {
            CafezinhoTheme {
                SearchableInterestSelector(
                    selectedInterests = emptyList(),
                    interestCategories = interestCategories,
                    onInterestToggle = { }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Todos")
            .assertExists()

        composeTestRule
            .onNodeWithText("Esporte")
            .assertExists()

        composeTestRule
            .onNodeWithText("Arte")
            .assertExists()
    }
}