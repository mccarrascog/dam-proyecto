package com.example.ghibliexplorer.ui.screens.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ghibliexplorer.GhibliExplorerApplication
import com.example.ghibliexplorer.data.Film
import com.example.ghibliexplorer.data.offline.OfflineFilmsRepository
import com.example.ghibliexplorer.data.online.OnlineFilmsRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface FilmsUiState {
    data class Success(val films: List<Film>) : FilmsUiState
    object Error : FilmsUiState
    object Loading : FilmsUiState
}

sealed interface AdminFilmsUiState {
    data class Success(val films: List<Film>) : AdminFilmsUiState
    object Error : AdminFilmsUiState
    object Loading : AdminFilmsUiState
}

class FilmsViewModel(
    private val onlineFilmsRepository: OnlineFilmsRepository,
    private val offlineFilmsRepository: OfflineFilmsRepository,
) : ViewModel() {

    var filmsUiState: FilmsUiState by mutableStateOf(FilmsUiState.Loading)
        private set

    // Estado específico para las películas en la sección de admin
    var adminFilmsUiState: AdminFilmsUiState by mutableStateOf(AdminFilmsUiState.Loading)
        private set

    var selectedFilm: Film? by mutableStateOf(null)
        private set

    private var userEmail: String? = null

    // Esta función debería ser llamada una vez con el userEmail cuando el usuario inicia sesión o cuando es necesario
    fun setUserEmail(email: String?) {
        userEmail = email
    }

    fun getFilms() {
        viewModelScope.launch {
            try {
                Log.e("FilmsViewModel", "Iniciando la obtención de películas...")

                // Actualiza ambos estados a 'Loading'
                updateFilmsStates(FilmsUiState.Loading, AdminFilmsUiState.Loading)
                Log.e("FilmsViewModel", "Cargando películas...")

                // Obtener las películas de la API
                val filmsFromApi = onlineFilmsRepository.getFilms()

                Log.e("FilmsViewModel", "Películas obtenidas de la API: ${filmsFromApi.size}")

                if (filmsFromApi.isNotEmpty()) {
                    // Guardar las películas en la base de datos local de films
                    filmsFromApi.forEach { film ->
                        if (!offlineFilmsRepository.isFilmInDatabase(film.id)) {
                            offlineFilmsRepository.insertFilm(film)
                        }
                    }

                    // Actualizar ambos estados con el resultado de la API
                    updateFilmsStates(
                        FilmsUiState.Success(filmsFromApi),
                        AdminFilmsUiState.Success(filmsFromApi)
                    )
                } else {
                    updateFilmsStates(FilmsUiState.Error, AdminFilmsUiState.Error)
                    Log.e("FilmsViewModel", "La API no retornó películas.")
                }

            } catch (e: IOException) {
                updateFilmsStates(FilmsUiState.Error, AdminFilmsUiState.Error)
                Log.e("FilmsViewModel", "Error obteniendo las películas de la API", e)
            }
        }
    }

    // Función para actualizar ambos estados a la vez
    private fun updateFilmsStates(filmsState: FilmsUiState, adminFilmsState: AdminFilmsUiState) {
        filmsUiState = filmsState
        adminFilmsUiState = adminFilmsState
    }

    fun getFilmById(filmId: String) {
        viewModelScope.launch {
            try {
                selectedFilm = onlineFilmsRepository.getFilmById(filmId)
            } catch (e: IOException) {
                Log.e("FilmsViewModel", "Error getting film details for id: $filmId", e)
                selectedFilm = null
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GhibliExplorerApplication)

                val onlineFilmsRepository = application.container.onlineFilmsRepository

                val offlineFilmsRepository = application.offlineAppContainer.OfflineFilmsRepository

                FilmsViewModel(
                    onlineFilmsRepository = onlineFilmsRepository,
                    offlineFilmsRepository = offlineFilmsRepository
                )
            }
        }
    }
}