package com.tekio.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tekio.app.data.TicketLocalStore
import com.tekio.app.ui.HistorialScreen
import com.tekio.app.ui.HistorialTicketScreen
import com.tekio.app.ui.LoginScreen
import com.tekio.app.ui.PerfilScreen
import com.tekio.app.ui.Rutas
import com.tekio.app.ui.TekioBottomBar
import com.tekio.app.ui.TicketDetailScreen
import com.tekio.app.ui.TicketListScreen
import com.tekio.app.ui.TicketViewModel
import com.tekio.app.ui.rutasConBarraInferior
import com.tekio.app.ui.theme.TekioBackground
import com.tekio.app.ui.theme.TekioTheme

class MainActivity : ComponentActivity() {
    // El cerebro de la app: una sola instancia compartida por TODAS las pantallas
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

/** Cambia de pestaña inferior conservando el estado de cada pantalla. */
private fun NavHostController.irAPestana(ruta: String) {
    navigate(ruta) {
        popUpTo(Rutas.TICKETS) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavigation(viewModel: TicketViewModel) {
    val navController = rememberNavController()
    var nombreTecnicoActivo by rememberSaveable { mutableStateOf("") }

    // Cada vez que llegan tickets nuevos desde MySQL se anotan en el historial
    val tickets by viewModel.tickets.collectAsState()
    LaunchedEffect(tickets) { TicketLocalStore.sincronizar(tickets) }

    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    val cerrarSesion = {
        nombreTecnicoActivo = ""
        navController.navigate(Rutas.LOGIN) { popUpTo(0) { inclusive = true } }
    }

    Scaffold(
        containerColor = TekioBackground,
        bottomBar = {
            // La barra solo aparece en Tickets / Historial / Perfil
            if (rutaActual in rutasConBarraInferior) {
                TekioBottomBar(rutaActual = rutaActual, onNavigate = { navController.irAPestana(it) })
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.LOGIN,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rutas.LOGIN) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { nombre ->
                        nombreTecnicoActivo = nombre
                        navController.navigate(Rutas.TICKETS) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                    }
                )
            }

            composable(Rutas.TICKETS) {
                TicketListScreen(
                    viewModel = viewModel,
                    onTicketClick = { id -> navController.navigate(Rutas.detalle(id)) },
                    onLogout = cerrarSesion
                )
            }

            composable(Rutas.HISTORIAL) {
                HistorialScreen(
                    viewModel = viewModel,
                    onTicketClick = { id -> navController.navigate(Rutas.detalle(id)) }
                )
            }

            composable(Rutas.PERFIL) {
                PerfilScreen(
                    viewModel = viewModel,
                    nombreTecnico = nombreTecnicoActivo,
                    onLogout = cerrarSesion
                )
            }

            // Historial filtrado: solo los cambios del ticket abierto
            composable(Rutas.HISTORIAL_TICKET) { backStackEntry ->
                val ticketId = backStackEntry.arguments?.getString("ticketId")?.toIntOrNull()
                if (ticketId != null) {
                    HistorialTicketScreen(
                        viewModel = viewModel,
                        ticketId = ticketId,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Rutas.DETALLE) { backStackEntry ->
                val ticketId = backStackEntry.arguments?.getString("ticketId")?.toIntOrNull()
                if (ticketId != null) {
                    TicketDetailScreen(
                        viewModel = viewModel,
                        ticketId = ticketId,
                        onBack = { navController.popBackStack() },
                        onVerHistorial = { navController.navigate(Rutas.historialTicket(ticketId)) }
                    )
                }
            }
        }
    }
}
