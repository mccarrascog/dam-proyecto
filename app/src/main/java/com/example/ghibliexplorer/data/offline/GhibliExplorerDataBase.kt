package com.example.ghibliexplorer.data.offline

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ghibliexplorer.data.Film

@Database(entities=[Film::class], version=1, exportSchema= false)

abstract class GhibliExplorerDataBase: RoomDatabase() {
    abstract fun filmDao(): FavFilmDAO

    companion object {
        @Volatile
        private var Instance: GhibliExplorerDataBase? = null

        fun getDatabase(context: Context): GhibliExplorerDataBase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, GhibliExplorerDataBase::class.java, "ghibli_favorites_db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}