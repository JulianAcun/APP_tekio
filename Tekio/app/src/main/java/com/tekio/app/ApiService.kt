package com.tekio.app

import com.tekio.app.data.Ticket
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

// 1. Definimos las rutas exactas de tus archivos PHP
interface TekioApi {
    @GET("sistema_tickets/get_tickets.php")
    suspend fun obtenerTickets(): List<Ticket>
    @FormUrlEncoded
    @POST("sistema_tickets/close_ticket.php")
    suspend fun cerrarTicket(@Field("id") id: Int): RespuestaPHP
    // NUEVO: Endpoint para enviar la actualización del equipo a MySQL
    @FormUrlEncoded
    @POST("sistema_tickets/update_ticket.php")
    suspend fun actualizarEquipo(
        @Field("id") id: Int,
        @Field("equipo") equipo: String
    ): RespuestaPHP
}

// 2. Configuramos el cliente con tu URL de Ngrok
object RetrofitClient {
    private const val BASE_URL = "https://creole-endurable-immature.ngrok-free.dev/"

    val apiService: TekioApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TekioApi::class.java)
    }
}

// NUEVO: Modelo para leer la respuesta (éxito o error) que envía tu archivo update_ticket.php
data class RespuestaPHP(val estado: String, val mensaje: String)