<?php
header('Content-Type: application/json');
$conexion = new mysqli("localhost", "root", "", "glpi_local");

$id = $_POST['id'] ?? '';

if ($id != '') {
    $stmt = $conexion->prepare("UPDATE tickets SET estado = 'cerrado' WHERE id = ?");
    $stmt->bind_param("i", $id);
    
    if ($stmt->execute()) {
        echo json_encode(["estado" => "exito", "mensaje" => "Ticket cerrado correctamente"]);
    } else {
        echo json_encode(["estado" => "error", "mensaje" => "Error al cerrar"]);
    }
    $stmt->close();
} else {
    echo json_encode(["estado" => "error", "mensaje" => "ID no proporcionado"]);
}
$conexion->close();
?>