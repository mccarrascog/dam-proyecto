package com.example.ghibliexplorer.ui.screens.views

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ghibliexplorer.R

@Composable
fun StartScreen(
    onStartButtonClicked: () -> Unit,
    onFavouritesButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
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
        if(isHorizontal){
            Row(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoghibli_removebg_preview__2_),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.width(40.dp))
                Column {
                    Button(
                        onClick = onStartButtonClicked,
                        Modifier.widthIn(min = 250.dp)
                    ) {
                        Text(
                            text = "View all films",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Button(
                        onClick = onFavouritesButtonClicked,
                        Modifier.widthIn(min = 250.dp)
                    ) {
                        Text(
                            text = "Favourites",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Image(
                        painter = painterResource(id = R.drawable.logoghibli_removebg_preview__2_),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(350.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        onClick = onStartButtonClicked,
                        Modifier.widthIn(min = 250.dp)
                    ) {
                        Text(
                            text = "View all films",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        onClick = onFavouritesButtonClicked,
                        Modifier.widthIn(min = 250.dp)
                    ) {
                        Text(
                            text = "Favourites",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}