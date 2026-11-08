# 📱 Tekio - Sistema de Gestión de Servicios Técnicos

Tekio es una solución de software cliente-servidor diseñada para centralizar y optimizar la gestión de tickets de servicio para técnicos de hardware independientes. El proyecto conecta una aplicación móvil nativa en Android con una API REST construida en PHP.

## 🚀 Rol en el Proyecto
**Líder Técnico & Scrum Master**
- Lideré el desarrollo del código en Android Studio y la arquitectura del Backend.
- Diseñé la base de datos relacional y el webhook para recepción de datos.
- Gestioné los ciclos de desarrollo utilizando metodologías ágiles (Scrum) y aprendizaje basado en proyectos.

## 🛠️ Stack Tecnológico
**Frontend (Móvil):**
- Android Studio (Nativo)
- Java / Kotlin
- Material Design (UI/UX accesible para movilidad)

**Backend & API:**
- PHP
- MySQL (Bases de datos relacionales)
- Arquitectura API REST (Webhook)
- Postman (Testing de endpoints)
- Ngrok (Túnel para exposición a internet)

## ✨ Características Principales
- **Registro de Tickets:** Creación rápida de servicios priorizando 3 datos clave (Cliente, Equipo, Falla).
- **Consumo de API:** Comunicación bidireccional entre la app móvil y el servidor mediante peticiones HTTP.
- **Validación de Datos:** Webhook blindado con validación de entradas y respuestas estructuradas en JSON (códigos 200/400).
- **Persistencia Local:** Preparado para almacenar datos en el dispositivo minimizando la dependencia de la red.

## 🏗️ Arquitectura
El sistema funciona bajo un modelo Cliente-Servidor:
1. El usuario interactúa con la **App Android** desarrollada bajo un patrón de diseño limpio.
2. La app envía solicitudes a una **API en PHP** alojada en un servidor.
3. El servidor procesa la lógica, interactúa con la base de datos **MySQL** y retorna respuestas en formato JSON.

## ⚙️ Instalación y Pruebas (Entorno Local)
1. Clonar este repositorio.
2. Importar la base de datos `tickets.sql` en phpMyAdmin (XAMPP).
3. Mover la carpeta del backend al directorio `htdocs` de XAMPP.
4. Ejecutar Apache y MySQL.
5. Abrir el proyecto móvil en Android Studio y sincronizar Gradle.
6. (Opcional) Usar Ngrok para exponer el `webhook.php` y hacer pruebas con Postman.
