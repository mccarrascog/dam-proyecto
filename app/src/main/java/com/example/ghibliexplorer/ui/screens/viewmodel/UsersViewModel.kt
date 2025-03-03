package com.example.ghibliexplorer.ui.screens.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ghibliexplorer.data.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UsersViewModel : ViewModel() {
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
            user?.copy(email = email, id = UUID.randomUUID().toString()) // Asignamos un UUID para id
        }
    }
}
