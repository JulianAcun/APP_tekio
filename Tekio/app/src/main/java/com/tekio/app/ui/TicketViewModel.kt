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