package com.example.ghibliexplorer.ui.screens.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ghibliexplorer.data.User
import com.example.ghibliexplorer.data.offline.LocalUserRepository
import com.example.ghibliexplorer.data.offline.OfflineUsersRepository
import com.example.ghibliexplorer.data.online.FirebaseUsersRepository
import com.example.ghibliexplorer.utils.SHA
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel(
    private val firebaseUsersRepository: FirebaseUsersRepository,
    private val offlineUsersRepository: OfflineUsersRepository // Para Room
) : ViewModel() {

    // Estado que mantiene el resultado del login
    private val _loginResult = MutableStateFlow<LoginResult?>(null)
    val loginResult: StateFlow<LoginResult?> get() = _loginResult

    // Clase para representar el resultado del login
    data class LoginResult(val success: Boolean, val errorMessage: String? = null)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val hashedPassword = SHA.generate512(password) ?: run {
                    _loginResult.value = LoginResult(success = false, errorMessage = "Password hashing failed")
                    return@launch
                }

                // Verificar en Firestore
                val user = authenticateUser(email, hashedPassword)

                if (user != null) {
                    // Verificar si los datos del usuario han cambiado (por ejemplo, el rol)
                    val localUser = offlineUsersRepository.getUserByEmail(email)
                    if (localUser != null) {
                        Log.e("Login", "User exists in Room, checking role")
                        // Imprimir el valor de los roles
                        Log.e("Login", "Local user role: ${localUser.rol}")
                        Log.e("Login", "Firestore user role: ${user.rol}")
                        if (localUser.rol != user.rol) {
                            Log.e("Login", "Updating user role in Room: ${user.rol}")
                            offlineUsersRepository.updateUser(user)
                        }
                    } else {
                        Log.e("Login", "User not found in Room, inserting new user")
                        offlineUsersRepository.insertUser(user)
                    }

                    _loginResult.value = LoginResult(success = true)
                } else {
                    _loginResult.value = LoginResult(success = false, errorMessage = "Invalid credentials")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult(success = false, errorMessage = "An error occurred: ${e.message}")
            }
        }
    }

    fun setLoginResult(result: LoginResult?) {
        _loginResult.value = result
    }

    // Método para limpiar el resultado del login (lo usamos al cerrar sesión)
    fun logout() {
        _loginResult.value = null // Reseteamos el estado de loginResult
    }

    private suspend fun authenticateUser(email: String, hashedPassword: String): User? {
        return try {
            val db = FirebaseFirestore.getInstance()
            val documentSnapshot = db.collection("users").document(email).get().await()

            if (documentSnapshot.exists()) {
                val storedHashedPassword = documentSnapshot.getString("password")
                Log.e("Login", "Password from Firestore: $storedHashedPassword") // Verificar password de Firestore

                if (storedHashedPassword == hashedPassword) {
                    val userId = documentSnapshot.getString("id")
                        ?: throw IllegalArgumentException("User ID is missing in Firestore")

                    val rol = documentSnapshot.getString("rol") ?: "User"
                    Log.e("Login", "User role from Firestore: $rol") // Verificar rol de Firestore

                    User(
                        id = userId,
                        name = documentSnapshot.getString("name") ?: "",
                        email = email,
                        password = storedHashedPassword ?: "",
                        rol = rol
                    )
                } else null
            } else {
                Log.e("Login", "User not found in Firestore") // Log si el documento no existe
                null
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error authenticating user", e)
            null
        }
    }
}