package com.example.ghibliexplorer.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.ghibliexplorer.data.Review
import com.example.ghibliexplorer.data.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

/**
 * Este archivo define la clase FirebaseService, que gestiona las interacciones con la bd de Firebase.
 * Los métodos permiten obtener usuarios, reseñas de películas, y realizar operaciones como añadir, editar o eliminar reseñas.
 * Se utiliza FirebaseFirestore para interactuar con la bd de Firebase en tiempo real.
 */

class FirebaseService {
    private val db = FirebaseFirestore.getInstance()
    suspend fun getUserByEmail(userEmail: String): User? {
        return try {
            val userDoc = db.collection("users").document(userEmail).get().await()
            userDoc.toObject<User>()
        } catch (e: Exception) {
            null
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getReviews(filmId: String): List<Review> {
        val reviewsCollection = db.collection("reviews")
        val querySnapshot = reviewsCollection.whereEqualTo("filmId", filmId).get().await()

        return querySnapshot.documents.map { document ->
            val comment = document.getString("comment") ?: ""
            val rating = (document.getDouble("rating") ?: 0.0).toFloat()
            val author = document.getString("author") ?: "Unknown"
            val date = document.getDate("date")?.toInstant()?.toEpochMilli() ?: System.currentTimeMillis()
            val filmIdFromFirestore = document.getString("filmId") ?: filmId
            val reviewId = document.id

            Review(
                id = reviewId,
                comment = comment,
                rating = rating,
                author = author,
                date = Date(date),
                filmId = filmIdFromFirestore
            )
        }
    }

    // Método para obtener una reseña de una peli específica y un autor determinado
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getReviewAndAuthorForFilm(filmId: String, author: String): Review? {
        val reviews = getReviews(filmId)
        return reviews.firstOrNull { it.author == author } // Filtra por autor y devuelve la primera coincidencia
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addReview(review: Review) {
        val reviewData = hashMapOf(
            "comment" to review.comment,
            "rating" to review.rating,
            "author" to review.author,
            "date" to review.date,
            "filmId" to review.filmId
        )

        try {
            db.collection("reviews")
                .document(review.id)  // Usar el ID aleatorio para crear el documento
                .set(reviewData)
                .await()
            Log.e("AddReview", "Reseña creada con ID: ${review.id}")
        } catch (e: Exception) {
            Log.e("AddReview", "Error al crear la reseña: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteReview(review: Review) {
        db.collection("reviews")
            .document(review.id)
            .delete()
            .await()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun editReview(updatedReview: Review) {
        val reviewData = hashMapOf(
            "comment" to updatedReview.comment,
            "rating" to updatedReview.rating,
            "author" to updatedReview.author,
            "date" to updatedReview.date,
            "filmId" to updatedReview.filmId
        )

        try {
            // Verificar si el documento existe antes de intentar actualizar
            val docRef = db.collection("reviews").document(updatedReview.id)
            val docSnapshot = docRef.get().await()
            if (docSnapshot.exists()) {
                // Actualizar solo si el documento existe
                docRef.update(reviewData as Map<String, Any>).await()
                Log.e("EditReview", "Reseña editada con ID: ${updatedReview.id}")
            } else {
                Log.e("EditReview", "Documento no encontrado con ID: ${updatedReview.id}")
            }
        } catch (e: Exception) {
            Log.e("EditReview", "Error al actualizar la reseña: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getUserRole(userEmail: String): String? {
        return try {
            val querySnapshot = Firebase.firestore.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .await()

            val document = querySnapshot.documents.firstOrNull()
            document?.getString("rol") // Obtenemos el rol
        } catch (e: Exception) {
            null // En caso de error, no mostramos la opción de admin
        }
    }

     suspend fun getUsers(): List<User> {
        val usersCollection = db.collection("users")
        val snapshot = usersCollection.get().await()

        return snapshot.documents.mapNotNull { document ->
            val email = document.id
            val user = document.toObject(User::class.java)

            user?.copy(
                email = email,
                id = UUID.randomUUID().toString()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getReviewsForAllFilms(): List<Review> {
        val reviewsCollection = db.collection("reviews")
        val querySnapshot = reviewsCollection.get().await()

        return querySnapshot.documents.map { document ->
            val comment = document.getString("comment") ?: ""
            val rating = (document.getDouble("rating") ?: 0.0).toFloat()
            val author = document.getString("author") ?: "Unknown"
            val date = document.getDate("date")?.toInstant()?.toEpochMilli()
                ?: System.currentTimeMillis()
            val filmIdFromFirestore = document.getString("filmId") ?: ""
            val reviewId = document.id

            Review(
                id = reviewId,
                comment = comment,
                rating = rating,
                author = author,
                date = Date(date),
                filmId = filmIdFromFirestore
            )
        }
    }
}