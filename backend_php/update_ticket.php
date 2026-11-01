<?php
header('Content-Type: application/json');
$conexion = new mysqli("localhost", "root", "", "glpi_local");

if ($conexion->connect_error) {
    die(json_encode(["estado" => "error", "mensaje" => "Error de conexión a la BD"]));
}

// Capturamos las variables enviadas por Android mediante POST
$id = $_POST['id'] ?? '';
$equipo = $_POST['equipo'] ?? '';

if ($id != '' && $equipo != '') {
    // Preparamos la consulta UPDATE (Usamos bind_param por seguridad)
    $stmt = $conexion->prepare("UPDATE tickets SET equipo = ? WHERE id = ?");
    $stmt->bind_param("si", $equipo, $id);
    
    if ($stmt->execute()) {
        echo json_encode(["estado" => "exito", "mensaje" => "Ticket actualizado correctamente"]);
    } else {
        echo json_encode(["estado" => "error", "mensaje" => "Error al ejecutar UPDATE"]);
    }
    $stmt->close();
} else {
    echo json_encode(["estado" => "error", "mensaje" => "Faltan datos de ID o equipo"]);
}

$conexion->close();
?>