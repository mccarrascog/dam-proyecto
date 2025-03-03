package com.example.ghibliexplorer.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Serializable
@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val email: String,
    val password: String,
    val name: String = "",
    val rol: String = "User",
    val createdAt: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()) // Fecha en formato legible
) {
    constructor() : this("", "", "", "", "", "") // Constructor sin argumentos para Firestore
}

