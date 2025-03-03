package com.example.ghibliexplorer.data

import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

data class Review(
    val id: String = UUID.randomUUID().toString(),  // Generar ID único aleatorio
    val filmId: String,
    val author: String,
    val rating: Float,
    val comment: String,
    val date: Date
)