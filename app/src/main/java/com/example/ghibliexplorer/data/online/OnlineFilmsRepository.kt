package com.example.ghibliexplorer.data.online

import com.example.ghibliexplorer.data.Film
import com.example.ghibliexplorer.network.FilmApiService

interface OnlineFilmsRepository {
    suspend fun getFilms() : List<Film>
    suspend fun getFilmDetails(filmId: String): Film?
}

class NetworkFilmsRepository(private val filmApiService: FilmApiService
) : OnlineFilmsRepository {
    override suspend fun getFilms(): List<Film> = filmApiService.getStudioGhibli()
    override suspend fun getFilmDetails(filmId: String): Film {
        return filmApiService.getFilmDetails(filmId = filmId)
    }
}