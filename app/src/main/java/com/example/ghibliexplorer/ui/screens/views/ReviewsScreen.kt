package com.example.ghibliexplorer.ui.screens.views

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ghibliexplorer.GhibliExplorerScreen
import com.example.ghibliexplorer.data.Film
import com.example.ghibliexplorer.data.Review
import com.example.ghibliexplorer.ui.screens.viewmodel.ReviewViewModel
import com.example.ghibliexplorer.utils.getUserEmail

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReviewScreen(
    film: Film,
    reviewViewModel: ReviewViewModel,
    navController: NavController, // Navegación a la pantalla de añadir reseña
    modifier: Modifier = Modifier
) {
    // Usamos un estado para la lista de reseñas
    val reviews by reviewViewModel.reviews.collectAsState()
    LaunchedEffect(reviews) {
        Log.d("ReviewScreen", "Reseñas recibidas: $reviews")
    }

    val context = LocalContext.current
    val userEmail = getUserEmail(context).orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(top = 56.dp)
            .padding(16.dp)
    ) {
        Text(
            text = "Reseñas de ${film.title ?: "Película desconocida"}",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (reviews.isNotEmpty()) {
            val averageRating = reviews.map { it.rating }.average()
            Text(
                text = "Puntuación Media: ${
                    if (averageRating % 1 == 0.0) {
                        averageRating.toInt() 
                    } else {
                        String.format("%.1f", averageRating) 
                    }
                }⭐",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            if (reviews.isEmpty()) {
                item {
                    Text(
                        text = "No hay reseñas aún. ¡Sé el primero en escribir una!",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            } else {
                items(reviews) { review ->
                    ReviewItem(
                        review = review,
                        userEmail = userEmail,
                        navController = navController,
                        film = film
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.navigate("${GhibliExplorerScreen.AddReview.name}/${film.id}")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text(
                text = "Añadir Reseña",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }

    LaunchedEffect(film.id) {
        reviewViewModel.loadReviews(film.id)
    }
}

@Composable
fun ReviewItem(
    review: Review,
    userEmail: String,
    navController: NavController,
    film: Film
) {
    val isUserReview = review.author == userEmail

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .shadow(4.dp, shape = RoundedCornerShape(12.dp))
            .clickable {
                if (isUserReview) {
                    // Si es la reseña del usuario, abrir el AddReviewDialog
                    navController.navigate("${GhibliExplorerScreen.AddReview.name}/${film.id}")
                }
            }
    ) {
        // Contenedor de los datos de la reseña
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = review.author,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "⭐ ${if (review.rating % 1 == 0f) review.rating.toInt() else String.format("%.1f", review.rating)}/5",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.date.toString(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Si es la reseña del usuario, agregar un indicador visual
            if (isUserReview) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tu reseña",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

