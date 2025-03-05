package com.example.ghibliexplorer.ui.screens.views

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ghibliexplorer.GhibliExplorerScreen
import com.example.ghibliexplorer.R
import com.example.ghibliexplorer.data.Film

@Composable
fun FilmDetailScreen(
    film: Film,
    navController: NavController,
    onAddFavouritesButtonClicked: () -> Unit,
    onRemoveFromFavsButtonClicked: () -> Unit,
    isFilmInFavs: Boolean,
    modifier: Modifier
) {
    val isHorizontal = LocalContext.current.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(id= R.drawable.logoghibli2__3_),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        if (isHorizontal) {
            Row(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mostrar detalles de la película
                // Mostrar imagen de la película usando AsyncImage
                Column {
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
                            .aspectRatio(1f)
                            .size(50.dp)
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 16.dp)
                ) {
                    item {
                        Text(
                            text = (film.title) ?: "Sin título",
                            style = MaterialTheme.typography.displayLarge,
                        )
                        // Mostrar titulo original y romanizado de la película
                        Text(
                            text = ("Original Title: " + film.originalTitle + " (" + film.originalTitleRomanised + ")")
                                ?: "Sin título",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        // Mostrar director y productor de la película
                        Text(
                            text = ("Director: " + film.director) ?: "Sin director",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = ("Producer: " + film.producer) ?: "Sin producer",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        // Mostrar año de lanzamiento y duración en minutos
                        Text(
                            text = ("Release Date: " + film.releaseDate) ?: "Sin fecha",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = ("Running Time: " + film.runningTime + " min.")
                                ?: "Sin duración",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        // Mostrar descripción de la película
                        Text(
                            text = ("Description: " + film.description) ?: "Sin descripción",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        //Mostrar botón de añadir o borrar de favs
                        IconButton(
                            onClick = if (isFilmInFavs) onRemoveFromFavsButtonClicked else onAddFavouritesButtonClicked,
                            modifier = Modifier.size(70.dp)
                        ) {
                            Image(
                                painter = painterResource(id = if (isFilmInFavs) R.drawable.nofav__1_ else R.drawable.favorito),
                                contentDescription = null,
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { navController.navigate("${GhibliExplorerScreen.Reviews.name}/${film.id}") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text("View reviews")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { navController.navigate("${GhibliExplorerScreen.AddReview.name}/${film.id}") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text("Add review")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Mostrar detalles de la película
                item {
                    Text(
                        text = (film.title) ?: "Sin título",
                        style = MaterialTheme.typography.displayLarge,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mostrar imagen de la película usando AsyncImage
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
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mostrar titulo original y romanizado de la película
                    Text(
                        text = ("Original Title: " + film.originalTitle + " (" + film.originalTitleRomanised + ")")
                            ?: "Sin título",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mostrar director y productor de la película
                    Text(
                        text = ("Director: " + film.director) ?: "Sin director",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = ("Producer: " + film.producer) ?: "Sin producer",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mostrar año de lanzamiento y duración en minutos
                    Text(
                        text = ("Release Date: " + film.releaseDate) ?: "Sin fecha",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = ("Running Time: " + film.runningTime + " min.") ?: "Sin duración",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mostrar descripción de la película
                    Text(
                        text = ("Description: " + film.description) ?: "Sin descripción",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Justify
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    //Mostrar botón de añadir o borrar de favs
                    IconButton(
                        onClick = if (isFilmInFavs) onRemoveFromFavsButtonClicked else onAddFavouritesButtonClicked,
                        modifier = Modifier.size(90.dp)
                    ) {
                        Image(
                            painter = painterResource(id = if (isFilmInFavs) R.drawable.nofav__1_ else R.drawable.favorito),
                            contentDescription = null,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            // Navegar a la pantalla de reseñas con el filmId
                            navController.navigate("${GhibliExplorerScreen.Reviews.name}/${film.id}")
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text("View reviews")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { navController.navigate("${GhibliExplorerScreen.AddReview.name}/${film.id}") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text("Add review")
                    }
                }
            }
        }
    }
}