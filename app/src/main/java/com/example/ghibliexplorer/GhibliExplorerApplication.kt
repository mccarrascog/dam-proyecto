package com.example.ghibliexplorer

import android.app.Application
import com.example.ghibliexplorer.data.offline.DefaultOfflineAppContainer
import com.example.ghibliexplorer.data.offline.OfflineAppContainer
import com.example.ghibliexplorer.data.online.DefaultOnlineAppContainer
import com.example.ghibliexplorer.data.online.OnlineAppContainer

/**
 * Este archivo define la clase GhibliExplorerApplication, que inicializa
 * los contenedores de repositorios para el acceso a datos tanto online como offline.
 */

class GhibliExplorerApplication : Application() {
    val offlineAppContainer: OfflineAppContainer by lazy {
        DefaultOfflineAppContainer(this)
    }

    lateinit var container: OnlineAppContainer

    override fun onCreate() {
        super.onCreate()

        //deleteDatabase("ghibli_favorites_db")

        container = DefaultOnlineAppContainer()
    }
}
