package com.tekio.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tekio.app.data.Ticket
import com.tekio.app.ui.theme.TekioBackground
import com.tekio.app.ui.theme.TekioMuted
import com.tekio.app.ui.theme.TekioRed
import com.tekio.app.ui.theme.TekioRedDark
import com.tekio.app.ui.theme.TekioSurface
import com.tekio.app.ui.theme.TekioSurfaceVariant

@Composable
fun TicketListScreen(
    viewModel: TicketViewModel,
    onTicketClick: (Int) -> Unit,
    onAddTicket: () -> Unit = {}
) {
    val tickets by viewModel.tickets.collectAsState()

    Scaffold(
        containerColor = TekioBackground,
        bottomBar = { TekioBottomBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTicket,
                shape = CircleShape,
                containerColor = TekioRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo ticket")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TekioHeader()
            TabsRow(cantidadActivos = tickets.size)
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(tickets, key = { it.id }) { ticket ->
                    // Eliminamos el toIntOrNull() problemático aquí
                    TicketCard(ticket) { onTicketClick(ticket.id) }
                }
            }
        }
    }
}

@Composable
private fun TekioHeader() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(TekioRed),
            contentAlignment = Alignment.Center
        ) {
            Text("T", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("TEKIO", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Text("Gestión de Tickets", color = TekioMuted, fontSize = 14.sp)
        }
        IconButton(onClick = { }) {
            Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Color.White)
        }
        IconButton(onClick = { }) {
            Icon(Icons.Filled.MoreVert, contentDescription = "Más", tint = Color.White)
        }
    }
}

@Composable
private fun TabsRow(cantidadActivos: Int) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(46.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(TekioSurfaceVariant)
    ) {
        val activeSelected = true
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(if (activeSelected) TekioRed else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tickets Activos", color = Color.White, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$cantidadActivos", color = Color.White, fontSize = 12.sp)
                }
            }
        }
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text("Completados", color = TekioMuted, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun TicketCard(ticket: Ticket, onClick: () -> Unit) {
    val accent = TekioRed
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(TekioSurface)
            .clickable(onClick = onClick)
    ) {
        Box(
            Modifier
                .width(4.dp)
                .height(110.dp)
                .background(Brush.verticalGradient(listOf(accent, accent.copy(alpha = 0.5f))))
        )
        Column(
            Modifier
                .weight(1f)
                .padding(start = 12.dp, top = 10.dp, bottom = 10.dp)
        ) {
            // Eliminamos la conversión de ID innecesaria
            Text("#%04d".format(ticket.id), color = TekioRed, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Text(ticket.cliente, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(
                ticket.falla,
                color = TekioMuted,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.PhoneAndroid, contentDescription = null, tint = TekioMuted, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF2A2F36))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        ticket.equipo,
                        color = Color(0xFFD0D3D8),
                        fontSize = 12.sp
                    )
                }
            }
        }
        Column(
            Modifier
                .padding(end = 8.dp, top = 10.dp, bottom = 10.dp)
                .height(90.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Sustituimos el estado problemático por un badge fijo temporal
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(TekioRed)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Nuevo", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun TekioBottomBar() {
    NavigationBar(containerColor = TekioSurface) {
        val colors = NavigationBarItemDefaults.colors(
            selectedIconColor = TekioRed,
            selectedTextColor = TekioRed,
            unselectedIconColor = TekioMuted,
            unselectedTextColor = TekioMuted,
            indicatorColor = Color.Transparent
        )
        NavigationBarItem(
            selected = true, onClick = { }, colors = colors,
            icon = { Icon(Icons.Filled.ConfirmationNumber, contentDescription = null) },
            label = { Text("Tickets") }
        )
        NavigationBarItem(
            selected = false, onClick = { }, colors = colors,
            icon = { Icon(Icons.Filled.History, contentDescription = null) },
            label = { Text("Historial") }
        )
        NavigationBarItem(
            selected = false, onClick = { }, colors = colors,
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text("Perfil") }
        )
    }
}