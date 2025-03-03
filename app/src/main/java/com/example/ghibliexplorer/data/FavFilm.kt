package com.example.ghibliexplorer.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    tableName = "favouriteFilms",
    primaryKeys = ["userId", "filmId"],
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = CASCADE),
        ForeignKey(entity = Film::class, parentColumns = ["id"], childColumns = ["filmId"], onDelete = CASCADE)
    ]
)
data class FavouriteFilm(
    val userId: String,  // ID del usuario
    val filmId: String // ID de la película
)
