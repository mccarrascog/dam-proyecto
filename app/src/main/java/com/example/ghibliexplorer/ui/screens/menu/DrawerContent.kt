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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
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
    if (loginResult?.success == true) { // Si loginResult es exitoso
        Surface(
            modifier = Modifier
                .width(280.dp) // Ajusta el ancho del menú
                .heightIn(min = 0.dp, max = Dp.Unspecified) // Permite que solo ocupe el espacio necesario
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = topBarHeight), // Desplaza el menú debajo del TopAppBar
            shape = RoundedCornerShape(
                topStart = 16.dp,
                bottomStart = 16.dp
            ),
            tonalElevation = 8.dp // Sombra para profundidad
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top // Asegura que los elementos se alineen arriba
            ) {
                // Título "Menú"
                userName.value?.let {
                    Text(
                        text = "Bienvenido, $it!",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(bottom = 8.dp) // Menor espacio para que no se aleje mucho del título
                    )
                }

                Text(
                    text = "GhibliExplorer",
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Opciones de navegación
                Column {
                    DrawerItem("Inicio", Icons.Default.Home) {
                        navController.navigate("Start")
                        onClose()
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    DrawerItem("Movies", Icons.Default.List) {
                        navController.navigate("Home")
                        onClose()
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    DrawerItem("Favs", Icons.Default.Favorite) {
                        navController.navigate("Favourites")
                        onClose()
                    }

                    // Mostrar la opción de "Administrar" solo si el rol es "Admin"
                    userRole.value?.takeIf { it == "Admin" }?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        DrawerItem("Administrar", Icons.Default.Settings) {
                            navController.navigate("Administration") // Navegar a la pantalla de administración
                            onClose()
                        }
                    }
                }

                // Espacio flexible para empujar el botón de logout hacia abajo
                Spacer(modifier = Modifier.weight(1f))

                // Botón de Logout
                Divider(modifier = Modifier.padding(vertical = 8.dp)) // Separador antes de Logout
                DrawerItem("Cerrar sesión", Icons.Default.ExitToApp) {
                    Log.e("Logout", "Botón de cerrar sesión pulsado")

                    // Limpiar los datos del usuario almacenados en el dispositivo
                    clearUserData(context)
                    Log.e("Logout", "Datos del usuario eliminados")

                    loginViewModel.logout()

                    // Navegar al Login
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
    editor.remove("user_email") // Borrar el correo electrónico guardado
    editor.apply()
}