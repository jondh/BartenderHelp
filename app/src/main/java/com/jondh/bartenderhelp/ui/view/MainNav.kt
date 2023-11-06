package com.jondh.bartenderhelp.ui.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNav() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavDrawerHeader(title = "Navigate", onCloseClicked = {
                    scope.launch {
                        drawerState.close()
                    }
                })
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                NavigationDrawerItem(
                    label = { Text(text = "Drink Cards") },
                    selected = true,
                    onClick = { /*TODO*/ }
                )
                // ...other drawer items
            }
        }
    ) {
        // Screen content
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("Drink Cards")
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(imageVector = Icons.Filled.Menu,
                                contentDescription = "Open Navigation Drawer")
                        }
                    }
                )
            },
        ) { innerPadding ->
            DrinkCardScreen()
        }
    }
}

@Composable
fun NavDrawerHeader(title: String, onCloseClicked: () -> Unit) {
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (titleText, closeButton) = createRefs()
        val buttonChain = createHorizontalChain(titleText, closeButton, chainStyle = ChainStyle.SpreadInside)

        constrain(buttonChain) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Medium, modifier = Modifier
            .constrainAs(titleText) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            .padding(16.dp))

        IconButton(onClick = onCloseClicked, modifier = Modifier
            .constrainAs(closeButton) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            .padding(16.dp)) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close Navigation Drawer")
        }
    }
}