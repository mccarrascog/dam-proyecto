package com.example.ghibliexplorer.ui.screens.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ghibliexplorer.GhibliExplorerApplication
import com.example.ghibliexplorer.data.Review
import com.example.ghibliexplorer.data.online.OnlineReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val onlineReviewsRepository: OnlineReviewsRepository
) : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _isReviewAdded = MutableStateFlow(false)

    private val _isReviewDeleted = MutableStateFlow(false)

    private val _isReviewEdited = MutableStateFlow(false)

    private val _shouldRefresh = MutableStateFlow(false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // No se puede inicializar _reviews aquí sin un filmId válido
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadReviews(filmId: String) {
        viewModelScope.launch {
            _isLoading.value = true // Iniciar el estado de carga
            try {
                val fetchedReviews = onlineReviewsRepository.getReviewsForFilm(filmId)
                _reviews.value = fetchedReviews // Ahora sí se actualizan las reseñas
                Log.d("ReviewViewModel", "Reseñas cargadas para el filmId: $filmId, cantidad: ${fetchedReviews.size}")
            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Error al obtener reseñas: ${e.message}")
            } finally {
                _isLoading.value = false // Finalizar el estado de carga
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadReviewsForAllFilms() {
        viewModelScope.launch {
            _isLoading.value = true // Iniciar el estado de carga
            try {
                // Suponiendo que tu repositorio tenga un método para obtener todas las reseñas
                val fetchedReviews = onlineReviewsRepository.getReviewsForAllFilms()
                _reviews.value = fetchedReviews // Actualiza la lista completa de reseñas
                Log.d("ReviewViewModel", "Reseñas cargadas para todas las películas, cantidad: ${fetchedReviews.size}")
            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Error al obtener reseñas para todas las películas: ${e.message}")
            } finally {
                _isLoading.value = false // Finalizar el estado de carga
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getUserReviewForFilm(filmId: String, userEmail: String): Review? {
        return _reviews.value.firstOrNull { it.filmId == filmId && it.author == userEmail }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addReview(review: Review) {
        viewModelScope.launch {
            try {
                onlineReviewsRepository.addReview(review)
                _isReviewAdded.value = true
                loadReviews(review.filmId) // Volver a cargar las reseñas después de añadir
            } catch (e: Exception) {
                _isReviewAdded.value = false
                Log.e("ReviewViewModel", "Error al agregar reseña: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteReview(review: Review) {
        viewModelScope.launch {
            try {
                onlineReviewsRepository.deleteReview(review)
                _isReviewDeleted.value = true
                loadReviews(review.filmId) // Volver a cargar las reseñas después de borrar
            } catch (e: Exception) {
                _isReviewDeleted.value = false
                Log.e("ReviewViewModel", "Error al eliminar reseña: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editReview(updatedReview: Review) {
        viewModelScope.launch {
            try {
                onlineReviewsRepository.editReview(updatedReview)
                _isReviewEdited.value = true
                loadReviews(updatedReview.filmId) // Recargar las reseñas tras editar
            } catch (e: Exception) {
                _isReviewEdited.value = false
                Log.e("EditReview", "Error al editar la reseña: ${e.message}")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GhibliExplorerApplication)
                val onlineReviewsRepository = application.container.onlineReviewsRepository
                ReviewViewModel(onlineReviewsRepository)
            }
        }
    }
}


