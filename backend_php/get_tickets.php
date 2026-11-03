<?php
// 1. CABECERA Y CONEXIÓN
header('Content-Type: application/json');

$conexion = new mysqli("localhost", "root", "", "glpi_local");

if ($conexion->connect_error) {
    die(json_encode(["estado" => "error", "mensaje" => "Error de conexión a la BD"]));
}

// 2. CONSULTA SQL (El "Por qué" del ORDER BY)
// Añadimos 'estado' a la consulta para que Android pueda separar las pestañas
$sql = "SELECT id, cliente, equipo, falla, estado FROM tickets ORDER BY id DESC";
$resultado = $conexion->query($sql);

// 3. EXTRACCIÓN Y ESTRUCTURACIÓN DE DATOS
$tickets = array();

if ($resultado->num_rows > 0) {
    while($fila = $resultado->fetch_assoc()) {
        $tickets[] = $fila; 
    }
}

// 4. TRANSFORMACIÓN A JSON Y RESPUESTA
echo json_encode($tickets);

$conexion->close();
?>