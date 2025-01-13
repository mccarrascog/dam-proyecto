package com.example.ghibliexplorer.data.offline

import android.content.Context

interface OfflineAppContainer{
    val OfflineFilmsRepository : OfflineFilmsRepository
}

class DefaultOfflineAppContainer(private val context: Context) : OfflineAppContainer {
    private val favFilmDAO: FavFilmDAO by lazy {
        GhibliExplorerDataBase.getDatabase(context).filmDao()
    }

    override val OfflineFilmsRepository: OfflineFilmsRepository by lazy {
        LocalFilmsRepository(favFilmDAO)
    }
}