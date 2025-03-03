package com.example.ghibliexplorer.ui.screens.viewmodel

import android.content.Context
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
import com.example.ghibliexplorer.data.User
import com.example.ghibliexplorer.data.offline.OfflineAppContainer
import com.example.ghibliexplorer.utils.getUserEmail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


sealed interface FavFilmsUiState {
    data class Success(val favFilms: Flow<List<Film>>) : FavFilmsUiState
    object Error : FavFilmsUiState
    object Loading : FavFilmsUiState
}

class FavsViewModel(private val appContainer: OfflineAppContainer, context: Context) : ViewModel() {

    var favFilmsUiState: FavFilmsUiState by mutableStateOf(FavFilmsUiState.Loading)
        private set

    private var loggedUser: User? = null

    init {
        viewModelScope.launch {
            val userEmail = getUserEmail(context)
            if (userEmail != null) {
                loggedUser = appContainer.OfflineUsersRepository.getUserByEmail(userEmail)
                if (loggedUser == null) {
                    favFilmsUiState = FavFilmsUiState.Error
                    Log.e("FavsViewModel", "No se encontró al usuario con email: $userEmail")
                } else {
                    getFavFilms()
                }
            } else {
                favFilmsUiState = FavFilmsUiState.Error
                Log.e("FavsViewModel", "No se pudo obtener el email del usuario")
            }
        }
    }

    fun getFavFilms() {
        val userId = loggedUser?.id ?: return
        viewModelScope.launch {
            try {
                favFilmsUiState = FavFilmsUiState.Loading
                Log.d("FavsViewModel", "Cargando favoritos para el usuario: $userId")

                // Aquí actualizamos el estado con la lista de favoritos en forma de Flow
                val favFilmsFlow = appContainer.OfflineFilmsRepository.getAllFavouriteFilmsByUser(userId)
                favFilmsUiState = FavFilmsUiState.Success(favFilmsFlow)
                Log.d("FavsViewModel", "Favoritos cargados correctamente")
            } catch (e: IOException) {
                favFilmsUiState = FavFilmsUiState.Error
                Log.e("FavsViewModel", "Error al obtener los favoritos", e)
            }
        }
    }

    fun removeFilmFromFavs(film: Film) {
        val userId = loggedUser?.id ?: return // Asegúrate de que hay un usuario logueado
        viewModelScope.launch {
            try {
                val favouriteFilm = FavouriteFilm(userId = userId, filmId = film.id) // Crear el objeto correcto
                appContainer.OfflineFilmsRepository.deleteFromFavourites(favouriteFilm) // Pasar FavouriteFilm
                Log.d("FavsViewModel", "Película eliminada de favoritos: ${film.title}")
                getFavFilms() // Actualizar lista de favoritos después de eliminar la película
            } catch (e: Exception) {
                favFilmsUiState = FavFilmsUiState.Error
                Log.e("FavsViewModel", "Error al eliminar la película de favoritos", e)
            }
        }
    }

    fun addFilmToFavourites(film: Film) {
        val userId = loggedUser?.id ?: return // Si userId es null, la función retorna inmediatamente

        viewModelScope.launch {
            try {
                val favouriteFilm = FavouriteFilm(userId = userId, filmId = film.id) // Crear objeto correcto
                appContainer.OfflineFilmsRepository.addToFavourites(favouriteFilm) // Guardar en favoritos
                Log.d("FavsViewModel", "Película añadida a favoritos: ${film.title}")
                getFavFilms() // Actualizar lista de favoritos después de añadir la película
            } catch (e: Exception) {
                favFilmsUiState = FavFilmsUiState.Error
                Log.e("FavsViewModel", "Error al agregar la película a favoritos: ${e.message}", e)
            }
        }
    }


    private val _isFilmInFavsState = MutableStateFlow(false)
    val isFilmInFavsState: StateFlow<Boolean> get() = _isFilmInFavsState

    fun checkIsFilmInFavs(film: Film) {
        val userId = loggedUser?.id ?: return
        viewModelScope.launch {
            try {
                appContainer.OfflineFilmsRepository.isFilmInFavs(film.id, userId).collect {
                    _isFilmInFavsState.value = it
                    Log.d("FavsViewModel", "Comprobación si la película está en favoritos: ${film.title} - $it")
                }
            } catch (e: Exception) {
                Log.e("FavsViewModel", "Error al comprobar si la película está en favoritos", e)
            }
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GhibliExplorerApplication)
                val appContainer = application.offlineAppContainer
                FavsViewModel(appContainer = appContainer, context = context)
            }
        }
    }
}
