package com.example.ghibliexplorer.ui.screens.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ghibliexplorer.ui.screens.viewmodel.LoginViewModel
import com.example.ghibliexplorer.utils.saveUserEmail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  onRegisterButtonClicked: () -> Unit,
  navController: NavController,
  loginViewModel: LoginViewModel,
  onLoginSuccess: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  val loginResult by loginViewModel.loginResult.collectAsState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color.White)
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "GhibliExplorer",
      fontSize = 24.sp,
      textAlign = TextAlign.Center,
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Mostrar mensaje de error si existe
    loginResult?.errorMessage?.let {
      Text(text = it, color = Color.Red, textAlign = TextAlign.Center)
      Spacer(modifier = Modifier.height(8.dp))
    }

    OutlinedTextField(
      value = email,
      onValueChange = { email = it },
      label = { Text("Email") },
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = password,
      onValueChange = { password = it },
      label = { Text("Password") },
      visualTransformation = PasswordVisualTransformation(),
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))

    Button(
      onClick = {
        when {
          email.isBlank() -> {
            loginViewModel.setLoginResult(LoginViewModel.LoginResult(false, "Email field is empty"))
            Log.d("LoginScreen", "Email field is empty")
          }
          password.isBlank() -> {
            loginViewModel.setLoginResult(LoginViewModel.LoginResult(false, "Password field is empty"))
            Log.d("LoginScreen", "Password field is empty")
          }
          else -> {
            loginViewModel.setLoginResult(null) // Limpiar errores anteriores
            saveUserEmail(context, email)
            loginViewModel.login(email, password)
          }
        }
      },
      modifier = Modifier.fillMaxWidth(),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688))
    ) {
      Text("Login")
    }

    Spacer(modifier = Modifier.height(8.dp))

    TextButton(
      onClick = {
        onRegisterButtonClicked()
        Log.d("LoginScreen", "Navigate to Register Screen")
      },
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Create An Account")
    }
  }

  // Navegación automática si el login es exitoso
  LaunchedEffect(loginResult) {
    loginResult?.let { result ->
      if (result.success) {
        onLoginSuccess()
        navController.navigate("Start") {
          popUpTo("Login") { inclusive = true }
        }
      }
    }
  }
}