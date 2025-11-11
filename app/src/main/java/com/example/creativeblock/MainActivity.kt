package com.example.creativeblock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.creativeblock.data.AppDatabase
import com.example.creativeblock.model.IdeaRepository
import com.example.creativeblock.navigation.AppNavigation
import com.example.creativeblock.ui.theme.CreativeblockTheme
import com.example.creativeblock.viewmodel.IdeaViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // 🆕 Estado que controla si el tema está en modo oscuro o no
            var isDarkTheme by remember { mutableStateOf(false) }

            // Aplica el tema dinámicamente según el estado
            CreativeblockTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Inicializa la base de datos y repository
                    val database = AppDatabase.getDatabase(this)
                    val repository = IdeaRepository(database.ideaDao())

                    // Crea el ViewModel usando viewModel() de Compose
                    val viewModel: IdeaViewModel = viewModel(
                        factory = IdeaViewModelFactory(repository)
                    )

                    // Controlador de navegación
                    val navController = rememberNavController()

                    // 🚀 Navegación de la app, con soporte para alternar tema
                    AppNavigation(
                        navController = navController,
                        viewModel = viewModel,
                        onToggleTheme = { isDarkTheme = !isDarkTheme }, // 🔦 alterna el tema
                        isDarkTheme = isDarkTheme // 🔦 pasa el estado actual
                    )
                }
            }
        }
    }
}

// Factory simplificada (sin cambios)
class IdeaViewModelFactory(
    private val repository: IdeaRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return IdeaViewModel(repository) as T
    }
}
