import 'package:flutter/material.dart';

import 'models.dart';
import 'theme.dart';
import 'ticket_store.dart';

class StatusBadge extends StatelessWidget {
  const StatusBadge({super.key, required this.status, this.large = false});

  final TicketStatus status;
  final bool large;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.symmetric(
          horizontal: large ? 16 : 11, vertical: large ? 6 : 4),
      decoration: BoxDecoration(
        color: status.bg,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Text(
        status.label,
        style: TextStyle(
          color: status.fg,
          fontSize: large ? 14 : 12,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }
}

class TicketCard extends StatelessWidget {
  const TicketCard({super.key, required this.ticket, required this.onTap});

  final Ticket ticket;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final pending = ticket.isPending;
    return Material(
      color: kSurface,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: const BorderSide(color: Color(0xFF1F2329)),
      ),
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: onTap,
        child: IntrinsicHeight(
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Container(width: 4, color: ticket.status.bg),
              Expanded(
                child: Padding(
                  padding: const EdgeInsets.fromLTRB(12, 12, 0, 12),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(ticket.code,
                          style: const TextStyle(
                              fontSize: 17,
                              fontWeight: FontWeight.w700,
                              color: kRedText)),
                      const SizedBox(height: 2),
                      Text(ticket.client,
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                          style: const TextStyle(
                              fontSize: 15, fontWeight: FontWeight.w600)),
                      const SizedBox(height: 2),
                      Text(ticket.summary,
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                          style: const TextStyle(fontSize: 13, color: kMuted)),
                      const SizedBox(height: 8),
                      Row(
                        children: [
                          const Icon(Icons.smartphone_outlined,
                              size: 20, color: kMuted),
                          const SizedBox(width: 8),
                          Flexible(
                            child: Container(
                              padding: const EdgeInsets.symmetric(
                                  horizontal: 10, vertical: 3),
                              decoration: BoxDecoration(
                                color: pending
                                    ? const Color(0xFF262B32)
                                    : const Color(0xFF3A1014),
                                borderRadius: BorderRadius.circular(12),
                              ),
                              child: Text(
                                ticket.device,
                                maxLines: 1,
                                overflow: TextOverflow.ellipsis,
                                style: TextStyle(
                                  fontSize: 12,
                                  fontWeight: FontWeight.w500,
                                  color: pending
                                      ? const Color(0xFFC9CDD3)
                                      : const Color(0xFFFF8A8F),
                                ),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.fromLTRB(8, 12, 6, 12),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  crossAxisAlignment: CrossAxisAlignment.end,
                  children: [
                    StatusBadge(status: ticket.status),
                    Row(
                      children: [
                        Text(ticket.timeLabel,
                            style:
                                const TextStyle(fontSize: 12, color: kMuted)),
                        const Icon(Icons.chevron_right, color: kMuted),
                      ],
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class EmptyState extends StatelessWidget {
  const EmptyState({super.key, required this.icon, required this.text});

  final IconData icon;
  final String text;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, size: 48, color: kMuted),
            const SizedBox(height: 12),
            Text(text,
                textAlign: TextAlign.center,
                style: const TextStyle(fontSize: 15, color: kMuted)),
          ],
        ),
      ),
    );
  }
}

/// Pregunta antes de borrar los tickets y volver a los datos de ejemplo.
Future<void> confirmReset(BuildContext context, TicketStore store) async {
  final ok = await showDialog<bool>(
    context: context,
    builder: (ctx) => AlertDialog(
      backgroundColor: kSurface,
      title: const Text('Restablecer datos'),
      content: const Text(
          'Se borrarán tus tickets y volverán los datos de ejemplo.'),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(ctx, false),
          child: const Text('Cancelar'),
        ),
        FilledButton(
          style: FilledButton.styleFrom(backgroundColor: kRed),
          onPressed: () => Navigator.pop(ctx, true),
          child: const Text('Restablecer'),
        ),
      ],
    ),
  );
  if (ok == true) store.resetSamples();
}
