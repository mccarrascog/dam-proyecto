package com.example.ghibliexplorer.data.online

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ghibliexplorer.data.Review
import com.example.ghibliexplorer.network.FirebaseService

/**
 * Este archivo define un repositorio para gestionar las reseñas de películas.
 * La interfaz OnlineReviewsRepository especifica los métodos para obtener, agregar, editar y eliminar reseñas.
 * La implementación FirebaseReviewRepository utiliza el servicio FirebaseService para interactuar con la base de datos de Firebase.
 */

interface OnlineReviewsRepository {
    suspend fun getReviewsForFilm(filmId: String): List<Review>
    suspend fun addReview(review: Review)
    suspend fun deleteReview(review: Review)
    suspend fun editReview(review: Review)
    suspend fun getReviewAndAuthorForFilm(filmId: String, author: String): Review?
    suspend fun getReviewsForAllFilms(): List<Review>
}

class FirebaseReviewRepository(private val firebaseService: FirebaseService) : OnlineReviewsRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getReviewsForFilm(filmId: String): List<Review> {
        return firebaseService.getReviews(filmId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun addReview(review: Review) {
        firebaseService.addReview(review)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun deleteReview(review: Review) {
        firebaseService.deleteReview(review)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun editReview(review: Review) {
        firebaseService.editReview(review)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getReviewAndAuthorForFilm(filmId: String, author: String): Review? {
        return firebaseService.getReviewAndAuthorForFilm(filmId, author)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getReviewsForAllFilms(): List<Review> {
        return firebaseService.getReviewsForAllFilms()
    }
}
