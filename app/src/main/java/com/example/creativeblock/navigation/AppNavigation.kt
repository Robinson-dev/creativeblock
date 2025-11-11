package com.example.creativeblock.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.creativeblock.view.DetailScreen
import com.example.creativeblock.view.FormScreen
import com.example.creativeblock.view.HomeScreen
import com.example.creativeblock.viewmodel.IdeaViewModel

// Clase que define las rutas de navegacion de la app
// Cada pantalla tiene su propia ruta
sealed class Screen(val route: String) {
    object Home : Screen("home")                           // Pantalla principal
    object Form : Screen("form")                           // Formulario de ideas
    object Edit : Screen("edit/{ideaId}") {                // Editar idea
        fun createRoute(ideaId: Int) = "edit/$ideaId"
    }
    object Detail : Screen("detail/{ideaId}") {            // Detalle de idea
        fun createRoute(ideaId: Int) = "detail/$ideaId"
    }
}

// Configuracion de navegacion de la aplicacion
@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: IdeaViewModel,
    onToggleTheme: () -> Unit,      // 🔦 callback que alterna el tema
    isDarkTheme: Boolean            // 🔦 estado actual del tema
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route // Pantalla inicial
    ) {
        // 🏠 Pantalla principal - lista de ideas
        composable(route = Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToForm = {
                    navController.navigate(Screen.Form.route)
                },
                onNavigateToDetail = { ideaId ->
                    navController.navigate(Screen.Detail.createRoute(ideaId))
                },
                // 🆕 Agregamos los parámetros de tema
                onToggleTheme = onToggleTheme,
                isDarkTheme = isDarkTheme
            )
        }

        // 📝 Pantalla de formulario - crear nueva idea
        composable(route = Screen.Form.route) {
            FormScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                isEditingMode = false // modo creación
            )
        }

        // ✏️ Pantalla para editar una idea existente
        composable(
            route = Screen.Edit.route,
            arguments = listOf(
                navArgument("ideaId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val ideaId = backStackEntry.arguments?.getInt("ideaId") ?: 0

            // Cargar la idea para edición
            LaunchedEffect(ideaId) {
                val idea = viewModel.getIdeaById(ideaId)
                if (idea != null) {
                    viewModel.loadIdeaForEditing(idea)
                }
            }

            FormScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                isEditingMode = true,
                ideaId = ideaId
            )
        }

        // 📄 Pantalla de detalle de una idea
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("ideaId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val ideaId = backStackEntry.arguments?.getInt("ideaId") ?: 0

            DetailScreen(
                ideaId = ideaId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                navController = navController
            )
        }
    }
}
