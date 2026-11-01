import 'package:flutter/material.dart';

import 'theme.dart';
import 'ticket_store.dart';
import 'widgets.dart';

/// Pestaña "Perfil": resumen de tickets y opciones de la app.
class ProfilePage extends StatelessWidget {
  const ProfilePage({super.key, required this.store});

  final TicketStore store;

  @override
  Widget build(BuildContext context) {
    return ListenableBuilder(
      listenable: store,
      builder: (context, _) {
        return ListView(
          padding: const EdgeInsets.all(16),
          children: [
            const SizedBox(height: 12),
            Center(
              child: Container(
                width: 88,
                height: 88,
                decoration: const BoxDecoration(
                  color: kRed,
                  shape: BoxShape.circle,
                ),
                child: const Icon(Icons.person, size: 48, color: Colors.white),
              ),
            ),
            const SizedBox(height: 14),
            const Center(
              child: Text('Técnico de soporte',
                  style: TextStyle(fontSize: 20, fontWeight: FontWeight.w700)),
            ),
            const Center(
              child: Padding(
                padding: EdgeInsets.only(top: 4),
                child: Text('Tekio · Gestión de Tickets',
                    style: TextStyle(fontSize: 13, color: kMuted)),
              ),
            ),
            const SizedBox(height: 24),
            Row(
              children: [
                _Stat(label: 'Activos', value: store.activeCount),
                const SizedBox(width: 10),
                _Stat(label: 'Completados', value: store.completedCount),
                const SizedBox(width: 10),
                _Stat(label: 'Total', value: store.totalCount),
              ],
            ),
            const SizedBox(height: 24),
            OutlinedButton.icon(
              onPressed: () => confirmReset(context, store),
              style: OutlinedButton.styleFrom(
                foregroundColor: kText,
                side: const BorderSide(color: kRedDark),
                minimumSize: const Size.fromHeight(50),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10)),
              ),
              icon: const Icon(Icons.restart_alt),
              label: const Text('Restablecer datos de ejemplo'),
            ),
          ],
        );
      },
    );
  }
}

class _Stat extends StatelessWidget {
  const _Stat({required this.label, required this.value});

  final String label;
  final int value;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 16),
        decoration: BoxDecoration(
          color: kSurface,
          border: Border.all(color: kBorder),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          children: [
            Text('$value',
                style: const TextStyle(
                    fontSize: 28, fontWeight: FontWeight.w800, color: kRedText)),
            const SizedBox(height: 2),
            Text(label, style: const TextStyle(fontSize: 12, color: kMuted)),
          ],
        ),
      ),
    );
  }
}
