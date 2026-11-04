package com.tekio.app.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tekio.app.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: TicketViewModel,
    onLoginSuccess: (String) -> Unit
) {
    var esRegistro by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    val contexto = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize().background(TekioBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier.size(80.dp).clip(RoundedCornerShape(16.dp)).background(TekioRed),
                contentAlignment = Alignment.Center
            ) {
                Text("T", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(16.dp))
            Text("TEKIO", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
            Text(if (esRegistro) "Registro de Técnico" else "Acceso Técnico", color = TekioMuted, fontSize = 16.sp)

            Spacer(Modifier.height(48.dp))

            if (esRegistro) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre Completo", color = TekioMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TekioRed, unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White
                    )
                )
                Spacer(Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text("Usuario", color = TekioMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TekioRed, unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                )
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = TekioMuted) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TekioRed, unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                )
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (usuario.isNotBlank() && password.isNotBlank()) {
                        cargando = true
                        if (esRegistro) {
                            if (nombre.isNotBlank()) {
                                viewModel.registrarUsuario(nombre, usuario, password,
                                    onExito = { msj ->
                                        cargando = false
                                        Toast.makeText(contexto, msj, Toast.LENGTH_SHORT).show()
                                        esRegistro = false // Regresa al login automáticamente
                                    },
                                    onError = { err ->
                                        cargando = false
                                        Toast.makeText(contexto, err, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                cargando = false
                                Toast.makeText(contexto, "Ingresa tu nombre", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            viewModel.iniciarSesion(usuario, password,
                                onExito = { nombreTecnico ->
                                    cargando = false
                                    onLoginSuccess(nombreTecnico)
                                },
                                onError = { error ->
                                    cargando = false
                                    Toast.makeText(contexto, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TekioRed),
                enabled = !cargando
            ) {
                if (cargando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (esRegistro) "Registrarme" else "Ingresar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (esRegistro) "¿Ya tienes cuenta? Inicia Sesión" else "¿No tienes cuenta? Regístrate",
                color = TekioRed,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { esRegistro = !esRegistro }
            )
        }
    }
}