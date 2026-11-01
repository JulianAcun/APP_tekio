package com.tekio.app

import com.tekio.app.data.Ticket
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// 1. Definimos la ruta exacta de tu archivo PHP
interface TekioApi {
    @GET("sistema_tickets/get_tickets.php")
    suspend fun obtenerTickets(): List<Ticket>
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