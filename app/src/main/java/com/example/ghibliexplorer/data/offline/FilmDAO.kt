package com.example.ghibliexplorer.data.offline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ghibliexplorer.data.Film
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFilm(film: Film)

    @Query("SELECT COUNT(*) FROM films WHERE id = :filmId")
    suspend fun isFilmInDatabase(filmId: String): Int

    @Query("SELECT * FROM films")
    fun getAllFilms(): Flow<List<Film>>
}
