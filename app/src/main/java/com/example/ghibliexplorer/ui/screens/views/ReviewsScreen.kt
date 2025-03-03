package com.example.ghibliexplorer.ui.screens.views

import android.os.Build
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

    val context = LocalContext.current
    val userEmail = getUserEmail(context).orEmpty()

    // Ajusta el padding superior para no tapar contenido con el TopAppBar
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding() // Asegura que el contenido respete la barra de estado
            .padding(top = 56.dp) // Ajusta el valor del top padding según el tamaño de tu TopAppBar
            .padding(16.dp) // Padding adicional para el contenido de la pantalla
    ) {
        Text(
            text = "Reseñas de ${film.title ?: "Película desconocida"}",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

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
                // Aquí navegamos a la pantalla de añadir reseña
                navController.navigate("${GhibliExplorerScreen.AddReview.name}/${film.id}")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(8.dp)) // Redondea el borde del botón
                .background(MaterialTheme.colorScheme.primary) // Estilo de fondo
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

    // Aquí añadimos un efecto para actualizar las reseñas cuando se regresa de la pantalla de añadir reseña.
    LaunchedEffect(film.id) {
        // Actualiza las reseñas cuando el usuario regresa de la pantalla de añadir reseña
        reviewViewModel.getReviewsForFilm(film.id)
    }
}

@Composable
fun ReviewItem(
    review: Review,
    userEmail: String,
    navController: NavController,
    film: Film
) {
    val isUserReview = review.author == userEmail // Verifica si esta es la reseña del usuario

    // Contenedor principal con borde redondeado, fondo y sombra
    Column(
        modifier = Modifier
            .fillMaxWidth() // Asegura que ocupe todo el ancho disponible
            .clip(RoundedCornerShape(12.dp)) // Aplica bordes redondeados al fondo
            .background(MaterialTheme.colorScheme.surface) // Fondo de la reseña
            .shadow(4.dp, shape = RoundedCornerShape(12.dp)) // Sombra sutil y redondeada
            .clickable {
                if (isUserReview) {
                    // Si es la reseña del usuario, abrir el AddReviewDialog
                    navController.navigate("${GhibliExplorerScreen.AddReview.name}/${film.id}")
                }
            }
    ) {
        // Contenedor de los datos de la reseña, con padding interno
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
                text = "⭐ ${review.rating}/5",
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

