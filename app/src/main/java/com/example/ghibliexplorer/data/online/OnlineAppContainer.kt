package com.example.ghibliexplorer.data.online

import com.example.ghibliexplorer.network.FilmApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

/**
 * Contenedor de dependencias para la capa de red de la aplicación.
 *
 * Esta clase configura Retrofit y proporciona un repositorio (OnlineFilmsRepository)
 * para interactuar con la API de películas de Studio Ghibli.
 *
 * - OnlineAppContainer define la estructura del contenedor de dependencias.
 * - DefaultOnlineAppContainer implementa la interfaz y configura Retrofit.
 * - Se utiliza "by lazy" para inicializar las dependencias solo cuando se necesiten.
 */

interface OnlineAppContainer{
    val OnlineFilmsRepository : OnlineFilmsRepository
}

class DefaultOnlineAppContainer() : OnlineAppContainer {
    private val BASE_URL = "https://ghibliapi.vercel.app/"

    private val json = Json {
        ignoreUnknownKeys=true
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .build()

    private val retrofitService : FilmApiService by lazy {
        retrofit.create(FilmApiService::class.java)
    }

    override val OnlineFilmsRepository: OnlineFilmsRepository by lazy {
        NetworkFilmsRepository(retrofitService)
    }
}