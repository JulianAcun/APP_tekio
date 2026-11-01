package com.tekio.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tekio.app.data.DeviceType
import com.tekio.app.ui.theme.TekioBackground
import com.tekio.app.ui.theme.TekioMuted
import com.tekio.app.ui.theme.TekioRed
import com.tekio.app.ui.theme.TekioRedDark
import com.tekio.app.ui.theme.TekioSurface

private data class DeviceOption(val type: DeviceType, val icon: ImageVector)

private val deviceOptions = listOf(
    DeviceOption(DeviceType.LAPTOP, Icons.Filled.Laptop),
    DeviceOption(DeviceType.PC, Icons.Filled.Computer),
    DeviceOption(DeviceType.MONITOR, Icons.Filled.Monitor),
    DeviceOption(DeviceType.IMPRESORA, Icons.Filled.Print),
    DeviceOption(DeviceType.OTRO, Icons.Filled.MoreHoriz)
)

@Composable
fun TicketDetailScreen(
    viewModel: TicketViewModel,
    ticketId: Int,
    onBack: () -> Unit
) {
    // 1. Escuchamos los datos reales y buscamos el ticket específico
    val tickets by viewModel.tickets.collectAsState()
    val ticket = tickets.find { it.id == ticketId }

    // 2. Variables de estado temporal para UI (hasta que creemos el endpoint POST)
    var notasInternas by remember { mutableStateOf("") }

    if (ticket == null) {
        onBack()
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(TekioBackground)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text("Detalle del Ticket", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            IconButton(onClick = { }) { Icon(Icons.Filled.MoreVert, contentDescription = "Más", tint = Color.White) }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF22262C)))

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ConfirmationNumber, contentDescription = null, tint = TekioRed, modifier = Modifier.size(36.dp))
                Spacer(Modifier.width(12.dp))
                Text("#%04d".format(ticket.id), color = TekioRed, fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))

                Box(Modifier.clip(RoundedCornerShape(50)).background(TekioRed).padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text("Nuevo", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            InfoRow(Icons.Filled.Person, "Cliente", ticket.cliente)
            InfoRow(Icons.Filled.CalendarToday, "Fecha de creación", "Reciente")

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

            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Filled.PhoneAndroid, contentDescription = null, tint = TekioMuted, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Equipo", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(TekioSurface)
                            .border(1.5.dp, TekioRed, RoundedCornerShape(8.dp)).padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(ticket.equipo, color = Color.White, fontSize = 17.sp, modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                    }
                }
            }

            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("Clasificar equipo", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(TekioSurface).border(1.dp, Color(0xFF2B3036), RoundedCornerShape(8.dp))) {
                        deviceOptions.forEach { option ->
                            val selected = ticket.equipo == option.type.label
                            Row(
                                Modifier.fillMaxWidth()
                                    .then(if (selected) Modifier.border(1.dp, TekioRed, RoundedCornerShape(6.dp)) else Modifier)
                                    .clickable { /* TODO: Fase 3 - Enviar actualización de clasificación a MySQL */ }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(option.icon, contentDescription = null, tint = Color(0xFFD0D3D8), modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(14.dp))
                                Text(option.type.label, color = Color.White, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                if (selected) Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TekioMuted)
                            }
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Filled.Edit, contentDescription = null, tint = TekioMuted, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("Notas internas (opcional)", color = TekioMuted, fontSize = 15.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = notasInternas,
                        onValueChange = { notasInternas = it },
                        placeholder = { Text("Agregar alguna observación...", color = TekioMuted) },
                        modifier = Modifier.fillMaxWidth().height(96.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = TekioRed, unfocusedBorderColor = Color(0xFF2B3036),
                            cursorColor = TekioRed, focusedContainerColor = TekioSurface, unfocusedContainerColor = TekioSurface
                        )
                    )
                    Text("${notasInternas.length}/200", color = TekioMuted, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), textAlign = TextAlign.End)
                }
            }
        }

        Button(
            onClick = {
                /* TODO: Fase 3 - Enviar orden de cierre a MySQL */
                onBack()
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TekioRed, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp).height(52.dp)
        ) {
            Icon(Icons.Filled.Check, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar / Completar Ticket", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
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