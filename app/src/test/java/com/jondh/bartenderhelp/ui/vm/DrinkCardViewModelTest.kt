package com.jondh.bartenderhelp.ui.vm

import com.jondh.bartenderhelp.model.Drink
import com.jondh.bartenderhelp.model.mockDrinks
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

// Test View Model public methods
// Note: I'm using random in the class. Ideally, any reason for random would be written out in tests.
internal class DrinkCardViewModelTest {

    var testDrinks = listOf<Drink>()

    @Before
    fun setUp() {
        // Make up some mock drinks.
        testDrinks = mockDrinks(Random.nextInt(5, 200))
    }

    @After
    fun tearDown() {

    }

    @Test
    fun testSetNewDrinksStartsAtFirstDrink() {
        val vm = DrinkCardViewModel()
        vm.setNewDrinks(testDrinks)

        assert(vm.uiState.value.drink == testDrinks[0])
    }

    @Test
    fun testToggleShowPreparation() {
        val vm = DrinkCardViewModel()
        assert(!vm.uiState.value.showPreparation)

        vm.toggleShowPreparation()
        assert(vm.uiState.value.showPreparation)

        vm.toggleShowPreparation()
        assert(!vm.uiState.value.showPreparation)
    }

    @Test
    fun testSetNewDrinksSetsShowPreparationFalse() {
        val vm = DrinkCardViewModel()

        // Stays false
        vm.setNewDrinks(testDrinks)
        assert(!vm.uiState.value.showPreparation)

        // Change to true
        vm.toggleShowPreparation()
        assert(vm.uiState.value.showPreparation)

        // Verify #setNewDrinks sets to false
        vm.setNewDrinks(testDrinks)
        assert(!vm.uiState.value.showPreparation)
    }

    @Test
    fun testGoToNextDrink() {
        val vm = DrinkCardViewModel()

        // Verify the empty state
        vm.goToNextDrink()
        assert(vm.uiState.value.drink == null)

        vm.setNewDrinks(testDrinks)

        // Verify next drink
        vm.goToNextDrink()
        assert(vm.uiState.value.drink == testDrinks[1])

        // Verify show preparation is reset
        vm.toggleShowPreparation()
        vm.goToNextDrink()
        assert(vm.uiState.value.drink == testDrinks[2])
        assert(!vm.uiState.value.showPreparation)

        // Verify loop through
        for (i in testDrinks.indices) {
            vm.goToNextDrink()
        }
        assert(vm.uiState.value.drink == testDrinks[2])
    }

    @Test
    fun testGoToPreviousDrink() {
        val vm = DrinkCardViewModel()

        // Verify the empty state
        vm.goToPrevDrink()
        assert(vm.uiState.value.drink == null)

        vm.setNewDrinks(testDrinks)

        // Verify next drink
        vm.goToPrevDrink()
        assert(vm.uiState.value.drink == testDrinks[testDrinks.size - 1])

        // Verify show preparation is reset
        vm.toggleShowPreparation()
        vm.goToPrevDrink()
        assert(vm.uiState.value.drink == testDrinks[testDrinks.size - 2])
        assert(!vm.uiState.value.showPreparation)

        // Verify loop through
        for (i in testDrinks.indices) {
            vm.goToPrevDrink()
        }
        assert(vm.uiState.value.drink == testDrinks[testDrinks.size - 2])
    }
}

