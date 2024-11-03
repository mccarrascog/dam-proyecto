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
import com.example.ghibliexplorer.data.online.OnlineFilmsRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface FilmsUiState {
    data class Success(val films: List<Film>) : FilmsUiState
    object Error : FilmsUiState
    object Loading : FilmsUiState
}

class FilmsViewModel(private val onlineFilmsRepository: OnlineFilmsRepository) : ViewModel() {
    var filmsUiState: FilmsUiState by mutableStateOf(FilmsUiState.Loading)
        private set

    init {getFilms()}

    fun getFilms() {
        viewModelScope.launch {
            try{
                filmsUiState = FilmsUiState.Loading
                filmsUiState = FilmsUiState.Success(onlineFilmsRepository.getFilms())
            }catch (e: IOException){
                filmsUiState = FilmsUiState.Error
                Log.e("FilmsViewModel", "Error getting films", e)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GhibliExplorerApplication)
                val onlineFilmsRepository = application.container.OnlineFilmsRepository
                FilmsViewModel(onlineFilmsRepository = onlineFilmsRepository)
            }
        }
    }
}