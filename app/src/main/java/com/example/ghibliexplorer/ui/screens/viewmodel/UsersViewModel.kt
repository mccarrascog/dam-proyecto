package com.example.ghibliexplorer.ui.screens.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ghibliexplorer.GhibliExplorerApplication
import com.example.ghibliexplorer.data.User
import com.example.ghibliexplorer.data.offline.OfflineUsersRepository
import com.example.ghibliexplorer.data.online.OnlineUsersRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UsersViewModel(
    private val onlineUsersRepository: OnlineUsersRepository,
    private val offlineUsersRepository: OfflineUsersRepository,
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users

    init {
        // Obtener los usuarios cuando se inicializa el ViewModel
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            try {
                // Obtener usuarios desde Firestore
                val userList = getUsersFromFirestore()
                _users.value = userList

                userList.forEach { user ->
                    val existingUser = offlineUsersRepository.getUserByEmail(user.email)
                    if (existingUser == null) {
                        offlineUsersRepository.insertUser(user)
                    } else {
                        offlineUsersRepository.updateUser(user)
                    }
                }
            } catch (e: Exception) {
                Log.e("UsersViewModel", "Error al obtener usuarios: ${e.message}")
            }
        }
    }

    // Función para obtener los usuarios desde Firestore
    private suspend fun getUsersFromFirestore(): List<User> {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")
        val snapshot = usersCollection.get().await()

        return snapshot.documents.mapNotNull { document ->
            // El 'id' de la base de datos se generará aleatoriamente (UUID)
            val email = document.id // Firestore usa el ID del documento como email
            val user = document.toObject(User::class.java)

            // Si el documento tiene los campos esperados, lo devolvemos con el email como ID
            user?.copy(email = email, id = UUID.randomUUID().toString())
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return offlineUsersRepository.getUserByEmail(email)
    }

    suspend fun insertUser(user: User) {
        offlineUsersRepository.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        offlineUsersRepository.updateUser(user)
    }

    suspend fun getUserByEmailOnline(email: String): User? {
        return onlineUsersRepository.getUserByEmailOnline(email)
    }

    suspend fun getUsers(): List<User> {
        return onlineUsersRepository.getUsers()
    }
    suspend fun getUserRole(userEmail: String): String? {
        return onlineUsersRepository.getUserRole(userEmail)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Obtener la instancia de la aplicación
                val application = (this[APPLICATION_KEY] as GhibliExplorerApplication)

                // Obtener el repositorio de películas online
                val onlineUsersRepository = application.container.onlineUsersRepository

                // Obtener el repositorio de películas offline
                val offlineUsersRepository = application.offlineAppContainer.OfflineUsersRepository

                UsersViewModel(
                    onlineUsersRepository = onlineUsersRepository,
                    offlineUsersRepository = offlineUsersRepository
                )
            }
        }
    }
}
