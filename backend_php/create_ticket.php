<?php
header('Content-Type: application/json');
$conexion = new mysqli("localhost", "root", "", "glpi_local");

$cliente = $_POST['cliente'] ?? '';
$equipo = $_POST['equipo'] ?? '';
$falla = $_POST['falla'] ?? '';

// Verificamos que al menos el cliente y la falla no estén vacíos
if ($cliente != '' && $falla != '') {
    $stmt = $conexion->prepare("INSERT INTO tickets (cliente, equipo, falla, estado) VALUES (?, ?, ?, 'abierto')");
    $stmt->bind_param("sss", $cliente, $equipo, $falla);
    
    if ($stmt->execute()) {
        echo json_encode(["estado" => "exito", "mensaje" => "Ticket creado correctamente"]);
    } else {
        echo json_encode(["estado" => "error", "mensaje" => "Error al guardar el ticket"]);
    }
    $stmt->close();
} else {
    echo json_encode(["estado" => "error", "mensaje" => "Faltan datos obligatorios"]);
}
$conexion->close();
?>