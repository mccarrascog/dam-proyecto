package com.example.ghibliexplorer.network

import com.example.ghibliexplorer.data.Film
import retrofit2.http.GET
import retrofit2.http.Path

interface FilmApiService {
    @GET("films")
    suspend fun getStudioGhibli(): List<Film>

    @GET("films/{id}")
    suspend fun getFilmDetails(@Path("id") filmId: String): Film
}