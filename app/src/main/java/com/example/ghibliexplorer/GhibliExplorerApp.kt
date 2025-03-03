package com.example.ghibliexplorer

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ghibliexplorer.data.offline.DefaultOfflineAppContainer
import com.example.ghibliexplorer.data.online.DefaultOnlineAppContainer
import com.example.ghibliexplorer.data.online.FirebaseReviewRepository
import com.example.ghibliexplorer.data.online.FirebaseUsersRepository
import com.example.ghibliexplorer.network.FirebaseService
import com.example.ghibliexplorer.ui.screens.AdministrationScreen
import com.example.ghibliexplorer.ui.screens.FavFilmsScreen
import com.example.ghibliexplorer.ui.screens.FilmsScreen
import com.example.ghibliexplorer.ui.screens.menu.DrawerContent
import com.example.ghibliexplorer.ui.screens.viewmodel.FavsViewModel
import com.example.ghibliexplorer.ui.screens.viewmodel.FilmsViewModel
import com.example.ghibliexplorer.ui.screens.viewmodel.LoginViewModel
import com.example.ghibliexplorer.ui.screens.viewmodel.RegisterViewModel
import com.example.ghibliexplorer.ui.screens.viewmodel.ReviewViewModel
import com.example.ghibliexplorer.ui.screens.viewmodel.ReviewViewModelFactory
import com.example.ghibliexplorer.ui.screens.viewmodel.UsersViewModel
import com.example.ghibliexplorer.ui.screens.views.AddReviewDialog
import com.example.ghibliexplorer.ui.screens.views.AdminReviewsScreen
import com.example.ghibliexplorer.ui.screens.views.AdminUsersScreen
import com.example.ghibliexplorer.ui.screens.views.FilmDetailScreen
import com.example.ghibliexplorer.ui.screens.views.LoginScreen
import com.example.ghibliexplorer.ui.screens.views.RegisterScreen
import com.example.ghibliexplorer.ui.screens.views.ReviewScreen
import com.example.ghibliexplorer.ui.screens.views.StartScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

enum class GhibliExplorerScreen(@StringRes val titulo: Int){
    Login(titulo = R.string.login),
    Register(titulo = R.string.register),
    Start(titulo = R.string.app_name),
    Home(titulo = R.string.peliculas),
    FilmDetail(titulo = R.string.detalle_pelicula),
    Favourites(titulo = R.string.favoritas),
    Reviews(titulo = R.string.resenias),
    AddReview(titulo = R.string.aniadir_resenia),
    Administration(titulo = R.string.administracion),
    AdminUsers(titulo = R.string.administrar_users),
    AdminReviews(titulo = R.string.administrar_resenias)
}

/**
 * Composable que muestra la barra superior y muestra el botón Atrás si es posible la navegación hacia atrás
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GhibliExplorerAppBar(
    canNavigateBack: Boolean,
    currentScreen: String,
    navigateUp: () -> Unit,
    isLoggedIn: Boolean, // Estado para saber si el usuario está logueado
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(currentScreen.substringBeforeLast("/")) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            // Mostrar el menú hamburguesa a la izquierda si el usuario está logueado
            if (isLoggedIn) {
                IconButton(onClick = openDrawer) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu"
                    )
                }
            }
        },
        actions = {
            // Mostrar el botón de "volver atrás" a la derecha si es necesario
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GhibliExplorerApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    val context = LocalContext.current

    val firebaseService = remember { FirebaseService() }
    val firebaseUsersRepository = remember { FirebaseUsersRepository(firebaseService) }

    val appContainer = DefaultOfflineAppContainer(context)
    val offlineUsersRepository = appContainer.OfflineUsersRepository
    val offlineFilmsRepository = appContainer.OfflineFilmsRepository

    val loginViewModel: LoginViewModel = remember {
        LoginViewModel(firebaseUsersRepository, offlineUsersRepository)
    }

    val registerViewModel = remember {
        RegisterViewModel(offlineUsersRepository = offlineUsersRepository)  // Asegúrate de pasar el repositorio correcto
    }

    val onlineFilmsRepository =
        (LocalContext.current.applicationContext as GhibliExplorerApplication).container.OnlineFilmsRepository

    val filmsViewModel: FilmsViewModel = remember {
        FilmsViewModel(onlineFilmsRepository, offlineFilmsRepository, offlineUsersRepository)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estado para saber si el usuario está logueado
    val loginResult by loginViewModel.loginResult.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp),
                shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                DrawerContent(
                    navController,
                    onClose = { scope.launch { drawerState.close() } },
                    topBarHeight = 56.dp,
                    loginViewModel)
            }
        }
    ) {
        Scaffold(
            topBar = {
                GhibliExplorerAppBar(
                    canNavigateBack = navController.previousBackStackEntry != null,
                    currentScreen = backStackEntry?.destination?.route
                        ?: GhibliExplorerScreen.Login.name,
                    navigateUp = { navController.navigateUp() },
                    isLoggedIn = loginResult?.success == true,
                    modifier = Modifier.fillMaxWidth(),
                    openDrawer = {
                        if (loginResult?.success == true) {  // Solo abrir el drawer si el usuario está logueado
                            scope.launch { drawerState.open() }
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = GhibliExplorerScreen.Login.name,
            ) {
                composable(route = GhibliExplorerScreen.Login.name) {
                    LoginScreen(
                        onRegisterButtonClicked = { navController.navigate(GhibliExplorerScreen.Register.name) },
                        loginViewModel = loginViewModel,
                        navController = navController,
                        onLoginSuccess = {
                            // Actualizamos el estado de login a "true" cuando se loguea
                            loginViewModel.setLoginResult(LoginViewModel.LoginResult(success = true))
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }

                composable(route = GhibliExplorerScreen.Register.name) {
                    RegisterScreen(
                        registerViewModel = registerViewModel,
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }

                composable(route = GhibliExplorerScreen.Start.name) {
                    StartScreen(
                        onStartButtonClicked = { navController.navigate(GhibliExplorerScreen.Home.name) },
                        onFavouritesButtonClicked = { navController.navigate(GhibliExplorerScreen.Favourites.name) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
                composable(route = GhibliExplorerScreen.Home.name) {
                    FilmsScreen(navController = navController, paddingValues = innerPadding)
                }

                composable(
                    route = "${GhibliExplorerScreen.FilmDetail.name}/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) {
                    val filmId = backStackEntry?.arguments?.getString("id")
                    if (filmId != null) {
                        val filmDetails = runBlocking {
                            DefaultOnlineAppContainer().OnlineFilmsRepository.getFilmById(filmId)
                        }
                        filmDetails?.let { film ->
                            val context = LocalContext.current
                            val favsViewModel: FavsViewModel = viewModel(factory = FavsViewModel.provideFactory(context))
                            LaunchedEffect(Unit) {
                                favsViewModel.checkIsFilmInFavs(film)
                            }

                            val isFilmInFavsState by favsViewModel.isFilmInFavsState.collectAsState(
                                initial = false
                            )

                            FilmDetailScreen(
                                film = film,
                                navController = navController,
                                onAddFavouritesButtonClicked = {
                                    favsViewModel.addFilmToFavourites(film)
                                    navController.navigate(GhibliExplorerScreen.Favourites.name)
                                },
                                onRemoveFromFavsButtonClicked = {
                                    favsViewModel.removeFilmFromFavs(film)
                                    navController.navigate(GhibliExplorerScreen.Favourites.name)
                                },
                                isFilmInFavs = isFilmInFavsState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            )
                        } ?: run {
                            Text("Film details not found")
                        }
                    } else {
                        Text("Invalid film id")
                    }
                }

                composable(route = "${GhibliExplorerScreen.Reviews.name}/{filmId}") { backStackEntry ->
                    val filmId = backStackEntry.arguments?.getString("filmId") ?: return@composable

                    val firebaseReviewRepository = FirebaseReviewRepository(firebaseService)

                    val reviewViewModel: ReviewViewModel = viewModel(factory = ReviewViewModelFactory(firebaseReviewRepository))


                    LaunchedEffect(filmId) {
                        filmsViewModel.getFilmById(filmId)
                        reviewViewModel.getReviewsForFilm(filmId)
                    }

                    val film = filmsViewModel.selectedFilm
                    val reviews by reviewViewModel.reviews.collectAsState()

                    if (film != null) {
                        ReviewScreen(
                            film = film,
                            reviewViewModel = reviewViewModel,
                            navController = navController
                        )
                    } else {
                        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                    }
                }

                composable(route = GhibliExplorerScreen.AddReview.name + "/{filmId}") { backStackEntry ->
                    val filmId = backStackEntry.arguments?.getString("filmId") ?: return@composable

                    val firebaseReviewRepository = FirebaseReviewRepository(firebaseService)

                    val reviewViewModel: ReviewViewModel =
                        remember { ReviewViewModel(firebaseReviewRepository) }

                    LaunchedEffect(filmId) {
                        filmsViewModel.getFilmById(filmId)
                    }

                    val film = filmsViewModel.selectedFilm

                    if (film != null) {
                        AddReviewDialog(
                            film = film,
                            onDismiss = { navController.popBackStack() },
                            viewModel = reviewViewModel
                        )
                    } else {
                        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                    }
                }

                composable(route = GhibliExplorerScreen.Favourites.name) {
                    FavFilmsScreen(navController = navController)
                }
                composable("Administration") {
                    AdministrationScreen(navController)
                }
                composable("AdminUsers") {
                    val usersViewModel: UsersViewModel = viewModel()
                    AdminUsersScreen(usersViewModel)
                }
                composable("AdminReviews") {
                    AdminReviewsScreen()
                }
            }
        }
    }
}