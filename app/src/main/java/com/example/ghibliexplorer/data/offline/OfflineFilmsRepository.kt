package com.example.ghibliexplorer.data.offline

import com.example.ghibliexplorer.data.FavouriteFilm
import com.example.ghibliexplorer.data.Film
import kotlinx.coroutines.flow.Flow

/**
 * Este archivo define el repositorio local para gestionar las películas favoritas en la bd.
 * La interfaz OfflineFilmsRepository declara las operaciones como añadir, eliminar y consultar películas favoritas.
 * La clase LocalFilmsRepository implementa estas operaciones utilizando el DAO FavFilmDAO, que interactúa con la bd local.
 */

interface OfflineFilmsRepository {
    // Métodos para la tabla "films"
    suspend fun insertFilm(film: Film)
    suspend fun isFilmInDatabase(filmId: String): Boolean
    fun getAllFilms(): Flow<List<Film>>

    // Métodos para la tabla "favourites"
    suspend fun addToFavourites(favouriteFilm: FavouriteFilm)
    suspend fun deleteFromFavourites(favouriteFilm: FavouriteFilm)
    fun getAllFavouriteFilmsByUser(userId: String): Flow<List<Film>>
    fun isFilmInFavs(filmId: String, userId: String): Flow<Boolean>
}


class LocalFilmsRepository(
    private val filmDAO: FilmDAO,  // DAO para la tabla films
    private val favFilmDAO: FavFilmDAO // DAO para la tabla favourites
) : OfflineFilmsRepository {

    // Métodos para la tabla "films"
    override suspend fun insertFilm(film: Film) {
        filmDAO.insertFilm(film)
    }

    override suspend fun isFilmInDatabase(filmId: String): Boolean {
        return filmDAO.isFilmInDatabase(filmId) > 0
    }

    override fun getAllFilms(): Flow<List<Film>> {
        return filmDAO.getAllFilms()
    }

    // Métodos para la tabla "favourites"
    override suspend fun addToFavourites(favouriteFilm: FavouriteFilm) {
        favFilmDAO.insertFavourite(favouriteFilm)
    }

    override suspend fun deleteFromFavourites(favouriteFilm: FavouriteFilm) {
        favFilmDAO.deleteFromFavourites(favouriteFilm)
    }

    override fun getAllFavouriteFilmsByUser(userId: String): Flow<List<Film>> {
        return favFilmDAO.getAllFavouriteFilmsByUser(userId)
    }

    override fun isFilmInFavs(filmId: String, userId: String): Flow<Boolean> {
        return favFilmDAO.isFilmInFavs(filmId, userId)
    }
}