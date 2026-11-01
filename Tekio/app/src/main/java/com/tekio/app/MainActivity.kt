package com.tekio.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tekio.app.ui.TicketDetailScreen
import com.tekio.app.ui.TicketListScreen
import com.tekio.app.ui.TicketViewModel
import com.tekio.app.ui.theme.TekioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TekioTheme {
                TekioApp()
            }
        }
    }
}

@Composable
fun TekioApp() {
    val navController = rememberNavController()
    // ViewModel compartido entre la lista y el detalle
    val viewModel: TicketViewModel = viewModel()

    NavHost(navController = navController, startDestination = "tickets") {
        composable("tickets") {
            TicketListScreen(
                viewModel = viewModel,
                onTicketClick = { id -> navController.navigate("ticket/$id") }
            )
        }
        composable(
            route = "ticket/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            TicketDetailScreen(
                viewModel = viewModel,
                ticketId = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
