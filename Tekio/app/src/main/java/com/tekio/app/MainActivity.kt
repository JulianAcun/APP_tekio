package com.tekio.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tekio.app.ui.LoginScreen
import com.tekio.app.ui.TicketDetailScreen
import com.tekio.app.ui.TicketListScreen
import com.tekio.app.ui.TicketViewModel
import com.tekio.app.ui.theme.TekioTheme

class MainActivity : ComponentActivity() {
    // Instanciamos el cerebro de la app una sola vez aquí
    private val viewModel: TicketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TekioTheme {
                AppNavigation(viewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: TicketViewModel) {
    val navController = rememberNavController()

    // Guardamos el nombre del técnico activo para inyectarlo luego en el Perfil
    var nombreTecnicoActivo by remember { mutableStateOf("") }

    // Definimos "login" como la primera pantalla a mostrar
    NavHost(navController = navController, startDestination = "login") {

        // 1. PANTALLA DE LOGIN
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { nombre ->
                    nombreTecnicoActivo = nombre
                    // Navega a la lista y elimina el login del historial de retroceso
                    navController.navigate("lista") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }


        // 2. PANTALLA PRINCIPAL (TICKETS / HISTORIAL / PERFIL)
        composable("lista") {
            TicketListScreen(
                viewModel = viewModel,
                nombreTecnico = nombreTecnicoActivo,
                onTicketClick = { ticketId ->
                    navController.navigate("detalle/$ticketId")
                },
                onLogout = { // <-- AÑADE ESTE BLOQUE COMPLETO
                    nombreTecnicoActivo = ""
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true } // Destruye todo el historial de pantallas
                    }
                }
            )
        }

        // 3. PANTALLA DE DETALLES DEL TICKET
        composable("detalle/{ticketId}") { backStackEntry ->
            val ticketIdString = backStackEntry.arguments?.getString("ticketId")
            val ticketId = ticketIdString?.toIntOrNull()

            if (ticketId != null) {
                TicketDetailScreen(
                    viewModel = viewModel,
                    ticketId = ticketId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}