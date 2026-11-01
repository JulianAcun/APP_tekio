# Tekio – Gestión de tickets (Flutter)

App móvil de gestión de tickets con tema oscuro rojo. Funciona igual en **Visual Studio Code** y en **Android Studio**.

## Requisitos
- Flutter 3.22 o superior (incluye Dart 3.3+): https://docs.flutter.dev/get-started/install
- Android Studio con el SDK de Android y un emulador (o un teléfono con depuración USB)
- Visual Studio Code con la extensión **Flutter** (instala también la de Dart)

## Primera vez
Esta carpeta trae solo `pubspec.yaml` y `lib/`. Las carpetas de plataforma (`android/`, etc.) se generan con Flutter:

```bash
cd tekio_flutter
flutter create . --project-name tekio --platforms=android,ios
flutter pub get
flutter run
```

`flutter create .` no pisa `lib/main.dart` ni `pubspec.yaml` si ya existen. Si pregunta, conserva los tuyos.
También crea `test/widget_test.dart`, que apunta a una clase que aquí no existe. Bórralo (o ajústalo) para evitar errores de análisis.

## Abrir el proyecto
- **VS Code:** File → Open Folder → `tekio_flutter`. Elige el emulador abajo a la derecha y pulsa F5.
- **Android Studio:** File → Open → `tekio_flutter`. Elige el emulador y pulsa Run.

## Funciones
- **Lista de tickets** con pestañas Activos (contador) y Completados.
- **Buscar** por cliente, número (#0012), falla o equipo (lupa de la cabecera).
- **Crear ticket** con el botón "+" (cliente, falla y equipo).
- **Detalle del ticket**: falla reportada, clasificar equipo, notas internas (máx. 200).
- **Cerrar / Completar** un ticket y **Reabrirlo** desde el detalle.
- **Cambiar estado** (Nuevo, En proceso, En espera) y **eliminar** desde el menú ⋮ del detalle.
- **Historial**: todos los tickets completados.
- **Perfil**: resumen (activos, completados, total) y restablecer datos de ejemplo.
- **Guardado local**: los tickets se conservan al cerrar la app (shared_preferences).

## Estructura (`lib/`)
- `main.dart`: arranque de la app
- `home_shell.dart`: barra inferior (Tickets, Historial, Perfil) y botón "+"
- `tickets_page.dart`: cabecera, búsqueda, pestañas y lista
- `detail_screen.dart`: detalle del ticket
- `new_ticket_screen.dart`: formulario de nuevo ticket
- `history_page.dart`, `profile_page.dart`: las otras dos pestañas
- `widgets.dart`: tarjeta de ticket, etiqueta de estado, estado vacío
- `models.dart`: modelo `Ticket`, estados y datos de ejemplo
- `ticket_store.dart`: estado y guardado local (aquí se cambiaría a base de datos o API)
- `theme.dart`: colores y tema
