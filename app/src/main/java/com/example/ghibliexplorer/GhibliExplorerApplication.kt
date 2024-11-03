package com.example.ghibliexplorer

import android.app.Application
import com.example.ghibliexplorer.data.online.DefaultOnlineAppContainer
import com.example.ghibliexplorer.data.online.OnlineAppContainer

class GhibliExplorerApplication : Application(){
    lateinit var container: OnlineAppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultOnlineAppContainer()
        // Inicializar variables globales o componentes como bases de datos, APIs, etc.
    }
}