package com.example.ghibliexplorer.ui.screens.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.ghibliexplorer.GhibliExplorerScreen
import com.example.ghibliexplorer.data.Film
import com.example.ghibliexplorer.data.Review
import com.example.ghibliexplorer.data.online.FirebaseReviewRepository
import com.example.ghibliexplorer.utils.getUserEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewRepository: FirebaseReviewRepository
) : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _isReviewAdded = MutableStateFlow(false)
    val isReviewAdded: StateFlow<Boolean> = _isReviewAdded.asStateFlow()

    private val _isReviewDeleted = MutableStateFlow(false)
    val isReviewDeleted: StateFlow<Boolean> = _isReviewDeleted.asStateFlow()

    private val _isReviewEdited = MutableStateFlow(false)
    val isReviewEdited: StateFlow<Boolean> = _isReviewEdited.asStateFlow()

    private val _shouldRefresh = MutableStateFlow(false)
    val shouldRefresh: StateFlow<Boolean> = _shouldRefresh.asStateFlow()

    private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger: StateFlow<Int> = _refreshTrigger

    @RequiresApi(Build.VERSION_CODES.O)
    fun getReviewsForFilm(filmId: String) {
        viewModelScope.launch {
            try {
                _reviews.value = reviewRepository.getReviewsForFilm(filmId)
            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Error al obtener reseñas: ${e.message}")
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
                reviewRepository.addReview(review)
                _isReviewAdded.value = true
                _shouldRefresh.value = true
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
                reviewRepository.deleteReview(review)
                _isReviewDeleted.value = true
                _shouldRefresh.value = true
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
                reviewRepository.editReview(updatedReview)

                // Obtener las reseñas y crear una nueva lista para forzar la recomposición
                val updatedReviews = reviewRepository.getReviewsForFilm(updatedReview.filmId)
                _reviews.value = updatedReviews.map { it.copy() } // Nueva lista con nuevas referencias

                _isReviewEdited.value = true
                _shouldRefresh.value = true
            } catch (e: Exception) {
                _isReviewEdited.value = false
                Log.e("EditReview", "Error al editar la reseña: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getReviewForFilm(filmId: String): Review? {
        return _reviews.value.firstOrNull { it.filmId == filmId }
    }
}

class ReviewViewModelFactory(
    private val reviewRepository: FirebaseReviewRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            return ReviewViewModel(reviewRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

