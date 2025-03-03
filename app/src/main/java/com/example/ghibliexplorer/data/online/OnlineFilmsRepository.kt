package com.example.ghibliexplorer.data.online

import com.example.ghibliexplorer.data.Film
import com.example.ghibliexplorer.network.FilmApiService

/**
 * Este archivo define un repositorio para obtener información sobre películas en línea.
 *
 * La interfaz OnlineFilmsRepository establece los métodos necesarios para recuperar una lista de películas
 * y detalles de una película específica.
 *
 * La implementación NetworkFilmsRepository utiliza un servicio de red (FilmApiService) para obtener los datos.
 */
interface OnlineFilmsRepository {
    suspend fun getFilms(): List<Film>
    suspend fun getFilmById(filmId: String): Film?
}

class NetworkFilmsRepository(private val filmApiService: FilmApiService) : OnlineFilmsRepository {
    override suspend fun getFilms(): List<Film> = filmApiService.getStudioGhibli()

    override suspend fun getFilmById(filmId: String): Film? {
        return filmApiService.getFilmDetails(filmId = filmId)
    }
}