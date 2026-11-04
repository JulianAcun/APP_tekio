package com.tekio.app.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Datos que el backend todavía NO guarda (historial, notas, estado de trabajo, repuestos).
 * Viven en memoria como "estado de Compose", así que cualquier pantalla que los lea
 * se actualiza sola cuando cambian. No toca ApiService, Ticket ni TicketViewModel.
 */
enum class TipoEvento(val label: String) {
    CREADO("Ticket recibido"),
    NOTA("Nota del técnico"),
    EQUIPO("Equipo actualizado"),
    REPUESTO("Cambio de repuesto"),
    ESTADO("Cambio de estado"),
    COMPLETADO("Ticket completado"),
    REABIERTO("Ticket reabierto")
}

data class EventoHistorial(
    val id: Long,
    val ticketId: Int,
    val tipo: TipoEvento,
    val detalle: String,
    val timestamp: Long
)

object TicketLocalStore {
    val eventos = mutableStateListOf<EventoHistorial>()
    val notas = mutableStateMapOf<Int, String>()
    val repuestos = mutableStateMapOf<Int, String>()
    val estados = mutableStateMapOf<Int, TicketStatus>()
    private val primeraVez = mutableStateMapOf<Int, Long>()
    private var siguienteId = 0L

    fun estadoDe(ticketId: Int): TicketStatus = estados[ticketId] ?: TicketStatus.NUEVO

    /** Fecha en la que la app vio el ticket por primera vez (el backend aún no envía fecha). */
    fun fechaCreacion(ticketId: Int): Long = primeraVez[ticketId] ?: System.currentTimeMillis()

    fun registrar(ticketId: Int, tipo: TipoEvento, detalle: String) {
        siguienteId++
        eventos.add(EventoHistorial(siguienteId, ticketId, tipo, detalle, System.currentTimeMillis()))
    }

    /** Detecta tickets nuevos que llegan desde MySQL y los anota en el historial. */
    fun sincronizar(tickets: List<Ticket>) {
        tickets.sortedBy { it.id }.forEach { t ->
            if (!primeraVez.containsKey(t.id)) {
                primeraVez[t.id] = System.currentTimeMillis()
                registrar(t.id, TipoEvento.CREADO, "Recibido de ${t.cliente}: ${t.falla.take(70)}")
            }
        }
    }
}

// ---------- Equipo: "Clasificación · Marca/Referencia" se guarda en el campo `equipo` ----------

val MARCAS_EQUIPO = listOf("HP", "Dell", "Lenovo", "Asus", "Acer", "Apple", "MSI", "Samsung", "Huawei", "Otros")

data class EquipoInfo(val clasificacion: DeviceType, val marca: String, val referencia: String)

object EquipoCodec {
    private const val SEP = " · "

    fun decodificar(equipo: String): EquipoInfo {
        val partes = equipo.split(SEP, limit = 2)
        var clasif = DeviceType.values().firstOrNull { it.label.equals(partes[0].trim(), ignoreCase = true) }
        if (clasif == DeviceType.PORTATIL) clasif = DeviceType.LAPTOP
        return if (clasif != null) {
            desdeReferencia(clasif, partes.getOrNull(1)?.trim().orEmpty())
        } else {
            // Tickets antiguos con texto libre: se muestra como referencia manual
            desdeReferencia(DeviceType.PENDIENTE, equipo.trim())
        }
    }

    private fun desdeReferencia(c: DeviceType, ref: String): EquipoInfo {
        if (ref.isBlank()) return EquipoInfo(c, "", "")
        val marca = MARCAS_EQUIPO.firstOrNull { it != "Otros" && it.equals(ref, ignoreCase = true) }
        return if (marca != null) EquipoInfo(c, marca, "") else EquipoInfo(c, "Otros", ref)
    }

    fun codificar(info: EquipoInfo): String {
        val ref = if (info.marca == "Otros") info.referencia.trim() else info.marca
        return if (ref.isBlank()) info.clasificacion.label else info.clasificacion.label + SEP + ref
    }
}

// ---------- Formato de fechas ----------

fun formatearHora(ts: Long): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ts))

fun formatearFecha(ts: Long): String {
    val hoy = Calendar.getInstance()
    val otro = Calendar.getInstance().apply { timeInMillis = ts }
    val mismoDia = hoy.get(Calendar.YEAR) == otro.get(Calendar.YEAR) &&
            hoy.get(Calendar.DAY_OF_YEAR) == otro.get(Calendar.DAY_OF_YEAR)
    return if (mismoDia) "Hoy, ${formatearHora(ts)}"
    else SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(ts))
}
