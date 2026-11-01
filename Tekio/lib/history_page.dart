import 'package:flutter/material.dart';

import 'detail_screen.dart';
import 'ticket_store.dart';
import 'widgets.dart';

/// Pestaña "Historial": todos los tickets completados.
class HistoryPage extends StatelessWidget {
  const HistoryPage({super.key, required this.store});

  final TicketStore store;

  @override
  Widget build(BuildContext context) {
    return ListenableBuilder(
      listenable: store,
      builder: (context, _) {
        final tickets = store.history;
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Padding(
              padding: EdgeInsets.fromLTRB(16, 20, 16, 4),
              child: Text('Historial',
                  style: TextStyle(fontSize: 24, fontWeight: FontWeight.w800)),
            ),
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 0, 16, 8),
              child: Text(
                '${tickets.length} ticket${tickets.length == 1 ? '' : 's'} completado${tickets.length == 1 ? '' : 's'}',
                style: const TextStyle(fontSize: 13, color: Color(0xFF9AA0A6)),
              ),
            ),
            Expanded(
              child: tickets.isEmpty
                  ? const EmptyState(
                      icon: Icons.history,
                      text: 'Cuando completes un ticket aparecerá aquí.')
                  : ListView.separated(
                      padding: const EdgeInsets.fromLTRB(12, 8, 12, 24),
                      itemCount: tickets.length,
                      separatorBuilder: (_, __) => const SizedBox(height: 10),
                      itemBuilder: (context, i) => TicketCard(
                        ticket: tickets[i],
                        onTap: () => openTicket(context, store, tickets[i].id),
                      ),
                    ),
            ),
          ],
        );
      },
    );
  }
}
