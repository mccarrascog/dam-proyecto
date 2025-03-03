package com.example.ghibliexplorer.data.offline

import android.content.Context

/**
 * Este archivo define un contenedor de la aplicación para gestionar los repositorios locales.
 * La interfaz OfflineAppContainer proporciona acceso al repositorio OfflineFilmsRepository.
 * La clase DefaultOfflineAppContainer implementa este contenedor, inicializando el repositorio local utilizando Room y el DAO FavFilmDAO.
 */

interface OfflineAppContainer{
    val OfflineFilmsRepository : OfflineFilmsRepository
    val OfflineUsersRepository: OfflineUsersRepository

}

class DefaultOfflineAppContainer(private val context: Context) : OfflineAppContainer {
    private val database: GhibliExplorerDataBase by lazy {
        GhibliExplorerDataBase.getDatabase(context)
    }

    private val favFilmDAO: FavFilmDAO by lazy {
        database.favFilmDao()
    }

    private val filmDAO: FilmDAO by lazy {
        database.filmDao()
    }

    private val userDao: UserDao by lazy {
        database.userDao()
    }

    override val OfflineFilmsRepository: OfflineFilmsRepository by lazy {
        LocalFilmsRepository(filmDAO,favFilmDAO)
    }

    override val OfflineUsersRepository: OfflineUsersRepository by lazy {
        LocalUserRepository(userDao)
    }
}
