package com.jondh.bartenderhelp

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.jondh.bartenderhelp.ui.theme.BartenderHelpTheme
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringWriter
import java.io.Writer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val drinks = getDrinksFromResources(resources)
        setContent {
            val drinkCardViewModel: DrinkCardViewModel = viewModel()
            drinkCardViewModel.setNewDrinks(drinks)
            BartenderHelpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DrinkCardScreen()
                }
            }
        }
    }
}

fun getDrinksFromResources(resources: Resources): List<Drink> {
    val inputStream = resources.openRawResource(R.raw.data)
    val writer: Writer = StringWriter()
    val buffer = CharArray(1024)
    inputStream.use {
        val reader: Reader = BufferedReader(InputStreamReader(it, "UTF-8"))
        var n: Int
        while (reader.read(buffer).also { n = it } != -1) {
            writer.write(buffer, 0, n)
        }
    }

    val jsonString: String = writer.toString()
    val jsonObject = JSONObject(jsonString)
    return parseDrinks(jsonObject)
}

fun parseDrinks(jsonObject: JSONObject): List<Drink> {
    val drinksJSONArray = jsonObject.getJSONArray("drinks")
    val drinksMutArray = mutableListOf<Drink>()
    for (i in 0 until drinksJSONArray.length()) {
        drinksMutArray.add(Drink(drinksJSONArray.getJSONObject(i)))
    }
    return drinksMutArray.toList()
}

data class Drink(
    var name: String = "",
    var type: String = "",
    var preparation: Preparation = Preparation()
) {
    constructor(jsonObject: JSONObject) : this() {
        this.name = jsonObject.getString("name")
        this.type = jsonObject.getString("type")

        val preparationJSON = jsonObject.getJSONObject("preparation")
        this.preparation = Preparation(preparationJSON)
    }
}

data class Preparation(
    var glasses: List<String> = listOf(),
    var ice: String = "",
    var steps: List<List<PreparationStep>> = listOf(),
    var garnish: List<String> = listOf(),
    var shake: Boolean = false,
    var top: List<PreparationStep> = listOf()
) {
    constructor(jsonObject: JSONObject) : this() {
        this.glasses = parseStringList(jsonObject.getJSONArray("glass"))
        this.ice = jsonObject.getString("ice")
        this.steps = parseSteps(jsonObject.getJSONArray("steps"))
        this.garnish = parseStringList(jsonObject.optJSONArray("garnish"))
        this.shake = jsonObject.optBoolean("shake")
        this.top = parseTop(jsonObject.optJSONArray("top"))
    }

    private fun parseStringList(jsonArray: JSONArray?): List<String> {
        if (jsonArray == null) return listOf()

        val glassList = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            glassList.add(jsonArray.getString(i))
        }
        return glassList.toList()
    }

    private fun parseSteps(stepsJSONArray: JSONArray): List<List<PreparationStep>> {
        val stepsMutArray = mutableListOf<List<PreparationStep>>()
        for (i in 0 until stepsJSONArray.length()) {
            val stepJsonArray = stepsJSONArray.getJSONArray(i)
            val stepMutArray = mutableListOf<PreparationStep>()
            for (stepIndex in 0 until stepJsonArray.length()) {
                stepMutArray.add(PreparationStep(stepJsonArray.getJSONObject(stepIndex)))
            }
            stepsMutArray.add(stepMutArray.toList())
        }
        return stepsMutArray.toList()
    }

    private fun parseTop(topJSONArray: JSONArray?): List<PreparationStep> {
        if (topJSONArray == null) return listOf()

        val topMutArray = mutableListOf<PreparationStep>()
        for (i in 0 until topJSONArray.length()) {
            topMutArray.add(PreparationStep(topJSONArray.getJSONObject(i)))
        }
        return topMutArray.toList()
    }
}

data class PreparationStep(
    var liquid: String = "",
    var amount: String = "",
    var quantity: Double = 0.0
) {
    constructor(jsonObject: JSONObject) : this() {
        this.liquid = jsonObject.getString("liquid")
        this.amount = jsonObject.getString("amount")
        this.quantity = jsonObject.optDouble("quantity")
    }

    fun getAmountString(): String {
        if (quantity > 0) {
            return "$quantity $amount"
        }
        return amount
    }
}