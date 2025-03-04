package com.example.ghibliexplorer.ui.screens.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ghibliexplorer.GhibliExplorerScreen
import com.example.ghibliexplorer.data.Film
import com.example.ghibliexplorer.ui.screens.viewmodel.FilmsUiState
import com.example.ghibliexplorer.R

@Composable
fun HomeScreen(
    filmsUiState: FilmsUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    when (filmsUiState) {
        is FilmsUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is FilmsUiState.Success -> FilmsGridScreen(films = filmsUiState.films,
            modifier = modifier.fillMaxSize(),
            navController = navController)
        is FilmsUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(200.dp),
            painter = painterResource(id = R.drawable.loading_img),
            contentDescription = stringResource(id = R.string.loading))
    }
}

@Composable
fun ErrorScreen(retryAction:()->Unit, modifier : Modifier = Modifier){
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error),
            contentDescription = "")
        Text(text = stringResource(
            id = R.string.loading_failed),
            modifier= Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}

@Composable
fun FilmCard(film: Film, modifier: Modifier = Modifier){
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*film.title?.let {
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    modifier = modifier.padding(top = 4.dp, bottom = 8.dp)
                )
            }*/
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(film.imageLink?.replace("http:", "https:"))
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(id = R.string.film_photo),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_broken_image),
                placeholder = painterResource(id = R.drawable.loading_img),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun FilmsGridScreen(
    films: List<Film>,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    var searchText by remember { mutableStateOf("") }

    val filteredFilms = films.filter { film ->
        film.title?.contains(searchText, ignoreCase = true) == true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text("Search for movies...") },
            singleLine = true
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoghibli2__3_),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            if (filteredFilms.isEmpty()) {
                Text(
                    text = "Oops! No results found :(",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    contentPadding = PaddingValues(4.dp),
                    modifier = modifier.fillMaxWidth()
                ) {
                    items(items = filteredFilms, key = { film -> film.id }) { film ->
                        FilmCard(
                            film = film,
                            modifier = modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(GhibliExplorerScreen.FilmDetail.name + "/${film.id}")
                                }
                        )
                    }
                }
            }
        }
    }
}