package com.jondh.bartenderhelp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DrinkCardViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(DrinkCardUiState())
    val uiState: StateFlow<DrinkCardUiState> = _uiState.asStateFlow()

    private var drinks = listOf<Drink>()
    private var currentIndex  = 0

    fun setNewDrinks(newDrinks: List<Drink>) {
        drinks = newDrinks
        currentIndex = 0
        if (drinks.isEmpty()) {
            _uiState.value = DrinkCardUiState()
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    drink = drinks[currentIndex],
                    showPreparation = false
                )
            }
        }
    }

    fun goToNextDrink() {
        var index = currentIndex
        ++index
        if (index >= drinks.size) {
            index = 0
        }
        changeDrink(index)
    }

    fun goToPrevDrink() {
        var index = currentIndex
        if (index == 0) {
            index = drinks.size
        }
        --index
        changeDrink(index)
    }

    private fun changeDrink(index: Int) {
        currentIndex = index
        val drink = drinks.getOrNull(currentIndex)
        _uiState.update { currentState ->
            currentState.copy(
                drink = drink,
                showPreparation = false
            )
        }
    }

    fun toggleShowPreparation() {
        _uiState.update { currentState ->
            currentState.copy(
                showPreparation = !currentState.showPreparation
            )
        }
    }
}

data class DrinkCardUiState(
    val drink: Drink? = null,
    val showPreparation: Boolean = false
)