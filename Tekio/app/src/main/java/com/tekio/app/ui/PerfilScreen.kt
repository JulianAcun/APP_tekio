package com.tekio.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tekio.app.ui.theme.*

@Composable
fun PerfilScreen(viewModel: TicketViewModel, nombreTecnico: String, onLogout: () -> Unit) {
    val tickets by viewModel.tickets.collectAsState()
    val activos = tickets.count { it.estado != "cerrado" }
    val completados = tickets.count { it.estado == "cerrado" }

    Column(
        Modifier.fillMaxSize().background(TekioBackground).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Box(
            Modifier.size(100.dp).clip(CircleShape).background(TekioRed),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (nombreTecnico.isNotBlank()) nombreTecnico.take(1).uppercase() else "T",
                color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(nombreTecnico.ifBlank { "Técnico" }, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Técnico de soporte", color = TekioMuted, fontSize = 16.sp)

        Spacer(Modifier.height(28.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Dato("Activos", activos, TekioRed, Modifier.weight(1f))
            Dato("Completados", completados, Color(0xFF2E9E5B), Modifier.weight(1f))
            Dato("Total", tickets.size, TekioMuted, Modifier.weight(1f))
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C)),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) { Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun Dato(titulo: String, valor: Int, color: Color, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(10.dp)).background(TekioSurface).padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("$valor", color = color, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(titulo, color = TekioMuted, fontSize = 12.sp)
    }
}
