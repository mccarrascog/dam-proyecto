package com.example.ghibliexplorer.ui.screens.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ghibliexplorer.GhibliExplorerApplication
import com.example.ghibliexplorer.data.User
import com.example.ghibliexplorer.data.offline.OfflineUsersRepository
import com.example.ghibliexplorer.utils.SHA.generate512
import kotlinx.coroutines.launch

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class RegisterViewModel(
    private val offlineUsersRepository: OfflineUsersRepository
) : ViewModel() {

    // Estado que mantiene el resultado del registro
    private val _registrationResult = mutableStateOf<Result?>(null)
    val registrationResult: State<Result?> get() = _registrationResult

    // Clase para representar el resultado
    data class Result(val success: Boolean, val errorMessage: String? = null)

    fun validateAndRegisterUser(name: String, email: String, password: String, rePassword: String) {
        when {
            name.isBlank() -> updateRegistrationResult(success = false, errorMessage = "Name is empty")
            email.isBlank() -> updateRegistrationResult(success = false, errorMessage = "Email is empty")
            password.isEmpty() -> updateRegistrationResult(success = false, errorMessage = "Password is empty")
            password != rePassword -> updateRegistrationResult(success = false, errorMessage = "Passwords do not match")
            else -> {
                // Si los campos son válidos, intentamos registrar
                registerUser(name, email, password)
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val success = registerUserInFirestore(name, email, password)

                if (success) {
                    updateRegistrationResult(success = true)
                } else {
                    updateRegistrationResult(success = false, errorMessage = "Registration failed")
                }
            } catch (e: Exception) {
                updateRegistrationResult(success = false, errorMessage = "An error occurred: ${e.message}")
            }
        }
    }

    private suspend fun registerUserInFirestore(name: String, email: String, password: String): Boolean {
        return try {
            val db = FirebaseFirestore.getInstance()

            // Verificamos si el usuario ya existe en Firestore
            val userSnapshot = db.collection("users").document(email).get().await()
            if (userSnapshot.exists()) {
                return false // El email ya está registrado
            }

            // Generamos un nuevo ID de usuario basado en UUID
            val newId = UUID.randomUUID().toString()

            val user = User(
                id = newId,
                name = name,
                email = email,
                password = generate512(password), // Guardamos la contraseña hasheada
                rol = "User"
            )

            // Guardamos el usuario en Firestore
            db.collection("users").document(email).set(user).await()

            // Guardamos el usuario en Room para acceso offline
            offlineUsersRepository.insertUser(user)

            true
        } catch (e: Exception) {
            Log.e("RegisterError", "Error registering user: ${e.message}", e)
            false
        }
    }

    // Método para actualizar el estado del resultado del registro
    private fun updateRegistrationResult(success: Boolean, errorMessage: String? = null) {
        _registrationResult.value = Result(success, errorMessage)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as GhibliExplorerApplication

                val offlineUsersRepository = application.offlineAppContainer.OfflineUsersRepository

                RegisterViewModel(offlineUsersRepository)
            }
        }
    }
}