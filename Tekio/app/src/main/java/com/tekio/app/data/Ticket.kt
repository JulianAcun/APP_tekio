package com.tekio.app.data

import androidx.compose.ui.graphics.Color
import com.tekio.app.ui.theme.TekioAmber
import com.tekio.app.ui.theme.TekioGrayChip
import com.tekio.app.ui.theme.TekioRed

// 1. Conservamos las etiquetas de estado por si la pantalla de Detalle las necesita
enum class TicketStatus(val label: String, val color: Color, val textColor: Color) {
    NUEVO("Nuevo", TekioRed, Color.White),
    EN_PROCESO("En proceso", TekioAmber, Color.Black),
    EN_ESPERA("En espera", TekioGrayChip, Color.White)
}

enum class DeviceType(val label: String) {
    PENDIENTE("Pendiente de clasificación"),
    LAPTOP("Laptop"),
    PORTATIL("Portátil"),
    PC("PC de escritorio"),
    MONITOR("Monitor"),
    IMPRESORA("Impresora"),
    OTRO("Otro")
}

// 2. EL CAMBIO CRÍTICO: Ajustamos el modelo exactamente a lo que envía PHP/MySQL
data class Ticket(
    val id: Int,
    val cliente: String,
    val equipo: String,
    val falla: String
)

// Nota: Eliminamos todo el bloque 'object SampleData' porque ya
// tenemos un backend real con Retrofit.