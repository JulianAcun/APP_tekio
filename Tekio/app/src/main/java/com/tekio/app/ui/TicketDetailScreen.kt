package com.tekio.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tekio.app.data.*
import com.tekio.app.ui.theme.*

private val clasificaciones = listOf(
    DeviceType.LAPTOP, DeviceType.PC, DeviceType.MONITOR, DeviceType.IMPRESORA, DeviceType.OTRO
)

private fun iconoClasificacion(t: DeviceType): ImageVector = when (t) {
    DeviceType.LAPTOP, DeviceType.PORTATIL -> Icons.Filled.Laptop
    DeviceType.PC -> Icons.Filled.Computer
    DeviceType.MONITOR -> Icons.Filled.Monitor
    DeviceType.IMPRESORA -> Icons.Filled.Print
    DeviceType.OTRO -> Icons.Filled.MoreHoriz
    DeviceType.PENDIENTE -> Icons.Filled.PhoneAndroid
}

@Composable
fun TicketDetailScreen(
    viewModel: TicketViewModel,
    ticketId: Int,
    onBack: () -> Unit,
    onVerHistorial: () -> Unit = {}
) {
    val tickets by viewModel.tickets.collectAsState()
    val ticket = tickets.find { it.id == ticketId }

    if (ticket == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    val completado = ticket.estado == "cerrado"

    // ---- Estado local del formulario (se inicializa una vez por ticket) ----
    val contexto = androidx.compose.ui.platform.LocalContext.current
    var guardando by remember { mutableStateOf(false) }
    val inicial = remember(ticket.id) { EquipoCodec.decodificar(ticket.equipo) }
    var clasificacion by remember(ticket.id) { mutableStateOf(inicial.clasificacion) }
    var marca by remember(ticket.id) { mutableStateOf(inicial.marca) }
    var referencia by remember(ticket.id) { mutableStateOf(inicial.referencia) }
    var notas by remember(ticket.id) { mutableStateOf(TicketLocalStore.notas[ticket.id] ?: "") }
    var repuesto by remember(ticket.id) { mutableStateOf(TicketLocalStore.repuestos[ticket.id] ?: "") }
    var estado by remember(ticket.id) { mutableStateOf(TicketLocalStore.estadoDe(ticket.id)) }
    var menuExpandido by remember { mutableStateOf(false) }

    /** Guarda lo que cambió y lo anota en el historial. */
    fun guardar() {
        // 1. Equipo -> MySQL con el endpoint existente
        val nuevoEquipo = EquipoCodec.codificar(EquipoInfo(clasificacion, marca, referencia))
        if (nuevoEquipo != ticket.equipo) {
            viewModel.actualizarEquipoEnMySQL(ticket.id, nuevoEquipo)
            TicketLocalStore.registrar(ticket.id, TipoEvento.EQUIPO, "${ticket.equipo} → $nuevoEquipo")
        }
        // 2. Notas del técnico
        if (notas.trim() != (TicketLocalStore.notas[ticket.id] ?: "")) {
            TicketLocalStore.notas[ticket.id] = notas.trim()
            if (notas.isNotBlank()) TicketLocalStore.registrar(ticket.id, TipoEvento.NOTA, notas.trim().take(120))
        }
        // 3. Repuesto
        if (repuesto.trim() != (TicketLocalStore.repuestos[ticket.id] ?: "")) {
            TicketLocalStore.repuestos[ticket.id] = repuesto.trim()
            if (repuesto.isNotBlank()) TicketLocalStore.registrar(ticket.id, TipoEvento.REPUESTO, "Repuesto: ${repuesto.trim()}")
        }
        // 4. Estado de trabajo
        val anterior = TicketLocalStore.estadoDe(ticket.id)
        if (estado != anterior) {
            TicketLocalStore.estados[ticket.id] = estado
            TicketLocalStore.registrar(ticket.id, TipoEvento.ESTADO, "${anterior.label} → ${estado.label}")
        }
    }

    Column(Modifier.fillMaxSize().background(TekioBackground)) {
        // ---- Barra superior ----
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text("Detalle del Ticket", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Box {
                IconButton(onClick = { menuExpandido = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Más", tint = Color.White)
                }
                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false },
                    modifier = Modifier.background(TekioSurfaceVariant)
                ) {
                    DropdownMenuItem(
                        text = { Text("Ver historial", color = Color.White) },
                        onClick = { menuExpandido = false; onVerHistorial() }
                    )
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF22262C)))

        // ---- Contenido ----
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ConfirmationNumber, contentDescription = null, tint = TekioRed, modifier = Modifier.size(36.dp))
                Spacer(Modifier.width(12.dp))
                Text("#%04d".format(ticket.id), color = TekioRed, fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
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
            }

            InfoRow(Icons.Filled.Person, "Cliente", ticket.cliente)
            InfoRow(Icons.Filled.CalendarToday, "Fecha de creación", formatearFecha(TicketLocalStore.fechaCreacion(ticket.id)))

            // Falla reportada (solo lectura)
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFF2A0C0F))
                    .border(1.dp, TekioRedDark, RoundedCornerShape(8.dp)).padding(14.dp)
            ) {
                Icon(Icons.Filled.Sms, contentDescription = null, tint = TekioRed, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Falla reportada", color = TekioRed, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(ticket.falla, color = Color(0xFFE0E0E0), fontSize = 16.sp)
                }
            }

            // Equipo: marca / referencia
            SeccionConIcono(Icons.Filled.PhoneAndroid, "Equipo (marca / referencia)") {
                CampoDesplegable(
                    valor = marca.ifBlank { "Seleccionar marca" },
                    opciones = MARCAS_EQUIPO,
                    resaltado = true,
                    onSeleccion = { marca = it; if (it != "Otros") referencia = "" }
                )
                if (marca == "Otros") {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = referencia,
                        onValueChange = { referencia = it },
                        placeholder = { Text("Escribe la referencia del equipo", color = TekioMuted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = camposColores()
                    )
                }
            }

            // Clasificación
            SeccionConIcono(Icons.Filled.Search, "Clasificar equipo") {
                CampoDesplegable(
                    valor = clasificacion.label,
                    opciones = clasificaciones.map { it.label },
                    iconoDe = { etiqueta -> iconoClasificacion(clasificaciones.first { it.label == etiqueta }) },
                    onSeleccion = { etiqueta -> clasificacion = clasificaciones.first { it.label == etiqueta } }
                )
            }

            // Estado de trabajo (solo mientras el ticket está activo)
            if (!completado) {
                SeccionConIcono(Icons.Filled.Sync, "Estado del ticket") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TicketStatus.values().forEach { opcion ->
                            val activo = estado == opcion
                            Box(
                                Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                                    .background(if (activo) opcion.color else TekioSurface)
                                    .border(1.dp, if (activo) opcion.color else Color(0xFF2B3036), RoundedCornerShape(8.dp))
                                    .clickable { estado = opcion }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(opcion.label, color = if (activo) opcion.textColor else TekioMuted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Repuesto
            SeccionConIcono(Icons.Filled.Build, "Repuesto cambiado (opcional)") {
                OutlinedTextField(
                    value = repuesto,
                    onValueChange = { repuesto = it.take(80) },
                    placeholder = { Text("Ej. Batería, disco SSD 512 GB…", color = TekioMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = camposColores()
                )
            }

            // Notas del técnico
            SeccionConIcono(Icons.Filled.Edit, "Notas del técnico (opcional)") {
                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it.take(200) },
                    placeholder = { Text("Agregar alguna observación...", color = TekioMuted) },
                    modifier = Modifier.fillMaxWidth().height(96.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = camposColores()
                )
                Text("${notas.length}/200", color = TekioMuted, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), textAlign = TextAlign.End)
            }
        }

        // ---- Botones inferiores ----
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    guardando = true
                    // Se conecta directo con las variables de tu archivo
                    viewModel.guardarCambiosTicket(
                        id = ticket.id,
                        estado = estado.name.lowercase(), // Envia "nuevo", "en_proceso", etc.
                        marcaEquipo = marca,
                        clasificacionEquipo = clasificacion.label,
                        repuesto = repuesto,
                        notas = notas,
                        onExito = { mensaje ->
                            guardando = false
                            android.widget.Toast.makeText(contexto, mensaje, android.widget.Toast.LENGTH_SHORT).show()
                            guardar() // Ejecuta tu función local para dejar el registro en el historial
                        },
                        onError = { error ->
                            guardando = false
                            android.widget.Toast.makeText(contexto, error, android.widget.Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TekioRed),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TekioRed),
                modifier = Modifier.fillMaxWidth().height(46.dp),
                enabled = !guardando
            ) {
                if (guardando) {
                    CircularProgressIndicator(color = TekioRed, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar cambios", fontWeight = FontWeight.SemiBold)
                }
            }

            if (completado) {
                Button(
                    onClick = {
                        viewModel.reabrirTicketEnMySQL(ticket.id)
                        TicketLocalStore.estados[ticket.id] = TicketStatus.NUEVO
                        TicketLocalStore.registrar(ticket.id, TipoEvento.REABIERTO, "El ticket volvió a la lista de activos")
                        onBack()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C), contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Reabrir Ticket", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Button(
                    onClick = {
                        guardar()
                        viewModel.cerrarTicketEnMySQL(ticket.id)
                        TicketLocalStore.registrar(ticket.id, TipoEvento.COMPLETADO, "Ticket finalizado y movido a Completados")
                        onBack()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TekioRed, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar / Completar Ticket", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun camposColores() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
    focusedBorderColor = TekioRed, unfocusedBorderColor = Color(0xFF2B3036),
    cursorColor = TekioRed, focusedContainerColor = TekioSurface, unfocusedContainerColor = TekioSurface
)

@Composable
private fun SeccionConIcono(icono: ImageVector, titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icono, contentDescription = null, tint = TekioMuted, modifier = Modifier.size(32.dp))
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            contenido()
        }
    }
}

/** Cuadro de selección: al tocarlo despliega las opciones (DropdownMenu nativo). */
@Composable
private fun CampoDesplegable(
    valor: String,
    opciones: List<String>,
    onSeleccion: (String) -> Unit,
    resaltado: Boolean = false,
    iconoDe: ((String) -> ImageVector)? = null
) {
    var expandido by remember { mutableStateOf(false) }
    var ancho by remember { mutableStateOf(0.dp) }
    val densidad = LocalDensity.current

    Box(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth()
                .onSizeChanged { ancho = with(densidad) { it.width.toDp() } }
                .clip(RoundedCornerShape(8.dp)).background(TekioSurface)
                .border(if (resaltado) 1.5.dp else 1.dp, if (resaltado) TekioRed else Color(0xFF2B3036), RoundedCornerShape(8.dp))
                .clickable { expandido = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(valor, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = Color.White)
        }
        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            modifier = Modifier.width(ancho).background(TekioSurfaceVariant)
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion, color = Color.White) },
                    leadingIcon = if (iconoDe != null) {
                        { Icon(iconoDe(opcion), contentDescription = null, tint = Color(0xFFD0D3D8)) }
                    } else null,
                    onClick = { onSeleccion(opcion); expandido = false }
                )
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = TekioMuted, modifier = Modifier.size(32.dp))
        Spacer(Modifier.width(14.dp))
        Column {
            Text(label, color = TekioMuted, fontSize = 14.sp)
            Text(value, color = Color.White, fontSize = 17.sp)
        }
    }
}
