package com.example.ghibliexplorer.data.offline

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ghibliexplorer.data.Film
import kotlinx.coroutines.flow.Flow

@Dao
interface FavFilmDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavourite(favoriteFilm: Film)

    @Delete
    suspend fun deleteFromFavourites(film: Film)

    @Query("SELECT * FROM favouriteFilms ORDER BY title ASC")
    fun getAllFavouriteFilms(): Flow<List<Film>>

    @Query("SELECT EXISTS (SELECT 1 FROM favouriteFilms WHERE id = :filmId)")
    fun isFilmInFavs(filmId: String): Flow<Boolean>
}
