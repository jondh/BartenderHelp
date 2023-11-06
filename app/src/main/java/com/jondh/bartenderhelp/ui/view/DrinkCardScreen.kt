package com.jondh.bartenderhelp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jondh.bartenderhelp.model.Drink
import com.jondh.bartenderhelp.ui.vm.DrinkCardViewModel

@Composable
fun DrinkCardScreen(
    viewModel: DrinkCardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    TopView(
        uiState.drink,
        uiState.showPreparation,
        onButtonPressed = {buttonType ->
            if (buttonType == -1) {
                viewModel.goToPrevDrink()
            } else if (buttonType == 1) {
                viewModel.goToNextDrink()
            } else if (buttonType == 0) {
                viewModel.toggleShowPreparation()
            }
        }
    )
}

@Composable
fun TopView(drink: Drink?, showPreparation: Boolean, onButtonPressed: (position: Int) -> Unit) {
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

        if (drink != null) {
            DrinkStack(Modifier.constrainAs(drinkStack) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }, drink, showPreparation, onButtonPressed)
        }

        Footer(Modifier.constrainAs(footer) {
            top.linkTo(drinkStack.bottom)
            bottom.linkTo(parent.bottom)
            height = Dimension.fillToConstraints
            width = Dimension.matchParent
        }, onButtonPressed)
    }
}

@Composable
fun DrinkStack(modifier: Modifier, drink: Drink, showPreparation: Boolean, onButtonPressed: (position: Int) -> Unit) {
    Column(
        modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DrinkCard(drink = drink, showPreparation, onButtonPressed)
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