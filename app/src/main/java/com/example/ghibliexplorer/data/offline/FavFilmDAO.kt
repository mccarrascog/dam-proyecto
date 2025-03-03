package com.example.ghibliexplorer.data.offline

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ghibliexplorer.data.FavouriteFilm
import com.example.ghibliexplorer.data.Film
import kotlinx.coroutines.flow.Flow

/**
 * Este archivo define un DAO para gestionar las películas favoritas en la base de datos.
 * Proporciona métodos para insertar, eliminar, obtener todas las películas favoritas y verificar si una película está en los favoritos.
 */

@Dao
interface FavFilmDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavourite(favouriteFilm: FavouriteFilm)

    @Delete
    suspend fun deleteFromFavourites(favouriteFilm: FavouriteFilm)

    @Query("""
        SELECT films.* FROM films
        INNER JOIN favouriteFilms ON films.id = favouriteFilms.filmId 
        WHERE favouriteFilms.userId = :userId 
        ORDER BY films.title ASC
    """)
    fun getAllFavouriteFilmsByUser(userId: String): Flow<List<Film>>

    @Query("""
        SELECT EXISTS (
            SELECT 1 FROM favouriteFilms 
            WHERE filmId = :filmId AND userId = :userId
        )
    """)
    fun isFilmInFavs(filmId: String, userId: String): Flow<Boolean>
}
