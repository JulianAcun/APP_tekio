package com.tekio.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.tekio.app.ui.theme.TekioMuted
import com.tekio.app.ui.theme.TekioRed
import com.tekio.app.ui.theme.TekioSurface

object Rutas {
    const val LOGIN = "login"
    const val TICKETS = "tickets"
    const val HISTORIAL = "historial"
    const val PERFIL = "perfil"
    const val DETALLE = "detalle/{ticketId}"
    const val HISTORIAL_TICKET = "historial/{ticketId}"
    fun historialTicket(id: Int) = "historial/$id"
    fun detalle(id: Int) = "detalle/$id"
}

private data class ItemBarra(val ruta: String, val titulo: String, val icono: ImageVector)

private val itemsBarra = listOf(
    ItemBarra(Rutas.TICKETS, "Tickets", Icons.Filled.ConfirmationNumber),
    ItemBarra(Rutas.HISTORIAL, "Historial", Icons.Filled.History),
    ItemBarra(Rutas.PERFIL, "Perfil", Icons.Filled.Person)
)

val rutasConBarraInferior = itemsBarra.map { it.ruta }

@Composable
fun TekioBottomBar(rutaActual: String?, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = TekioSurface) {
        val colores = NavigationBarItemDefaults.colors(
            selectedIconColor = TekioRed,
            selectedTextColor = TekioRed,
            unselectedIconColor = TekioMuted,
            unselectedTextColor = TekioMuted,
            indicatorColor = Color.Transparent
        )
        itemsBarra.forEach { item ->
            NavigationBarItem(
                selected = rutaActual == item.ruta,
                onClick = { onNavigate(item.ruta) },
                colors = colores,
                icon = { Icon(item.icono, contentDescription = item.titulo) },
                label = { Text(item.titulo) }
            )
        }
    }
}
