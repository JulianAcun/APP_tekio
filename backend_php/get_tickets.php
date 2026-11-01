<?php
// 1. CABECERA Y CONEXIÓN
// Le decimos a Android (o al navegador) que lo que va a recibir es un JSON puro
header('Content-Type: application/json');

$conexion = new mysqli("localhost", "root", "", "glpi_local");

if ($conexion->connect_error) {
    die(json_encode(["estado" => "error", "mensaje" => "Error de conexión a la BD"]));
}

// 2. CONSULTA SQL (El "Por qué" del ORDER BY)
// Traemos todos los tickets, pero ordenados de forma descendente (DESC). 
// Esto es vital en UX para que el técnico vea siempre los casos más recientes al inicio de su app.
$sql = "SELECT id, cliente, equipo, falla FROM tickets ORDER BY id DESC";
$resultado = $conexion->query($sql);

// 3. EXTRACCIÓN Y ESTRUCTURACIÓN DE DATOS
// Creamos un arreglo vacío. Aquí iremos guardando cada fila que nos devuelva la base de datos.
$tickets = array();

if ($resultado->num_rows > 0) {
    // fetch_assoc() toma cada fila de SQL y la convierte en un arreglo asociativo de PHP
    while($fila = $resultado->fetch_assoc()) {
        $tickets[] = $fila; // Empujamos la fila dentro de nuestro arreglo principal
    }
}

// 4. TRANSFORMACIÓN A JSON Y RESPUESTA
// json_encode agarra nuestro arreglo de PHP y lo traduce al formato universal JSON que Android consume
echo json_encode($tickets);

$conexion->close();
?>