package com.example.ghibliexplorer.ui.screens.views

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ghibliexplorer.ui.screens.viewmodel.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
  navController: NavController,
  registerViewModel: RegisterViewModel,
  modifier: Modifier = Modifier
) {
  val registrationResult by registerViewModel.registrationResult

  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var rePassword by remember { mutableStateOf("") }

  // Detectar cuando el registro sea exitoso y navegar a la pantalla de Login
  LaunchedEffect(registrationResult) {
    registrationResult?.let {
      if (it.success) {
        navController.navigate("Login") {
          popUpTo("Register") { inclusive = true }
        }
      }
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Color.White),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    item {
      Text(
        text = "GhibliExplorer",
        fontSize = 30.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Create an account",
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(16.dp))
    }

    item {
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Name") },
        singleLine = true
      )
      Spacer(modifier = Modifier.height(8.dp))
      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
      )
      Spacer(modifier = Modifier.height(8.dp))
      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
      )
      Spacer(modifier = Modifier.height(8.dp))
      OutlinedTextField(
        value = rePassword,
        onValueChange = { rePassword = it },
        label = { Text("Re-Password") },
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
      )
      Spacer(modifier = Modifier.height(16.dp))
    }

    item {
      // Mostrar mensaje de error si existe alguno
      registrationResult?.let {
        if (!it.success) {
          Text(text = it.errorMessage ?: "Unknown error", color = Color.Red)
          Spacer(modifier = Modifier.height(8.dp))
        }
      }

      Button(
        onClick = {
          // Llamamos a validateAndRegisterUser en lugar de registerUser directamente
          registerViewModel.validateAndRegisterUser(name, email, password, rePassword)
        },
        modifier = Modifier.width(200.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688))
      ) {
        Text("Create Account")
      }

      Spacer(modifier = Modifier.height(8.dp))

      TextButton(
        onClick = { navController.popBackStack() },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Back")
      }
    }
  }
}