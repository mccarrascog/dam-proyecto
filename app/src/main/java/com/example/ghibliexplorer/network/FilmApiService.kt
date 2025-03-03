package com.example.ghibliexplorer.network

import com.example.ghibliexplorer.data.Film
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Este archivo define la interfaz FilmApiService para interactuar con la API de Studio Ghibli.
 **/

interface FilmApiService {
    @GET("films")
    suspend fun getStudioGhibli(): List<Film>

    @GET("films/{id}")
    suspend fun getFilmDetails(@Path("id") filmId: String): Film
}