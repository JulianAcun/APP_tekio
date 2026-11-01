<?php
// =====================================================================
// 1. MODO VERIFICACIÓN (Petición GET de Meta)
// =====================================================================
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    
    $mi_token_secreto = "TekioSeguridad2026"; 
    
    if (isset($_GET['hub_mode']) && isset($_GET['hub_verify_token']) && isset($_GET['hub_challenge'])) {
        $modo = $_GET['hub_mode'];
        $token_recibido = $_GET['hub_verify_token'];
        $reto = $_GET['hub_challenge'];

        if ($modo === 'subscribe' && $token_recibido === $mi_token_secreto) {
            http_response_code(200);
            echo $reto; // Devolvemos el número tal cual, sin formato JSON
            exit; 
        } else {
            http_response_code(403);
            exit;
        }
    }
}

// =====================================================================
// 2. MODO RECEPCIÓN DE TICKETS (Petición POST de Meta)
// =====================================================================
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    $conexion = new mysqli("localhost", "root", "", "glpi_local");

    if ($conexion->connect_error) {
        http_response_code(500);
        exit;
    }

    $datos_crudos = file_get_contents('php://input');
    $datos_json = json_decode($datos_crudos, true);

    // QA: Verificamos si el JSON trae el arreglo "messages" (Es un texto, no una notificación de "leído")
    if (isset($datos_json['entry'][0]['changes'][0]['value']['messages'][0])) {
        
        // Entramos al laberinto de datos de Meta
        $mensaje_info = $datos_json['entry'][0]['changes'][0]['value']['messages'][0];
        $contacto_info = $datos_json['entry'][0]['changes'][0]['value']['contacts'][0];

        // 1. Extraemos el nombre del cliente de su perfil de WhatsApp
        $cliente = $contacto_info['profile']['name']; 
        
        // 2. Extraemos lo que escribió (La falla)
        $falla = $mensaje_info['text']['body']; 
        
        // 3. Asignamos un equipo por defecto (El técnico lo cambiará en Android)
        $equipo = "Pendiente de clasificación"; 

        if (!empty($cliente) && !empty($falla)) {
            // Preparamos la inserción en tu tabla glpi_local
            $sql = "INSERT INTO tickets (cliente, equipo, falla) VALUES ('$cliente', '$equipo', '$falla')";
            
            if ($conexion->query($sql) === TRUE) {
                // Meta exige que respondamos SIEMPRE con un código 200 rápido para saber que recibimos el mensaje
                http_response_code(200); 
            } else {
                http_response_code(500);
            }
        }
    } else {
        // Si es un evento de estado (ej. status: read), simplemente respondemos 200 OK 
        // para que Meta quede tranquilo y no nos reenvíe la petición repetidas veces.
        http_response_code(200);
    }

    $conexion->close();
}
?>