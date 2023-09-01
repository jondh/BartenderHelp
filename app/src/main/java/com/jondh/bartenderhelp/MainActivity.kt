package com.jondh.bartenderhelp

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.jondh.bartenderhelp.ui.theme.BartenderHelpTheme
import org.json.JSONArray
import org.json.JSONObject
import java.io.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val drinks = getDrinksFromResources(resources)
        val uiState = DrinkStackUiState(drinks)

        doTheThing(uiState)
    }

    private fun doTheThing(uiState: DrinkStackUiState) {
        setContent {
            BartenderHelpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TopView(
                        uiState,
                        onButtonPressed = {buttonType ->
                            var index = uiState.currentIndex
                            var showPreparation = uiState.showPreparation
                            if (buttonType == -1) {
                                if (index == 0) {
                                    index = uiState.drinks.size
                                }
                                --index
                            } else if (buttonType == 1) {
                                ++index
                                if (index >= uiState.drinks.size) {
                                    index = 0
                                }
                            } else if (buttonType == 0) {
                                showPreparation = !showPreparation
                            }
                            doTheThing(
                                DrinkStackUiState(
                                    uiState.drinks,
                                    index,
                                    showPreparation
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

data class DrinkStackUiState(
    val drinks: List<Drink> = listOf(),
    val currentIndex: Int = 0,
    val showPreparation: Boolean = false
)

@Composable
fun TopView(uiState: DrinkStackUiState, onButtonPressed: (position: Int) -> Unit) {
    ConstraintLayout {
        val (header, drinkStack, footer) = createRefs()

        Header(
            Modifier
                .constrainAs(header) {
                    bottom.linkTo(drinkStack.top, margin = 8.dp)
                    top.linkTo(parent.top)
                    height = Dimension.fillToConstraints
                }
                .background(Color(0x99C83980))
                .fillMaxWidth())

        DrinkStack(Modifier.constrainAs(drinkStack) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }, uiState, onButtonPressed)

        Footer(Modifier.constrainAs(footer) {
            top.linkTo(drinkStack.bottom)
            bottom.linkTo(parent.bottom)
            height = Dimension.fillToConstraints
            width = Dimension.matchParent
        }, onButtonPressed)

    }
}

@Composable
fun Header(modifier: Modifier) {
    Box(modifier = modifier
        .background(Color.Cyan)) {
        Text(text = "Header")
    }
}

@Composable
fun Footer(modifier: Modifier, onButtonPressed: (position: Int) -> Unit) {
    ConstraintLayout(
        modifier.padding(horizontal = 16.dp)) {
        val (leftButton, centerButton, rightButton) = createRefs()
        val buttonChain = createHorizontalChain(leftButton, centerButton, rightButton, chainStyle = ChainStyle.SpreadInside)

        constrain(buttonChain) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        Button(onClick = {
                         onButtonPressed(-1)
        },
            Modifier
                .constrainAs(leftButton) {
                    top.linkTo(parent.top)
                }
                .size(75.dp)) {
            Text(text = "Left")
        }

        Button(onClick = {
                         onButtonPressed(0)
        },
            Modifier
                .constrainAs(centerButton) {
                    start.linkTo(leftButton.end)
                }
                .size(75.dp)) {
            Text(text = "Info")
        }

        Button(onClick = {
                         onButtonPressed(1)
        },
            Modifier
                .constrainAs(rightButton) {
                    start.linkTo(centerButton.end)
                }
                .size(75.dp)) {
            Text(text = "Right")
        }
    }
}

@Composable
fun DrinkStack(modifier: Modifier, uiState: DrinkStackUiState, onButtonPressed: (position: Int) -> Unit) {
    Column(
        modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DrinkCard(drink = uiState.drinks[uiState.currentIndex], uiState.showPreparation, onButtonPressed)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkCard(drink: Drink, showPreparation: Boolean, onButtonPressed: (position: Int) -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(16.dp),
        onClick = {
            onButtonPressed(0)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp))
        {
            if (!showPreparation) {
                Text(text = drink.name, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = drink.type, fontSize = 20.sp)
            } else {
                DrinkPreparationList(drink = drink)
            }
        }
    }
}

fun stringListToCommaString(stringList: List<String>): String {
    var commaString = ""
    for (str in stringList) {
        commaString += str
        if (str != stringList.last()) {
            commaString += ", "
        }
    }
    return commaString
}

@Composable
fun DrinkPreparationList(drink: Drink) {
    Column {
        Text(text = drink.name, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(text = "Glass: ", fontWeight = FontWeight.Medium)
            Text(text = stringListToCommaString(drink.preparation.glasses))
        }
        Row {
            Text(text = "Ice: ", fontWeight = FontWeight.Medium)
            Text(text = drink.preparation.ice)
        }
        Column {
            Text(text = "Pours", fontWeight = FontWeight.Medium)
            for (step in drink.preparation.steps) {
                Column(Modifier.padding(start = 8.dp)) {
                    Spacer(modifier = Modifier.height(2.dp))
                    for (subStep in step) {
                        Text(text = "${subStep.liquid} | ${subStep.getAmountString()}")
                    }
                    Box(modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Color.Gray))
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
        if (drink.preparation.garnish.isNotEmpty()) {
            Row {
                Text(text = "Garnish: ", fontWeight = FontWeight.Medium)
                Text(text = stringListToCommaString(drink.preparation.garnish))
            }
        }
        if (drink.preparation.shake) {
            Text(text = "Shake and Strain", fontWeight = FontWeight.Medium)
        }
        if (drink.preparation.top.isNotEmpty()) {
            Text(text = "Top", fontWeight = FontWeight.Medium)
            for (top in drink.preparation.top) {
                Column(Modifier.padding(start = 8.dp)) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "${top.liquid} | ${top.getAmountString()}")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BartenderHelpTheme {
        Greeting("Android")
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