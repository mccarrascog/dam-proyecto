package com.example.ghibliexplorer.utils

import android.content.Context

// Función para guardar el correo electrónico en SharedPreferences
fun saveUserEmail(context: Context, email: String) {
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("user_email", email).apply()
}

// Función para obtener el correo electrónico desde SharedPreferences
fun getUserEmail(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("user_email", null)  // Devuelve null si no se encuentra
}