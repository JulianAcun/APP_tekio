package com.tekio.app.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tekio.app.data.Ticket
import com.tekio.app.ui.theme.*

@Composable
fun TicketListScreen(
    viewModel: TicketViewModel,
    onTicketClick: (Int) -> Unit,
    onAddTicket: () -> Unit = {}
) {
    val tickets by viewModel.tickets.collectAsState()

    val mensajeError by viewModel.mensajeError.collectAsState()
    val contexto = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(mensajeError) {
        if (mensajeError != null) {
            android.widget.Toast.makeText(contexto, mensajeError, android.widget.Toast.LENGTH_LONG).show()
            viewModel.limpiarError()
        }
    }
// Variables de estado para dar vida a la interfaz
    var mostrarActivos by remember { mutableStateOf(true) }
    var pantallaActual by remember { mutableStateOf("Tickets") }
    var textoBusqueda by remember { mutableStateOf("") }
    var mostrarBuscador by remember { mutableStateOf(false) }

// Control del formulario emergente
    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var nuevoCliente by remember { mutableStateOf("") }
    var nuevoEquipo by remember { mutableStateOf("") }
    var nuevaFalla by remember { mutableStateOf("") }

// ÚNICA lógica de filtrado dinámico (Pestañas + Buscador)
    val ticketsFiltrados = tickets.filter { ticket ->
        val coincideEstado = if (mostrarActivos) ticket.estado != "cerrado" else ticket.estado == "cerrado"
        val coincideTexto = ticket.cliente.contains(textoBusqueda, ignoreCase = true) || ticket.falla.contains(textoBusqueda, ignoreCase = true)
        coincideEstado && coincideTexto
    }

// Cuenta cuántos activos hay en total para el globo rojo
    val totalActivos = tickets.count { it.estado != "cerrado" }

    Scaffold(
        containerColor = TekioBackground,
        bottomBar = {
            TekioBottomBar(
                pantallaActual = pantallaActual,
                onScreenSelected = { pantallaActual = it }
            )
        },
        floatingActionButton = {
            if (pantallaActual == "Tickets") {
                FloatingActionButton(
                    onClick = { mostrarDialogoCrear = true }, // ESTA LÍNEA CAMBIA
                    shape = CircleShape,
                    containerColor = TekioRed,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Nuevo ticket")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (pantallaActual) {
                "Tickets" -> {
                    Column(Modifier.fillMaxSize()) {
                        // Aquí inyectamos el nuevo encabezado con buscador
                        TekioHeader(
                            mostrarBuscador = mostrarBuscador,
                            textoBusqueda = textoBusqueda,
                            onBuscadorToggle = { mostrarBuscador = !mostrarBuscador },
                            onTextoCambio = { textoBusqueda = it }
                        )

                        TabsRow(
                            cantidadActivos = totalActivos,
                            mostrarActivos = mostrarActivos,
                            onTabSelected = { mostrarActivos = it }
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(ticketsFiltrados, key = { it.id }) { ticket ->
                                TicketCard(ticket) { onTicketClick(ticket.id) }
                            }
                        }
                    }
                }
                "Historial" -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Pantalla de Historial", color = Color.White)
                    }
                }
                "Perfil" -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Pantalla de Perfil Técnico", color = Color.White)
                    }
                }
            }
        }
        // Cuadro de diálogo para crear nuevo ticket
        if (mostrarDialogoCrear) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoCrear = false },
                containerColor = TekioSurface,
                title = { Text("Nuevo Ticket", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = nuevoCliente,
                            onValueChange = { nuevoCliente = it },
                            label = { Text("Nombre del Cliente", color = TekioMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TekioRed, unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = nuevoEquipo,
                            onValueChange = { nuevoEquipo = it },
                            label = { Text("Equipo (Ej. Laptop, Impresora)", color = TekioMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TekioRed, unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = nuevaFalla,
                            onValueChange = { nuevaFalla = it },
                            label = { Text("Falla Reportada", color = TekioMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TekioRed, unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().height(100.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nuevoCliente.isNotBlank() && nuevaFalla.isNotBlank()) {
                                viewModel.crearTicketEnMySQL(nuevoCliente, nuevoEquipo, nuevaFalla)
                                mostrarDialogoCrear = false
                                nuevoCliente = ""
                                nuevoEquipo = ""
                                nuevaFalla = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TekioRed)
                    ) {
                        Text("Crear Ticket", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            mostrarDialogoCrear = false
                            nuevoCliente = ""
                            nuevoEquipo = ""
                            nuevaFalla = ""
                        }
                    ) {
                        Text("Cancelar", color = TekioMuted)
                    }
                }
            )
        }
    }
}

@Composable
private fun TekioHeader(
    mostrarBuscador: Boolean,
    textoBusqueda: String,
    onBuscadorToggle: () -> Unit,
    onTextoCambio: (String) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(50.dp),
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
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                )
            )
            IconButton(onClick = {
                onTextoCambio("")
                onBuscadorToggle()
            }) {
                Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = Color.White)
            }
        } else {
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
            IconButton(onClick = onBuscadorToggle) {
                Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Color.White)
            }
            IconButton(onClick = { /* TODO: Implementar menú opciones */ }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Más", tint = Color.White)
            }
        }
    }
}

@Composable
private fun TabsRow(
    cantidadActivos: Int,
    mostrarActivos: Boolean,
    onTabSelected: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(46.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(TekioSurfaceVariant)
    ) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(if (mostrarActivos) TekioRed else Color.Transparent)
                .clickable { onTabSelected(true) },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Tickets Activos",
                    color = if (mostrarActivos) Color.White else TekioMuted,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (mostrarActivos) Color.Black.copy(alpha = 0.6f) else TekioSurface),
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
                .background(if (!mostrarActivos) TekioRed else Color.Transparent)
                .clickable { onTabSelected(false) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Completados",
                color = if (!mostrarActivos) Color.White else TekioMuted,
                fontWeight = FontWeight.SemiBold
            )
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
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(TekioRed)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    if (ticket.estado == "abierto") "Nuevo" else "Ok",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun TekioBottomBar(
    pantallaActual: String,
    onScreenSelected: (String) -> Unit
) {
    NavigationBar(containerColor = TekioSurface) {
        val colors = NavigationBarItemDefaults.colors(
            selectedIconColor = TekioRed,
            selectedTextColor = TekioRed,
            unselectedIconColor = TekioMuted,
            unselectedTextColor = TekioMuted,
            indicatorColor = Color.Transparent
        )
        NavigationBarItem(
            selected = pantallaActual == "Tickets",
            onClick = { onScreenSelected("Tickets") },
            colors = colors,
            icon = { Icon(Icons.Filled.ConfirmationNumber, contentDescription = null) },
            label = { Text("Tickets") }
        )
        NavigationBarItem(
            selected = pantallaActual == "Historial",
            onClick = { onScreenSelected("Historial") },
            colors = colors,
            icon = { Icon(Icons.Filled.History, contentDescription = null) },
            label = { Text("Historial") }
        )
        NavigationBarItem(
            selected = pantallaActual == "Perfil",
            onClick = { onScreenSelected("Perfil") },
            colors = colors,
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text("Perfil") }
        )
    }
}