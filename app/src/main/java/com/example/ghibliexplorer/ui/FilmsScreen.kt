@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ghibliexplorer.ui

import androidx.compose.foundation.layout.PaddingValues
import com.example.ghibliexplorer.R
import com.example.ghibliexplorer.ui.screens.viewmodel.FilmsViewModel
import com.example.ghibliexplorer.ui.screens.views.HomeScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmsScreen(navController: NavController, paddingValues: PaddingValues) {
    Surface(
        modifier = Modifier.fillMaxSize()
            .padding(paddingValues)
    ) {
        val filmsViewModel: FilmsViewModel = viewModel(factory = FilmsViewModel.Factory)
        HomeScreen(
            filmsUiState = filmsViewModel.filmsUiState,
            retryAction = filmsViewModel::getFilms,
            navController = navController
        )
    }
}

/*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmsScreen(navController: NavController) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { FilmsTopAppBar(scrollBehavior = scrollBehavior) }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            val filmsViewModel: FilmsViewModel = viewModel(factory = FilmsViewModel.Factory)
            HomeScreen(
                filmsUiState = filmsViewModel.filmsUiState,
                retryAction = filmsViewModel::getFilms,
                navController = navController
            )
        }
    }
}

@Composable
fun FilmsTopAppBar(scrollBehavior: TopAppBarScrollBehavior, modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        modifier = modifier
    )
}*/