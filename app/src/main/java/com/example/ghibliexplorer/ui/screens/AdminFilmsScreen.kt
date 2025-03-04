package com.example.ghibliexplorer.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ghibliexplorer.ui.screens.viewmodel.FilmsViewModel
import com.example.ghibliexplorer.ui.screens.views.AdminReviewsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminFilmsScreen(navController: NavController, paddingValues: PaddingValues) {
    Surface(
        modifier = Modifier.fillMaxSize()
            .padding(paddingValues)
    ) {
        val filmsViewModel: FilmsViewModel = viewModel(factory = FilmsViewModel.Factory)

        // Se asegura de que se haga la carga de películas cuando la pantalla se inicie
        LaunchedEffect(Unit) {
            Log.e("AdminFilmScreen", "Iniciando carga de películas...") // Log para indicar que la carga está comenzando
            filmsViewModel.getFilms()
        }

        // Aquí se pasa el estado y la acción de reintentar
        AdminReviewsScreen(
            filmsViewModel = filmsViewModel,
            retryAction = filmsViewModel::getFilms,
            navController = navController
        )
    }
}

