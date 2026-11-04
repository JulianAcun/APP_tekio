<?php
header('Content-Type: application/json');
$conexion = new mysqli("localhost", "root", "", "glpi_local");

$id = $_POST['id'] ?? '';
$estado = $_POST['estado'] ?? '';
$marca_equipo = $_POST['marca_equipo'] ?? '';
$clasificacion_equipo = $_POST['clasificacion_equipo'] ?? '';
$repuesto = $_POST['repuesto'] ?? '';
$notas = $_POST['notas'] ?? '';

if ($id != '' && $estado != '') {
    $stmt = $conexion->prepare("UPDATE tickets SET estado=?, marca_equipo=?, clasificacion_equipo=?, repuesto=?, notas=? WHERE id=?");
    $stmt->bind_param("sssssi", $estado, $marca_equipo, $clasificacion_equipo, $repuesto, $notas, $id);
    
    if ($stmt->execute()) {
        echo json_encode(["estado" => "exito", "mensaje" => "Ticket actualizado correctamente"]);
    } else {
        echo json_encode(["estado" => "error", "mensaje" => "Error al actualizar"]);
    }
    $stmt->close();
} else {
    echo json_encode(["estado" => "error", "mensaje" => "Faltan datos requeridos"]);
}
$conexion->close();
?>