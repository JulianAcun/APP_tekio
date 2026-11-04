package com.tekio.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tekio.app.data.EventoHistorial
import com.tekio.app.data.TicketLocalStore
import com.tekio.app.data.TipoEvento
import com.tekio.app.data.formatearFecha
import com.tekio.app.ui.theme.*

private fun iconoDe(tipo: TipoEvento): ImageVector = when (tipo) {
    TipoEvento.CREADO -> Icons.Filled.ConfirmationNumber
    TipoEvento.NOTA -> Icons.Filled.Edit
    TipoEvento.EQUIPO -> Icons.Filled.Laptop
    TipoEvento.REPUESTO -> Icons.Filled.Build
    TipoEvento.ESTADO -> Icons.Filled.Sync
    TipoEvento.COMPLETADO -> Icons.Filled.CheckCircle
    TipoEvento.REABIERTO -> Icons.Filled.Refresh
}

private fun colorDe(tipo: TipoEvento): Color = when (tipo) {
    TipoEvento.COMPLETADO -> Color(0xFF2E9E5B)
    TipoEvento.ESTADO -> TekioAmber
    TipoEvento.NOTA, TipoEvento.REPUESTO, TipoEvento.EQUIPO -> Color(0xFF5B9BFF)
    else -> TekioRed
}

@Composable
fun HistorialScreen(viewModel: TicketViewModel, onTicketClick: (Int) -> Unit) {
    // Misma instancia del ViewModel: se usa solo para consultar nombres de cliente
    val tickets by viewModel.tickets.collectAsState()
    // Más reciente arriba
    val eventos = TicketLocalStore.eventos.reversed()

    Column(Modifier.fillMaxSize().background(TekioBackground)) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text("Historial", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Registro de tickets y cambios realizados", color = TekioMuted, fontSize = 14.sp)
        }

        if (eventos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no hay movimientos", color = TekioMuted)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(eventos, key = { it.id }) { ev ->
                    val cliente = tickets.find { it.id == ev.ticketId }?.cliente ?: ""
                    EventoItem(ev, cliente) { onTicketClick(ev.ticketId) }
                }
            }
        }
    }
}

@Composable
private fun EventoItem(ev: EventoHistorial, cliente: String, onClick: (() -> Unit)?) {
    val color = colorDe(ev.tipo)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(TekioSurface)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) { Icon(iconoDe(ev.tipo), contentDescription = null, tint = color, modifier = Modifier.size(22.dp)) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row {
                Text(ev.tipo.label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                Text(formatearFecha(ev.timestamp), color = TekioMuted, fontSize = 12.sp)
            }
            Text("#%04d  %s".format(ev.ticketId, cliente), color = TekioRed, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(2.dp))
            Text(ev.detalle, color = Color(0xFFD0D3D8), fontSize = 14.sp)
        }
    }
}


/**
 * Historial filtrado de UN ticket (se abre desde los 3 puntos del detalle → "Ver historial").
 * Filtra TicketLocalStore.eventos por ticketId.
 */
@Composable
fun HistorialTicketScreen(viewModel: TicketViewModel, ticketId: Int, onBack: () -> Unit) {
    val tickets by viewModel.tickets.collectAsState()
    val ticket = tickets.find { it.id == ticketId }
    val eventos = TicketLocalStore.eventos.filter { it.ticketId == ticketId }.reversed()

    Column(Modifier.fillMaxSize().background(TekioBackground)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Column(Modifier.weight(1f)) {
                Text("Historial del ticket", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "#%04d  %s".format(ticketId, ticket?.cliente ?: ""),
                    color = TekioRed, fontSize = 14.sp, fontWeight = FontWeight.Medium
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF22262C)))

        if (eventos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Este ticket aún no tiene cambios registrados", color = TekioMuted)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(eventos, key = { it.id }) { ev ->
                    EventoItem(ev, ticket?.cliente ?: "", null)
                }
            }
        }
    }
}
