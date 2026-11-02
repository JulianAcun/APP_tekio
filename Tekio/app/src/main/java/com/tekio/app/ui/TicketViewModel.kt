package com.tekio.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.tekio.app.RetrofitClient
import com.tekio.app.data.Ticket

class TicketViewModel : ViewModel() {

    // 1. Variable reactiva que guardará la lista de tickets. Comienza vacía.
    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets.asStateFlow()

    // 2. Al abrir la app (inicializar el ViewModel), ejecutamos la consulta automáticamente
    init {
        obtenerTicketsDeMySQL()
    }
    fun actualizarEquipoEnMySQL(id: Int, nuevoEquipo: String) {
        viewModelScope.launch {
            try {
                val respuesta = RetrofitClient.apiService.actualizarEquipo(id, nuevoEquipo)
                if (respuesta.estado == "exito") {
                    // Si el PHP confirma la actualización, recargamos la lista para refrescar la pantalla
                    obtenerTicketsDeMySQL()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun cerrarTicketEnMySQL(id: Int) {
        viewModelScope.launch {
            try {
                val respuesta = RetrofitClient.apiService.cerrarTicket(id)
                if (respuesta.estado == "exito") {
                    obtenerTicketsDeMySQL() // Refresca la lista al volver
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun obtenerTicketsDeMySQL() {
        // viewModelScope.launch abre un hilo de trabajo en segundo plano
        viewModelScope.launch {
            try {
                // Llamamos a tu archivo PHP a través de Retrofit
                val listaDesdePHP = RetrofitClient.apiService.obtenerTickets()

                // Inyectamos los datos reales en la variable reactiva
                _tickets.value = listaDesdePHP

            } catch (e: Exception) {
                // Si el túnel de Ngrok está apagado o no hay internet, evitamos que la app explote
                e.printStackTrace()
            }
        }
    }
}