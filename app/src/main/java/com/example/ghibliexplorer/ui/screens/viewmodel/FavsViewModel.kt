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

class FavsViewModel(private val offlineFilmsRepository: OfflineFilmsRepository) : ViewModel() {
    var favFilmsUiState: FavFilmsUiState by mutableStateOf(FavFilmsUiState.Loading)
        private set

    init {getFavFilms()}

    fun getFavFilms() {
        viewModelScope.launch {
            try{
                favFilmsUiState = FavFilmsUiState.Loading
                favFilmsUiState = FavFilmsUiState.Success(offlineFilmsRepository.getAllFavouriteFilms())
            }catch (e: IOException){
                favFilmsUiState = FavFilmsUiState.Error
                Log.e("FavsViewModel", "Error getting fav films", e)
            }
        }
    }

    fun removeFilmFromFavs(film: Film) {
        viewModelScope.launch {
            try {
                offlineFilmsRepository.deleteFromFavourites(film)
            } catch (e: Exception) {
                favFilmsUiState = FavFilmsUiState.Error
            }
        }
    }

    fun addFilmToFavourites(film: Film) {
        viewModelScope.launch {
            try {
                offlineFilmsRepository.addToFavourites(film)
            } catch (e: Exception) {
                favFilmsUiState = FavFilmsUiState.Error
            }
        }
    }

    private val _isFilmInFavsState = MutableStateFlow(false)
    val isFilmInFavsState: StateFlow<Boolean> get() = _isFilmInFavsState

    fun checkIsFilmInFavs(film: Film) {
        viewModelScope.launch {
            offlineFilmsRepository.isFilmInFavs(film.id).collect {
                _isFilmInFavsState.value = it
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GhibliExplorerApplication)
                val offlineFilmsRepository = application.offlineAppContainer.OfflineFilmsRepository
                FavsViewModel(offlineFilmsRepository = offlineFilmsRepository)
            }
        }
    }
}