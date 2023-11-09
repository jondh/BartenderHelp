package com.jondh.bartenderhelp.model

import org.junit.Test
import kotlin.random.Random

class DrinkTest {

    @Test
    fun testMockDrinks() {
        val drinkList = mockDrinks(1)
        assert(drinkList.size == 1)

        assert(drinkList[0].name == "0")
        assert(drinkList[0].type == "test0")
        // Note: get rid of randomness and test
    }

}

fun mockDrinks(count: Int) : List<Drink> {
    val preparationSteps = mutableListOf<PreparationStep>()
    for (i in 0 until 10) { // make 10 steps
        preparationSteps.add(
            PreparationStep(liquid = "$i", amount = "oz", quantity = 1.0)
        )
    }
    val preparations = mutableListOf<Preparation>()
    for (i in 0 until 10) { // make 10 preparation options
        val bigSteps = mutableListOf<List<PreparationStep>>()
        for (j in 0..Random.nextInt(1, 4)) { // 1 to 4 steps
            val littleSteps = mutableListOf<PreparationStep>()
            for (k in 0..Random.nextInt(1, 2)) { // 1 to 2 processes
                littleSteps.add(preparationSteps.random())
            }
            bigSteps.add(littleSteps)
        }
        preparations.add(
            Preparation(
                glasses = listOf("$i"),
                ice = "mhm",
                steps = bigSteps,
                garnish = listOf("bha"),
                shake = true,
                top = listOf()
            )
        )
    }

    val drinks = mutableListOf<Drink>()
    for (i in 0 until count) {
        drinks.add(
            Drink(
                name = "$i",
                type = "test${i%3}",
                preparation = preparations.random()
            )
        )
    }

    return drinks
}