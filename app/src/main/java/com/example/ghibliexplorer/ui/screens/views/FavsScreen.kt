package com.example.ghibliexplorer.ui.screens.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ghibliexplorer.GhibliExplorerScreen
import com.example.ghibliexplorer.R
import com.example.ghibliexplorer.data.Film
import com.example.ghibliexplorer.ui.screens.viewmodel.FavFilmsUiState
import kotlinx.coroutines.flow.Flow

@Composable
fun FavsScreen(
    favFilmsUiState: FavFilmsUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    when (favFilmsUiState) {
        is FavFilmsUiState.Loading -> FavLoadingScreen(modifier = modifier.fillMaxSize())
        is FavFilmsUiState.Success -> FavFilmsGridScreen(films = favFilmsUiState.favFilms,
            modifier = modifier.fillMaxSize(),
            navController = navController)
        is FavFilmsUiState.Error -> FavErrorScreen(retryAction, modifier = modifier.fillMaxSize())
    }
}

@Composable
fun FavLoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(200.dp),
            painter = painterResource(id = R.drawable.loading_img),
            contentDescription = stringResource(id = R.string.loading)
        )
    }
}

@Composable
fun FavErrorScreen(retryAction:()->Unit, modifier : Modifier = Modifier){
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
fun FavFilmCard(film: Film, modifier: Modifier = Modifier){
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
fun FavFilmsGridScreen(
    films: Flow<List<Film>>,
    modifier: Modifier = Modifier,
    navController: NavController
){
    // Recolectar el flujo y convertirlo en un estado observable
    val filmsState by films.collectAsState(initial = emptyList())
    if(filmsState.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id= R.drawable.logoghibli2__3_),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(text = "NO FAVOURITES YET",
                    style = MaterialTheme.typography.displayLarge)
            }
        }
    } else {
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
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                contentPadding = PaddingValues(4.dp),
                modifier = modifier.fillMaxWidth()
            ) {
                items(items = filmsState, key = { film -> film.id }) { film ->
                    FavFilmCard(
                        film = film,
                        modifier = modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                            .clickable { navController.navigate(GhibliExplorerScreen.FilmDetail.name + "/${film.id}") }
                    )
                }
            }
        }
    }
}