package com.example.ghibliexplorer.ui.screens.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ghibliexplorer.data.Film
import com.example.ghibliexplorer.data.Review
import com.example.ghibliexplorer.ui.screens.viewmodel.ReviewViewModel
import com.example.ghibliexplorer.utils.getUserEmail
import java.util.Date
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddReviewDialog(
    film: Film,
    onDismiss: () -> Unit,
    viewModel: ReviewViewModel // Se pasa el ViewModel
) {
    val context = LocalContext.current
    val userEmail = getUserEmail(context).orEmpty()

    // Obtener la reseña del usuario si existe en el ViewModel
    LaunchedEffect(film.id) {
        viewModel.loadReviews(film.id)
    }

    // Recuperar la reseña existente (si la hay) desde el ViewModel
    val userReview = viewModel.getUserReviewForFilm(film.id, userEmail)

    // Usar los valores de la reseña si existe, o inicializar valores vacíos si no existe
    var comment by remember { mutableStateOf(userReview?.comment ?: "") }
    var rating by remember { mutableStateOf(userReview?.rating ?: 3f) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    // Cuando userReview cambia, actualizar los valores
    userReview?.let {
        comment = it.comment
        rating = it.rating
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (userReview == null) "Add Review" else "Update Review") },
        text = {
            Column {
                TextField(
                    value = userEmail,
                    onValueChange = {},
                    label = { Text("Author") },
                    enabled = false, // El usuario no debe poder cambiar su email
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comment") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = rating,
                    onValueChange = { rating = it },
                    valueRange = 1f..5f,
                    steps = 7
                )

                Text(text = "Rating: ${if (rating % 1 == 0f) rating.toInt() else String.format("%.1f", rating)}⭐")
            }
        },
        confirmButton = {
            Button(onClick = {
                // Si ya existe una reseña, la actualizamos; si no, la creamos.
                val newReview = if (userReview == null) {
                    // Crear una nueva reseña con un ID generado aleatoriamente si no hay reseña previa
                    Review(
                        id = UUID.randomUUID().toString(),
                        filmId = film.id,
                        author = userEmail,
                        rating = rating,
                        comment = comment,
                        date = Date()
                    )
                } else {
                    // Si ya existe una reseña, reutilizamos su ID y actualizamos los demás campos
                    Review(
                        id = userReview.id,  // Mantener el mismo ID que la reseña existente
                        filmId = film.id,
                        author = userEmail,
                        rating = rating,
                        comment = comment,
                        date = Date()
                    )
                }

                // Llamar al método adecuado en el ViewModel dependiendo de si existe una reseña
                if (userReview == null) {
                    viewModel.addReview(newReview)
                } else {
                    viewModel.editReview(newReview)
                }

                onDismiss() // Cerrar el diálogo
            }) {
                Text(if (userReview == null) "Save" else "Update", style = MaterialTheme.typography.bodySmall)  // Estilo más pequeño
            }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).padding(2.dp)
                ) {
                    Text("Cancel", style = MaterialTheme.typography.bodySmall)
                }

                if (userReview != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { showDeleteConfirmationDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.weight(1f).padding(2.dp)
                    ) {
                        Text("Delete", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    )

    // Diálogo de confirmación para eliminar la reseña
    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text(text = "Delete Review") },
            text = { Text("Are you sure you want to delete this review?") },
            confirmButton = {
                Button(
                    onClick = {
                        userReview?.let { viewModel.deleteReview(it) }
                        showDeleteConfirmationDialog = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red // Establecer el fondo del botón como rojo
                    ),
                    modifier = Modifier.size(100.dp, 40.dp)  // Hacer el botón más pequeño
                ) {
                    Text("Delete", color = Color.White, style = MaterialTheme.typography.bodySmall)  // Estilo más pequeño
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmationDialog = false },
                    modifier = Modifier.size(100.dp, 40.dp)  // Hacer el botón más pequeño
                ) {
                    Text("Cancel", style = MaterialTheme.typography.bodySmall)  // Estilo más pequeño
                }
            }
        )
    }
}