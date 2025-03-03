package com.example.ghibliexplorer.data.offline

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ghibliexplorer.data.FavouriteFilm
import com.example.ghibliexplorer.data.Film
import com.example.ghibliexplorer.data.User

/**
 * Este archivo define la base de datos de Room para gestionar las películas favoritas.
 * La clase GhibliExplorerDataBase incluye la entidad Film y el DAO FavFilmDAO para realizar operaciones en la bd.
 */

@Database(entities = [Film::class, User::class, FavouriteFilm::class], version = 3, exportSchema = false)
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // 🔥 Agregar migraciones
                    .fallbackToDestructiveMigration() // Opción si prefieres borrar la BD al fallar la migración
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Aquí defines los cambios en la estructura de la base de datos
        database.execSQL("CREATE TABLE IF NOT EXISTS `favouriteFilms` (`userId` INTEGER NOT NULL, `filmId` TEXT NOT NULL, PRIMARY KEY(`userId`, `filmId`), FOREIGN KEY(`userId`) REFERENCES `User`(`id`) ON DELETE CASCADE, FOREIGN KEY(`filmId`) REFERENCES `Film`(`id`) ON DELETE CASCADE)")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Agregar la columna createdAt a la tabla users
        database.execSQL("ALTER TABLE users ADD COLUMN createdAt TEXT NOT NULL DEFAULT ''")
    }
}

