package com.example.ghibliexplorer.ui.screens.views

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ghibliexplorer.R
import com.example.ghibliexplorer.data.Film
import com.example.ghibliexplorer.ui.screens.viewmodel.AdminFilmsUiState
import com.example.ghibliexplorer.ui.screens.viewmodel.FilmsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ghibliexplorer.data.Review
import com.example.ghibliexplorer.ui.screens.viewmodel.ReviewViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminReviewsScreen(
    filmsViewModel: FilmsViewModel,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    when (filmsViewModel.adminFilmsUiState) {
        is AdminFilmsUiState.Loading -> {
            Log.e("AdminReviewsScreen", "Cargando películas...") // Log de carga
            LoadingScreen(modifier = modifier.fillMaxSize())
        }
        is AdminFilmsUiState.Success -> {
            Log.e("AdminReviewsScreen", "Películas cargadas con éxito: ${(filmsViewModel.adminFilmsUiState as AdminFilmsUiState.Success).films.size}") // Log cuando se cargan las películas
            AdminReviewsGridScreen(
                films = (filmsViewModel.adminFilmsUiState as AdminFilmsUiState.Success).films,
                modifier = modifier.fillMaxSize(),
                navController = navController
            )
        }
        is AdminFilmsUiState.Error -> {
            Log.e("AdminReviewsScreen", "Error al cargar las películas") // Log de error
            ErrorScreen2(retryAction, modifier = modifier.fillMaxSize())
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminReviewsGridScreen(
    films: List<Film>,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Log.e("AdminReviewsGridScreen", "Películas para mostrar: ${films.size}") // Log de la lista de películas

    // Obtenemos el ViewModel y las reseñas de todas las películas
    val reviewsViewModel: ReviewViewModel = viewModel(factory = ReviewViewModel.Factory)
    val reviews by reviewsViewModel.reviews.collectAsState()
    val isLoading by reviewsViewModel.isLoading.collectAsState()

    // Solo cargamos las reseñas una vez, cuando se monta el componente
    LaunchedEffect(Unit) {
        if (reviews.isEmpty()) {
            // Cargar todas las reseñas si no se han cargado previamente
            reviewsViewModel.loadReviewsForAllFilms()
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(8.dp) // Añadimos padding a la lista entera
    ) {
        items(films) { film ->
            Log.d("FilmReviewDebug", "Mostrando película: ${film.title}") // Log por cada película mostrada
            FilmCard2(film = film, reviews = reviews, isLoading = isLoading, modifier = Modifier.padding(bottom = 16.dp)) // Separación entre tarjetas
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FilmCard2(
    film: Film,
    reviews: List<Review>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    // Filtrar las reseñas para esta película
    val filteredReviews = remember(reviews) {
        reviews.filter { it.filmId.trim() == film.id.trim() }
    }
    val reviewCount = filteredReviews.size

    // Log para ver si está cambiando el estado de carga
    LaunchedEffect(isLoading) {
        println("Cargando: $isLoading, cantidad de reseñas: $reviewCount")
    }

    // El Card para mostrar la información de la película
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de la película
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(film.imageLink?.replace("http:", "https:"))
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(id = R.string.film_photo),
                contentScale = ContentScale.Fit,
                error = painterResource(id = R.drawable.ic_broken_image),
                placeholder = painterResource(id = R.drawable.loading_img),
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = film.title ?: "Untitled",
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Mostrar un indicador de carga solo si estamos cargando las reseñas
                if (isLoading && reviewCount == 0) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MailOutline,
                            contentDescription = "Comentarios",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reseñas: $reviewCount",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorScreen2(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Log.e("ErrorScreen2", "Mostrando pantalla de error...") // Log de error

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error),
            contentDescription = "connection_error_description",
            modifier = Modifier.size(120.dp) // Ajustamos el tamaño de la imagen
        )
        Text(
            text = stringResource(id = R.string.loading_failed),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        Button(
            onClick = retryAction,
            modifier = Modifier.padding(top = 8.dp) // Espacio entre el texto y el botón
        ) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}