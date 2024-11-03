package com.example.ghibliexplorer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Entity data class represents a single row in the database.
 */

@Entity(tableName = "favouriteFilms")
@Serializable
data class Film(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val title: String?,
    @SerialName(value = "original_title")
    val originalTitle: String?,
    @SerialName(value = "original_title_romanised")
    val originalTitleRomanised: String?,
    @SerialName(value = "release_date")
    val releaseDate: String?,
    @SerialName(value = "running_time")
    val runningTime: Int?,
    @SerialName(value = "image")
    var imageLink: String?,
    val description: String?,
    val director: String?,
    val producer: String?
)
