<?php
header('Content-Type: application/json');
$conexion = new mysqli("localhost", "root", "", "glpi_local");

$id = $_POST['id'] ?? '';

if ($id != '') {
    $stmt = $conexion->prepare("UPDATE tickets SET estado = 'abierto' WHERE id = ?");
    $stmt->bind_param("i", $id);
    
    if ($stmt->execute()) {
        echo json_encode(["estado" => "exito", "mensaje" => "Ticket reabierto correctamente"]);
    } else {
        echo json_encode(["estado" => "error", "mensaje" => "Error al reabrir"]);
    }
    $stmt->close();
}
$conexion->close();
?>