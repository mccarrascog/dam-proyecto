package com.example.ghibliexplorer.ui

import androidx.compose.foundation.layout.PaddingValues
import com.example.ghibliexplorer.ui.screens.viewmodel.FilmsViewModel
import com.example.ghibliexplorer.ui.screens.views.HomeScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

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