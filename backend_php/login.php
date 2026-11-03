<?php
header('Content-Type: application/json');
$conexion = new mysqli("localhost", "root", "", "glpi_local");

$usuario = $_POST['usuario'] ?? '';
$password = $_POST['password'] ?? '';

$stmt = $conexion->prepare("SELECT id, nombre FROM tecnicos WHERE usuario = ? AND password = ?");
$stmt->bind_param("ss", $usuario, $password);
$stmt->execute();
$resultado = $stmt->get_result();

if ($fila = $resultado->fetch_assoc()) {
    echo json_encode(["estado" => "exito", "mensaje" => $fila['nombre']]);
} else {
    echo json_encode(["estado" => "error", "mensaje" => "Credenciales incorrectas"]);
}
$stmt->close();
$conexion->close();
?>