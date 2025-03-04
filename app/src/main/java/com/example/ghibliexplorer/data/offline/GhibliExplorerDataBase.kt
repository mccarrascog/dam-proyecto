package com.example.ghibliexplorer.data.offline

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ghibliexplorer.data.FavouriteFilm
import com.example.ghibliexplorer.data.Film
import com.example.ghibliexplorer.data.User

/**
 * Este archivo define la base de datos de Room para gestionar las películas favoritas.
 * La clase GhibliExplorerDataBase incluye la entidad Film y el DAO FavFilmDAO para realizar operaciones en la bd.
 */

@Database(entities = [Film::class, User::class, FavouriteFilm::class], version = 1, exportSchema = false)
abstract class GhibliExplorerDataBase : RoomDatabase() {
    abstract fun favFilmDao(): FavFilmDAO
    abstract fun filmDao(): FilmDAO
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var Instance: GhibliExplorerDataBase? = null

        fun getDatabase(context: Context): GhibliExplorerDataBase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, GhibliExplorerDataBase::class.java, "ghibli_favorites_db")
                    //.addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4) // 🔥 Agregamos migraciones
                    .build()
                    .also { Instance = it }
            }
        }
    }
}



