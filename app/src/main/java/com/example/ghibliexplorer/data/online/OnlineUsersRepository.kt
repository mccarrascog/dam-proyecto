package com.example.ghibliexplorer.data.online

import com.example.ghibliexplorer.data.User
import com.example.ghibliexplorer.network.FirebaseService

/**
 * Este archivo define un repositorio para gestionar los usuarios.
 * La interfaz OnlineUsersRepository especifica el método para obtener un usuario por su correo electrónico.
 * La implementación FirebaseUsersRepository utiliza el servicio FirebaseService para interactuar con la base de datos de Firebase.
 */

interface OnlineUsersRepository {
    suspend fun getUserByEmail(userEmail: String): User?
}

class FirebaseUsersRepository(private val firebaseService: FirebaseService) : OnlineUsersRepository {
    override suspend fun getUserByEmail(userEmail: String): User? {
        return firebaseService.getUserByEmail(userEmail)
    }
}
