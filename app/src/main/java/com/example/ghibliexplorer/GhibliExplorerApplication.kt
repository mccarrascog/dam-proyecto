package com.example.ghibliexplorer

import android.app.Application
import com.example.ghibliexplorer.data.offline.DefaultOfflineAppContainer
import com.example.ghibliexplorer.data.offline.OfflineAppContainer
import com.example.ghibliexplorer.data.online.DefaultOnlineAppContainer
import com.example.ghibliexplorer.data.online.OnlineAppContainer

class GhibliExplorerApplication : Application(){
    val offlineAppContainer: OfflineAppContainer by lazy {
        DefaultOfflineAppContainer(this)
    }

    lateinit var container: OnlineAppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultOnlineAppContainer()
        // Inicializar variables globales o componentes como bases de datos, APIs, etc.
    }
}