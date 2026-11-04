package com.tekio.app.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tekio.app.data.*
import com.tekio.app.ui.theme.*

/**
 * Pantalla "Tickets". La barra inferior ya NO vive aquí: la orquesta el NavHost en MainActivity.
 * Flujo de datos:  tickets (ViewModel) -> ticketsFiltrados (pestaña) -> ticketsMostrar (lupa) -> LazyColumn
 */
@Composable
fun TicketListScreen(
    viewModel: TicketViewModel,
    onTicketClick: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val tickets by viewModel.tickets.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()
    val contexto = LocalContext.current

    LaunchedEffect(mensajeError) {
        if (mensajeError != null) {
            Toast.makeText(contexto, mensajeError, Toast.LENGTH_LONG).show()
            viewModel.limpiarError()
        }
    }

    // Estado de la interfaz
    var mostrarActivos by remember { mutableStateOf(true) }
    var textoBusqueda by remember { mutableStateOf("") }
    var mostrarBuscador by remember { mutableStateOf(false) }

    // Formulario del botón (+)
    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var nuevoCliente by remember { mutableStateOf("") }
    var nuevoEquipo by remember { mutableStateOf("") }
    var nuevaFalla by remember { mutableStateOf("") }

    // 1er filtro: pestaña (Activos / Completados)
    val ticketsFiltrados = tickets.filter { ticket ->
        if (mostrarActivos) ticket.estado != "cerrado" else ticket.estado == "cerrado"
    }
    // 2do filtro encadenado: buscador (cliente, falla o número)
    val ticketsMostrar = ticketsFiltrados.filter {
        it.cliente.contains(textoBusqueda, ignoreCase = true) ||
                it.falla.contains(textoBusqueda, ignoreCase = true) ||
                "#%04d".format(it.id).contains(textoBusqueda, ignoreCase = true)
    }
    val totalActivos = tickets.count { it.estado != "cerrado" }

    Box(Modifier.fillMaxSize().background(TekioBackground)) {
        Column(Modifier.fillMaxSize()) {
            TekioHeader(
                mostrarBuscador = mostrarBuscador,
                textoBusqueda = textoBusqueda,
                onBuscadorToggle = { mostrarBuscador = !mostrarBuscador },
                onTextoCambio = { textoBusqueda = it },
                onLogout = onLogout
            )
            TabsRow(
                cantidadActivos = totalActivos,
                mostrarActivos = mostrarActivos,
                onTabSelected = { mostrarActivos = it }
            )
            LazyColumn(
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (ticketsMostrar.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                            Text(
                                if (textoBusqueda.isNotBlank()) "Sin resultados para “$textoBusqueda”"
                                else if (mostrarActivos) "No hay tickets activos" else "No hay tickets completados",
                                color = TekioMuted
                            )
                        }
                    }
                }
                items(ticketsMostrar, key = { it.id }) { ticket ->
                    TicketCard(ticket) { onTicketClick(ticket.id) }
                }
            }
        }

        FloatingActionButton(
            onClick = { mostrarDialogoCrear = true },
            shape = CircleShape,
            containerColor = TekioRed,
            contentColor = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Filled.Add, contentDescription = "Nuevo ticket") }
    }

    if (mostrarDialogoCrear) {
        val limpiar = { nuevoCliente = ""; nuevoEquipo = ""; nuevaFalla = "" }
        val colores = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TekioRed, unfocusedBorderColor = Color.Gray,
            focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = TekioRed
        )
        AlertDialog(
            onDismissRequest = { mostrarDialogoCrear = false },
            containerColor = TekioSurface,
            title = { Text("Nuevo Ticket", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = nuevoCliente, onValueChange = { nuevoCliente = it },
                        label = { Text("Nombre del Cliente", color = TekioMuted) },
                        colors = colores, singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nuevoEquipo, onValueChange = { nuevoEquipo = it },
                        label = { Text("Equipo (Ej. Laptop, Impresora)", color = TekioMuted) },
                        colors = colores, singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nuevaFalla, onValueChange = { nuevaFalla = it },
                        label = { Text("Falla Reportada", color = TekioMuted) },
                        colors = colores, modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevoCliente.isNotBlank() && nuevaFalla.isNotBlank()) {
                            val equipo = nuevoEquipo.ifBlank { DeviceType.PENDIENTE.label }
                            viewModel.crearTicketEnMySQL(nuevoCliente, equipo, nuevaFalla)
                            mostrarDialogoCrear = false
                            limpiar()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TekioRed)
                ) { Text("Crear Ticket", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCrear = false; limpiar() }) {
                    Text("Cancelar", color = TekioMuted)
                }
            }
        )
    }
}

@Composable
private fun TekioHeader(
    mostrarBuscador: Boolean,
    textoBusqueda: String,
    onBuscadorToggle: () -> Unit,
    onTextoCambio: (String) -> Unit,
    onLogout: () -> Unit
) {
    var menuExpandido by remember { mutableStateOf(false) }
    val contexto = LocalContext.current

    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp).height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (mostrarBuscador) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = onTextoCambio,
                placeholder = { Text("Buscar cliente o falla...", color = Color.Gray) },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TekioRed, unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = TekioRed
                )
            )
            IconButton(onClick = { onTextoCambio(""); onBuscadorToggle() }) {
                Icon(Icons.Filled.Close, contentDescription = "Cerrar búsqueda", tint = Color.White)
            }
        } else {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(TekioRed),
                contentAlignment = Alignment.Center
            ) { Text("T", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("TEKIO", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text("Gestión de Tickets", color = TekioMuted, fontSize = 14.sp)
            }
            IconButton(onClick = onBuscadorToggle) {
                Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Color.White)
            }
            // Menú de tres puntos (DropdownMenu nativo)
            Box {
                IconButton(onClick = { menuExpandido = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones", tint = Color.White)
                }
                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false },
                    modifier = Modifier.background(TekioSurfaceVariant)
                ) {
                    DropdownMenuItem(
                        text = { Text("Ajustes", color = Color.White) },
                        onClick = {
                            menuExpandido = false
                            Toast.makeText(contexto, "Ajustes: próximamente", Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Acerca de Tekio", color = Color.White) },
                        onClick = {
                            menuExpandido = false
                            Toast.makeText(contexto, "Tekio v1.0", Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Cerrar sesión", color = TekioRed) },
                        onClick = { menuExpandido = false; onLogout() }
                    )
                }
            }
        }
    }
}

@Composable
private fun TabsRow(cantidadActivos: Int, mostrarActivos: Boolean, onTabSelected: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(46.dp)
            .clip(RoundedCornerShape(8.dp)).background(TekioSurfaceVariant)
    ) {
        Box(
            Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(8.dp))
                .background(if (mostrarActivos) TekioRed else Color.Transparent)
                .clickable { onTabSelected(true) },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tickets Activos", color = if (mostrarActivos) Color.White else TekioMuted, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.size(22.dp).clip(CircleShape)
                        .background(if (mostrarActivos) Color.Black.copy(alpha = 0.6f) else TekioSurface),
                    contentAlignment = Alignment.Center
                ) { Text("$cantidadActivos", color = Color.White, fontSize = 12.sp) }
            }
        }
        Box(
            Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(8.dp))
                .background(if (!mostrarActivos) TekioRed else Color.Transparent)
                .clickable { onTabSelected(false) },
            contentAlignment = Alignment.Center
        ) {
            Text("Completados", color = if (!mostrarActivos) Color.White else TekioMuted, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun iconoEquipo(tipo: DeviceType): ImageVector = when (tipo) {
    DeviceType.LAPTOP, DeviceType.PORTATIL -> Icons.Filled.Laptop
    DeviceType.PC -> Icons.Filled.Computer
    DeviceType.MONITOR -> Icons.Filled.Monitor
    DeviceType.IMPRESORA -> Icons.Filled.Print
    DeviceType.OTRO -> Icons.Filled.MoreHoriz
    DeviceType.PENDIENTE -> Icons.Filled.PhoneAndroid
}

@Composable
private fun TicketCard(ticket: Ticket, onClick: () -> Unit) {
    val completado = ticket.estado == "cerrado"
    val estado = TicketLocalStore.estadoDe(ticket.id)
    val acento = if (completado) Color(0xFF2E9E5B) else if (estado == TicketStatus.NUEVO) TekioRed else estado.color
    val info = EquipoCodec.decodificar(ticket.equipo)
    val clasificado = info.clasificacion != DeviceType.PENDIENTE

    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(TekioSurface).clickable(onClick = onClick)
    ) {
        Box(
            Modifier.width(4.dp).height(110.dp)
                .background(Brush.verticalGradient(listOf(acento, acento.copy(alpha = 0.5f))))
        )
        Column(Modifier.weight(1f).padding(start = 12.dp, top = 10.dp, bottom = 10.dp)) {
            Text("#%04d".format(ticket.id), color = TekioRed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(ticket.cliente, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(ticket.falla, color = TekioMuted, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(iconoEquipo(info.clasificacion), contentDescription = null, tint = TekioMuted, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(50))
                        .background(if (clasificado) Color(0xFF3A0F12) else Color(0xFF2A2F36))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        ticket.equipo,
                        color = if (clasificado) Color(0xFFFF5A5F) else Color(0xFFD0D3D8),
                        fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Column(
            Modifier.padding(end = 8.dp, top = 10.dp, bottom = 10.dp).height(90.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                Modifier.clip(RoundedCornerShape(50))
                    .background(if (completado) Color(0xFF1E3A2A) else estado.color)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    if (completado) "Completado" else estado.label,
                    color = if (completado) Color(0xFF5FD18C) else estado.textColor,
                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(formatearHora(TicketLocalStore.fechaCreacion(ticket.id)), color = TekioMuted, fontSize = 13.sp)
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TekioMuted)
            }
        }
    }
}
