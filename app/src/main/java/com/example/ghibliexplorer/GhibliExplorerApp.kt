package com.example.ghibliexplorer

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ghibliexplorer.ui.screens.views.StartScreen
import kotlinx.coroutines.runBlocking

enum class GhibliExplorerScreen(@StringRes val titulo: Int){
    Start(titulo = R.string.app_name),
}

/**
 * Composable que muestra la barra superior y muestra el botón Atrás si es posible la navegación hacia atrás
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioGhibliAppBar(
    canNavigateBack: Boolean,
    currentScreen: String,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = { Text(currentScreen.substringBeforeLast("/"))},
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }
    )
}

@Composable
fun StudioGhibliApp() {
    val navController = rememberNavController()
    //Inicializamos entrada pila de actividades
    val backStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        topBar = {
            StudioGhibliAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                currentScreen = backStackEntry?.destination?.route ?: GhibliExplorerScreen.Start.name,
                navigateUp = { navController.navigateUp() },
                modifier = Modifier.fillMaxWidth()
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = GhibliExplorerScreen.Start.name,
        ) {
            composable(route = GhibliExplorerScreen.Start.name) {
                StartScreen(
                    onStartButtonClicked = {navController.navigate(GhibliExplorerScreen.Start.name)},
                    onFavouritesButtonClicked = {navController.navigate(GhibliExplorerScreen.Start.name)},
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }


        }
    }
}