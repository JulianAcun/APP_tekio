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
    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    fun limpiarError() { _mensajeError.value = null }
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
                _mensajeError.value = "Sin conexión al servidor Ngrok"
                e.printStackTrace()
            }
        }
    }
    fun iniciarSesion(
        usuario: String,
        clave: String,
        onExito: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val respuesta = RetrofitClient.apiService.login(usuario, clave)
                if (respuesta.estado == "exito") {
                    onExito(respuesta.mensaje) // Trae el nombre de MySQL
                } else {
                    onError(respuesta.mensaje)
                }
            } catch (e: Exception) {
                onError("Sin conexión al servidor")
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
                _mensajeError.value = "Sin conexión al servidor Ngrok"
                e.printStackTrace()
            }
        }
    }
    fun crearTicketEnMySQL(cliente: String, equipo: String, falla: String) {
        viewModelScope.launch {
            try {
                val respuesta = RetrofitClient.apiService.crearTicket(cliente, equipo, falla)
                if (respuesta.estado == "exito") {
                    obtenerTicketsDeMySQL() // Recargamos para que el nuevo ticket aparezca arriba
                }
            } catch (e: Exception) {
                _mensajeError.value = "Sin conexión al servidor Ngrok"
                e.printStackTrace()
            }
        }
    }
    fun reabrirTicketEnMySQL(id: Int) {
        viewModelScope.launch {
            try {
                val respuesta = RetrofitClient.apiService.reabrirTicket(id)
                if (respuesta.estado == "exito") {
                    obtenerTicketsDeMySQL() // Recarga la lista
                }
            } catch (e: Exception) {
                _mensajeError.value = "Sin conexión al servidor Ngrok"
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
                _mensajeError.value = "Sin conexión al servidor Ngrok"
                e.printStackTrace()
            }
        }
    }
    fun registrarUsuario(
        nombre: String,
        usuario: String,
        clave: String,
        onExito: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val respuesta = RetrofitClient.apiService.registrar(nombre, usuario, clave)
                if (respuesta.estado == "exito") {
                    onExito(respuesta.mensaje)
                } else {
                    onError(respuesta.mensaje)
                }
            } catch (e: Exception) {
                onError("Sin conexión al servidor Ngrok")
                e.printStackTrace()
            }
        }
    }
    fun guardarCambiosTicket(
        id: Int,
        estado: String,
        marcaEquipo: String,
        clasificacionEquipo: String,
        repuesto: String,
        notas: String,
        onExito: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val respuesta = RetrofitClient.apiService.actualizarTicket(
                    id, estado, marcaEquipo, clasificacionEquipo, repuesto, notas
                )
                if (respuesta.estado == "exito") {
                    obtenerTicketsDeMySQL() // Refresca la lista general para ver los cambios
                    onExito(respuesta.mensaje)
                } else {
                    onError(respuesta.mensaje)
                }
            } catch (e: Exception) {
                onError("Sin conexión al servidor")
                e.printStackTrace()
            }
        }
    }
}