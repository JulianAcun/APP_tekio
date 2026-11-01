import 'package:flutter/material.dart';

enum TicketStatus {
  nuevo('Nuevo', Color(0xFFE50914), Colors.white),
  enProceso('En proceso', Color(0xFFF5A300), Color(0xFF1A1200)),
  enEspera('En espera', Color(0xFF3A3F47), Colors.white),
  completado('Completado', Color(0xFF1E6B3C), Colors.white);

  const TicketStatus(this.label, this.bg, this.fg);
  final String label;
  final Color bg;
  final Color fg;

  static TicketStatus fromName(String? name) => TicketStatus.values
      .firstWhere((s) => s.name == name, orElse: () => TicketStatus.nuevo);
}

const String kPendiente = 'Pendiente de clasificación';

class DeviceOption {
  const DeviceOption(this.label, this.icon);
  final String label;
  final IconData icon;
}

const List<DeviceOption> kDeviceOptions = [
  DeviceOption('Laptop', Icons.laptop),
  DeviceOption('PC de escritorio', Icons.desktop_windows_outlined),
  DeviceOption('Monitor', Icons.monitor),
  DeviceOption('Impresora', Icons.print_outlined),
  DeviceOption('Otro', Icons.more_horiz),
];

String _two(int n) => n.toString().padLeft(2, '0');

class Ticket {
  const Ticket({
    required this.id,
    required this.client,
    required this.fault,
    required this.createdAt,
    this.status = TicketStatus.nuevo,
    this.device = kPendiente,
    this.notes = '',
  });

  final int id;
  final String client;
  final String fault;
  final DateTime createdAt;
  final TicketStatus status;
  final String device;
  final String notes;

  bool get completed => status == TicketStatus.completado;
  bool get isPending => device == kPendiente;
  String get code => '#${id.toString().padLeft(4, '0')}';

  /// Primera versión corta de la falla para la lista.
  String get summary => fault.replaceAll(RegExp(r'\s*\n\s*'), ' ');

  int get _daysAgo {
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final day = DateTime(createdAt.year, createdAt.month, createdAt.day);
    return today.difference(day).inDays;
  }

  String get _hm => '${_two(createdAt.hour)}:${_two(createdAt.minute)}';

  /// Etiqueta corta para la tarjeta: hora, "Ayer" o dd/MM.
  String get timeLabel {
    final d = _daysAgo;
    if (d <= 0) return _hm;
    if (d == 1) return 'Ayer';
    return '${_two(createdAt.day)}/${_two(createdAt.month)}';
  }

  /// Etiqueta larga para el detalle.
  String get dateLabel {
    final d = _daysAgo;
    if (d <= 0) return 'Hoy, $_hm';
    if (d == 1) return 'Ayer, $_hm';
    return '${_two(createdAt.day)}/${_two(createdAt.month)}/${createdAt.year}, $_hm';
  }

  Ticket copyWith({TicketStatus? status, String? device, String? notes}) {
    return Ticket(
      id: id,
      client: client,
      fault: fault,
      createdAt: createdAt,
      status: status ?? this.status,
      device: device ?? this.device,
      notes: notes ?? this.notes,
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'client': client,
        'fault': fault,
        'createdAt': createdAt.toIso8601String(),
        'status': status.name,
        'device': device,
        'notes': notes,
      };

  factory Ticket.fromJson(Map<String, dynamic> json) => Ticket(
        id: json['id'] as int,
        client: json['client'] as String,
        fault: json['fault'] as String,
        createdAt: DateTime.parse(json['createdAt'] as String),
        status: TicketStatus.fromName(json['status'] as String?),
        device: (json['device'] as String?) ?? kPendiente,
        notes: (json['notes'] as String?) ?? '',
      );
}

/// Datos de ejemplo (los del diseño) con fechas relativas a hoy.
List<Ticket> sampleTickets() {
  final now = DateTime.now();
  DateTime at(int daysAgo, int h, int m) =>
      DateTime(now.year, now.month, now.day - daysAgo, h, m);

  return [
    Ticket(
      id: 12,
      client: 'Juan Pérez',
      fault: 'El equipo no enciende, no hace nada.\nNo muestra señales de vida.',
      createdAt: at(0, 10, 18),
      status: TicketStatus.nuevo,
    ),
    Ticket(
      id: 11,
      client: 'María López',
      fault: 'Se apaga solo, ya se reinició varias veces.',
      createdAt: at(0, 9, 52),
      status: TicketStatus.enProceso,
      device: 'Laptop',
    ),
    Ticket(
      id: 10,
      client: 'Carlos Ramírez',
      fault: 'Pantalla con rayas, no se ve bien.',
      createdAt: at(0, 8, 37),
      status: TicketStatus.enProceso,
      device: 'Monitor',
    ),
    Ticket(
      id: 9,
      client: 'Laura Gómez',
      fault: 'No carga la batería, solo funciona conectado.',
      createdAt: at(0, 8, 12),
      status: TicketStatus.nuevo,
      device: 'Portátil',
    ),
    Ticket(
      id: 8,
      client: 'Andrés Silva',
      fault: 'Teclado no responde algunas teclas.',
      createdAt: at(0, 7, 45),
      status: TicketStatus.enEspera,
      device: 'Laptop',
    ),
    Ticket(
      id: 7,
      client: 'Sofía Torres',
      fault: 'Hace ruido extraño al encender.',
      createdAt: at(0, 6, 20),
      status: TicketStatus.enProceso,
      device: 'PC de escritorio',
    ),
    Ticket(
      id: 6,
      client: 'Pedro Díaz',
      fault: 'Impresora atasca el papel.',
      createdAt: at(1, 16, 40),
      status: TicketStatus.completado,
      device: 'Impresora',
    ),
    Ticket(
      id: 5,
      client: 'Valentina Ruiz',
      fault: 'El mouse no es detectado por el equipo.',
      createdAt: at(1, 11, 5),
      status: TicketStatus.completado,
      device: 'Otro',
    ),
    Ticket(
      id: 4,
      client: 'Camilo Herrera',
      fault: 'Sin conexión WiFi desde esta mañana.',
      createdAt: at(3, 9, 30),
      status: TicketStatus.completado,
      device: 'Laptop',
    ),
  ];
}
