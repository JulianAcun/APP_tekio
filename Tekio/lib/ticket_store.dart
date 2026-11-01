import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'models.dart';

/// Estado de la app. Guarda los tickets en el teléfono con shared_preferences.
/// Si más adelante quieres una base de datos o una API, solo cambia este archivo.
class TicketStore extends ChangeNotifier {
  TicketStore._(this._prefs, this._tickets);

  static const _key = 'tekio_tickets_v1';

  final SharedPreferences _prefs;
  List<Ticket> _tickets;
  bool _showCompleted = false;
  String _query = '';

  static Future<TicketStore> load() async {
    final prefs = await SharedPreferences.getInstance();
    final raw = prefs.getString(_key);
    List<Ticket> list;
    if (raw == null) {
      list = sampleTickets();
    } else {
      try {
        list = (jsonDecode(raw) as List)
            .map((e) => Ticket.fromJson(e as Map<String, dynamic>))
            .toList();
      } catch (_) {
        list = sampleTickets();
      }
    }
    return TicketStore._(prefs, list);
  }

  // ---- Lectura -------------------------------------------------------------

  bool get showCompleted => _showCompleted;
  String get query => _query;
  int get activeCount => _tickets.where((t) => !t.completed).length;
  int get completedCount => _tickets.where((t) => t.completed).length;
  int get totalCount => _tickets.length;

  bool _matches(Ticket t) {
    if (_query.isEmpty) return true;
    final q = _query.toLowerCase();
    return t.client.toLowerCase().contains(q) ||
        t.code.contains(q) ||
        t.fault.toLowerCase().contains(q) ||
        t.device.toLowerCase().contains(q);
  }

  /// Tickets de la pestaña seleccionada (activos o completados), con búsqueda.
  List<Ticket> get visible => _tickets
      .where((t) => t.completed == _showCompleted && _matches(t))
      .toList();

  /// Todos los tickets completados, del más reciente al más antiguo.
  List<Ticket> get history {
    final list = _tickets.where((t) => t.completed).toList();
    list.sort((a, b) => b.createdAt.compareTo(a.createdAt));
    return list;
  }

  Ticket? byId(int id) {
    for (final t in _tickets) {
      if (t.id == id) return t;
    }
    return null;
  }

  // ---- Escritura -----------------------------------------------------------

  void setShowCompleted(bool value) {
    _showCompleted = value;
    notifyListeners();
  }

  void setQuery(String value) {
    _query = value.trim();
    notifyListeners();
  }

  Ticket add({
    required String client,
    required String fault,
    String device = kPendiente,
  }) {
    final nextId = _tickets.isEmpty
        ? 1
        : _tickets.map((t) => t.id).reduce((a, b) => a > b ? a : b) + 1;
    final ticket = Ticket(
      id: nextId,
      client: client,
      fault: fault,
      createdAt: DateTime.now(),
      device: device,
    );
    _tickets.insert(0, ticket);
    _commit();
    return ticket;
  }

  void _update(int id, Ticket Function(Ticket) change) {
    final i = _tickets.indexWhere((t) => t.id == id);
    if (i < 0) return;
    _tickets[i] = change(_tickets[i]);
    _commit();
  }

  void setDevice(int id, String device) =>
      _update(id, (t) => t.copyWith(device: device));

  void setNotes(int id, String notes) =>
      _update(id, (t) => t.copyWith(notes: notes));

  void setStatus(int id, TicketStatus status) =>
      _update(id, (t) => t.copyWith(status: status));

  void complete(int id) => setStatus(id, TicketStatus.completado);

  void reopen(int id) => setStatus(id, TicketStatus.nuevo);

  void remove(int id) {
    _tickets.removeWhere((t) => t.id == id);
    _commit();
  }

  void resetSamples() {
    _tickets = sampleTickets();
    _query = '';
    _showCompleted = false;
    _commit();
  }

  void _commit() {
    _prefs.setString(
        _key, jsonEncode(_tickets.map((t) => t.toJson()).toList()));
    notifyListeners();
  }
}
