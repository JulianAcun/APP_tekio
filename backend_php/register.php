<?php
header('Content-Type: application/json');
$conexion = new mysqli("localhost", "root", "", "glpi_local");

$nombre = $_POST['nombre'] ?? '';
$usuario = $_POST['usuario'] ?? '';
$password = $_POST['password'] ?? '';

if ($nombre != '' && $usuario != '' && $password != '') {
    $check = $conexion->prepare("SELECT id FROM tecnicos WHERE usuario = ?");
    $check->bind_param("s", $usuario);
    $check->execute();
    if ($check->get_result()->num_rows > 0) {
        echo json_encode(["estado" => "error", "mensaje" => "El usuario ya existe"]);
    } else {
        $stmt = $conexion->prepare("INSERT INTO tecnicos (nombre, usuario, password) VALUES (?, ?, ?)");
        $stmt->bind_param("sss", $nombre, $usuario, $password);
        if ($stmt->execute()) {
            echo json_encode(["estado" => "exito", "mensaje" => "Usuario creado. Ya puedes iniciar sesión."]);
        } else {
            echo json_encode(["estado" => "error", "mensaje" => "Error al registrar"]);
        }
        $stmt->close();
    }
    $check->close();
} else {
    echo json_encode(["estado" => "error", "mensaje" => "Faltan datos"]);
}
$conexion->close();
?>