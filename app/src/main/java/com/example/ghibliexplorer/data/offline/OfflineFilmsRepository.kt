package com.example.ghibliexplorer.data.offline

import com.example.ghibliexplorer.data.Film
import kotlinx.coroutines.flow.Flow

interface OfflineFilmsRepository {
    suspend fun addToFavourites(film: Film)
    suspend fun deleteFromFavourites(film: Film)
    fun getAllFavouriteFilms(): Flow<List<Film>>
    fun isFilmInFavs(filmId: String): Flow<Boolean>
}

class LocalFilmsRepository(
    private val favFilmDAO: FavFilmDAO
) : OfflineFilmsRepository {
    // Implementación de los métodos definidos en OfflineFilmsRepository
    override suspend fun addToFavourites(film: Film) = favFilmDAO.insertFavourite(film)

    override suspend fun deleteFromFavourites(film: Film) = favFilmDAO.deleteFromFavourites(film)

    override fun getAllFavouriteFilms(): Flow<List<Film>> {
        return favFilmDAO.getAllFavouriteFilms()
    }

    override fun isFilmInFavs(filmId: String): Flow<Boolean> {
        return favFilmDAO.isFilmInFavs(filmId)
    }
}