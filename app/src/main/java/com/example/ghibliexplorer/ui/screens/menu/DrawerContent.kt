package com.example.ghibliexplorer.ui.screens.menu

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ghibliexplorer.ui.screens.viewmodel.LoginViewModel
import com.example.ghibliexplorer.ui.screens.viewmodel.UsersViewModel
import com.example.ghibliexplorer.utils.getUserEmail

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DrawerContent(
    navController: NavController,
    onClose: () -> Unit,
    topBarHeight: Dp,
    loginViewModel: LoginViewModel,
    usersViewModel: UsersViewModel
) {
    val context = LocalContext.current
    val userEmail = getUserEmail(context).orEmpty()
    val userRole = remember { mutableStateOf<String?>(null) }
    val loginResult by loginViewModel.loginResult.collectAsState()
    val userName = remember { mutableStateOf<String?>(null) }

    // LaunchedEffect para obtener los datos del usuario cuando el email no es vacío
    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            val user = usersViewModel.getUserByEmail(userEmail)
            userName.value = user?.name
            userRole.value = user?.rol
        }
    }

    // Solo mostrar el Drawer si el usuario está autenticado
    if (loginResult?.success == true) {
        Surface(
            modifier = Modifier
                .width(280.dp)
                .heightIn(min = 0.dp, max = Dp.Unspecified)
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = topBarHeight),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                bottomStart = 16.dp
            ),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                // Título "Menú"
                userName.value?.let {
                    Text(
                        text = "Welcome, $it!",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Text(
                    text = "GhibliExplorer",
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column {
                    DrawerItem("Home", Icons.Default.Home) {
                        navController.navigate("Start")
                        onClose()
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    DrawerItem("Movies", Icons.Default.List) {
                        navController.navigate("Home")
                        onClose()
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    DrawerItem("Favs", Icons.Default.FavoriteBorder) {
                        navController.navigate("Favourites")
                        onClose()
                    }

                    DrawerItem("Your Reviews", Icons.Default.MailOutline) {
                        navController.navigate("UserReviews") // Navegar a la pantalla de reseñas del usuario
                        onClose()
                    }

                    // Mostrar la opción de "Administrar" solo si el rol es "Admin"
                    userRole.value?.takeIf { it == "Admin" }?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        DrawerItem("Admin", Icons.Default.Settings) {
                            navController.navigate("Administration") // Navegar a la pantalla de administración
                            onClose()
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                DrawerItem("Logout", Icons.Default.ExitToApp) {
                    Log.e("Logout", "Botón de cerrar sesión pulsado")

                    // Limpiar los datos del usuario almacenados en el dispositivo
                    clearUserData(context)
                    Log.e("Logout", "Datos del usuario eliminados")

                    loginViewModel.logout()

                    navController.navigate("Login") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                    Log.e("Logout", "Navegando a Login")

                    onClose()
                }
            }
        }
    }
}

// Función para borrar los datos del usuario (limpiar SharedPreferences)
fun clearUserData(context: Context) {
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove("user_email")
    editor.apply()
}