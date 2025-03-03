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
import com.example.ghibliexplorer.data.FavouriteFilm
import com.example.ghibliexplorer.data.Film
import com.example.ghibliexplorer.data.offline.OfflineFilmsRepository
import com.example.ghibliexplorer.data.offline.OfflineUsersRepository
import com.example.ghibliexplorer.data.online.OnlineFilmsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface FilmsUiState {
    data class Success(val films: List<Film>) : FilmsUiState
    object Error : FilmsUiState
    object Loading : FilmsUiState
}

class FilmsViewModel(
    private val onlineFilmsRepository: OnlineFilmsRepository,
    private val offlineFilmsRepository: OfflineFilmsRepository,
    private val offlineUsersRepository: OfflineUsersRepository
) : ViewModel() {

    var filmsUiState: FilmsUiState by mutableStateOf(FilmsUiState.Loading)
        private set

    var selectedFilm: Film? by mutableStateOf(null)
        private set

    private var userEmail: String? = null // Guardamos el userEmail como una propiedad del ViewModel

    // Esta función debería ser llamada una vez con el userEmail cuando el usuario inicia sesión o cuando es necesario
    fun setUserEmail(email: String?) {
        userEmail = email
    }

    fun getFilms() {
        viewModelScope.launch {
            try {
                Log.e("FilmsViewModel", "Iniciando la obtención de películas...")
                filmsUiState = FilmsUiState.Loading
                Log.e("FilmsViewModel", "Cargando películas...")

                val email = userEmail
                if (email != null) {
                    val loggedUser = offlineUsersRepository.getUserByEmail(email)

                    if (loggedUser != null) {
                        val userId = loggedUser.id

                        // Obtener las películas de la API
                        val filmsFromApi = onlineFilmsRepository.getFilms()

                        Log.e("FilmsViewModel", "Películas obtenidas de la API: ${filmsFromApi.size}")

                        if (filmsFromApi.isNotEmpty()) {
                            // Guardar las películas en la base de datos de films
                            filmsFromApi.forEach { film ->
                                if (!offlineFilmsRepository.isFilmInDatabase(film.id)) {
                                    offlineFilmsRepository.insertFilm(film)
                                }
                            }

                            // Actualizar el estado de éxito
                            filmsUiState = FilmsUiState.Success(filmsFromApi)
                        } else {
                            // Si la API no retorna películas
                            filmsUiState = FilmsUiState.Error
                            Log.e("FilmsViewModel", "La API no retornó películas.")
                        }
                    } else {
                        filmsUiState = FilmsUiState.Error
                        Log.e("FilmsViewModel", "Usuario no encontrado para el email: $email")
                    }
                } else {
                    filmsUiState = FilmsUiState.Error
                    Log.e("FilmsViewModel", "No se pudo obtener el email.")
                }
            } catch (e: IOException) {
                filmsUiState = FilmsUiState.Error
                Log.e("FilmsViewModel", "Error obteniendo las películas de la API", e)
            }
        }
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
                // Obtener la instancia de la aplicación
                val application = (this[APPLICATION_KEY] as GhibliExplorerApplication)

                // Obtener el repositorio de películas online
                val onlineFilmsRepository = application.container.OnlineFilmsRepository

                // Obtener el repositorio de películas offline
                val offlineFilmsRepository = application.offlineAppContainer.OfflineFilmsRepository
                val offlineUsersRepository = application.offlineAppContainer.OfflineUsersRepository

                FilmsViewModel(
                    onlineFilmsRepository = onlineFilmsRepository,
                    offlineFilmsRepository = offlineFilmsRepository,
                    offlineUsersRepository = offlineUsersRepository
                )
            }
        }
    }
}