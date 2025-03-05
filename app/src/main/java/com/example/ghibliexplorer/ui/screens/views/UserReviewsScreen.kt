package com.example.ghibliexplorer.ui.screens.views

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ghibliexplorer.GhibliExplorerScreen
import com.example.ghibliexplorer.data.Review
import com.example.ghibliexplorer.ui.screens.viewmodel.ReviewViewModel
import com.example.ghibliexplorer.utils.getUserEmail
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontStyle
import com.example.ghibliexplorer.data.Film
import com.example.ghibliexplorer.ui.screens.viewmodel.FilmsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserReviewsScreen(
    reviewViewModel: ReviewViewModel,
    filmsViewModel: FilmsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Obtenemos todas las reseñas
    val allReviews by reviewViewModel.reviews.collectAsState()
    // Obtenemos el correo del usuario logueado
    val context = LocalContext.current
    val userEmail = getUserEmail(context).orEmpty()

    // Filtramos las reseñas por el correo del usuario
    val userReviews = allReviews.filter { it.author == userEmail }

    LaunchedEffect(userReviews) {
        Log.d("UserReviewsScreen", "Reseñas del usuario: $userReviews")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(top = 56.dp)
            .padding(16.dp)
    ) {
        Text(
            text = "Your Reviews",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (userReviews.isNotEmpty()) {
            val averageRating = userReviews.map { it.rating }.average()
            Text(
                text = "Average Rating: ${
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
            if (userReviews.isEmpty()) {
                item {
                    Text(
                        text = "No reviews yet :(",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            } else {
                items(userReviews) { review ->
                    ReviewItem(
                        review = review,
                        userEmail = userEmail,
                        filmsViewModel = filmsViewModel,
                        reviewViewModel = reviewViewModel,
                        navController = navController
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Cargar todas las reseñas al iniciar la pantalla
    LaunchedEffect(userEmail) {
        reviewViewModel.loadReviewsForAllFilms()
    }
}

@Composable
fun ReviewItem(
    review: Review,
    userEmail: String,
    filmsViewModel: FilmsViewModel,
    reviewViewModel: ReviewViewModel,
    navController: NavController
) {
    val isUserReview = review.author == userEmail

    // Estado para almacenar la película
    var film by remember { mutableStateOf<Film?>(null) }

    // Cargar la película cuando cambia el filmId
    LaunchedEffect(review.filmId) {
        film = filmsViewModel.getFilmObjectById(review.filmId)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .shadow(4.dp, shape = RoundedCornerShape(12.dp))
            .clickable {
                if (isUserReview) {
                    navController.navigate("${GhibliExplorerScreen.AddReview.name}/${review.filmId}")
                }
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Mostrar el título de la película si se ha cargado
            film?.title?.let { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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

            if (isUserReview) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your review",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}
