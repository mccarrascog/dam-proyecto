package com.example.ghibliexplorer.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import com.example.ghibliexplorer.ui.screens.viewmodel.FilmsViewModel
import com.example.ghibliexplorer.ui.screens.views.HomeScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ghibliexplorer.utils.getUserEmail

@Composable
fun FilmsScreen(navController: NavController, paddingValues: PaddingValues) {
    Surface(
        modifier = Modifier.fillMaxSize()
            .padding(paddingValues)
    ) {
        val filmsViewModel: FilmsViewModel = viewModel(factory = FilmsViewModel.Factory)

        val context = LocalContext.current
        val userEmail = getUserEmail(context)

        LaunchedEffect(userEmail) {
            if (userEmail != null) {
                filmsViewModel.setUserEmail(userEmail)
                filmsViewModel.getFilms()
            }
        }

        HomeScreen(
            filmsUiState = filmsViewModel.filmsUiState,
            retryAction = filmsViewModel::getFilms,
            navController = navController
        )
    }
}