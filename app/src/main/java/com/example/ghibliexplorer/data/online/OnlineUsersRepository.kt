package com.example.ghibliexplorer.data.online

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ghibliexplorer.data.User
import com.example.ghibliexplorer.network.FirebaseService

/**
 * Este archivo define un repositorio para gestionar los usuarios.
 * La interfaz OnlineUsersRepository especifica el método para obtener un usuario por su correo electrónico.
 * La implementación FirebaseUsersRepository utiliza el servicio FirebaseService para interactuar con la base de datos de Firebase.
 */

interface OnlineUsersRepository {
    suspend fun getUserByEmailOnline(userEmail: String): User?
    suspend fun getUsers(): List<User>
    suspend fun getUserRole(userEmail: String): String?
}

class FirebaseUsersRepository(private val firebaseService: FirebaseService) : OnlineUsersRepository {
    override suspend fun getUserByEmailOnline(userEmail: String): User? {
        return firebaseService.getUserByEmail(userEmail)
    }

    override suspend fun getUsers(): List<User> {
        return firebaseService.getUsers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getUserRole(userEmail: String): String? {
        return firebaseService.getUserRole(userEmail)
    }
}
