import 'package:flutter/material.dart';

import 'models.dart';
import 'theme.dart';
import 'ticket_store.dart';
import 'widgets.dart';

void openTicket(BuildContext context, TicketStore store, int id) {
  Navigator.of(context).push(
    MaterialPageRoute(builder: (_) => DetailScreen(store: store, ticketId: id)),
  );
}

class DetailScreen extends StatefulWidget {
  const DetailScreen({super.key, required this.store, required this.ticketId});

  final TicketStore store;
  final int ticketId;

  @override
  State<DetailScreen> createState() => _DetailScreenState();
}

class _DetailScreenState extends State<DetailScreen> {
  late final TextEditingController _notes;

  // Opción resaltada en la lista "Clasificar equipo".
  late String _highlight;

  @override
  void initState() {
    super.initState();
    final t = widget.store.byId(widget.ticketId);
    _notes = TextEditingController(text: t?.notes ?? '');
    _highlight = kDeviceOptions.any((o) => o.label == t?.device)
        ? t!.device
        : kDeviceOptions.first.label;
  }

  @override
  void dispose() {
    _notes.dispose();
    super.dispose();
  }

  void _pick(String label) {
    setState(() => _highlight = label);
    widget.store.setDevice(widget.ticketId, label);
  }

  void _complete() {
    final messenger = ScaffoldMessenger.of(context);
    widget.store.complete(widget.ticketId);
    Navigator.of(context).pop();
    messenger.showSnackBar(const SnackBar(content: Text('Ticket completado')));
  }

  void _reopen() {
    widget.store.reopen(widget.ticketId);
    ScaffoldMessenger.of(context)
        .showSnackBar(const SnackBar(content: Text('Ticket reabierto')));
  }

  Future<void> _delete() async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        backgroundColor: kSurface,
        title: const Text('Eliminar ticket'),
        content: const Text('Esta acción no se puede deshacer.'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: const Text('Cancelar'),
          ),
          FilledButton(
            style: FilledButton.styleFrom(backgroundColor: kRed),
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text('Eliminar'),
          ),
        ],
      ),
    );
    if (ok != true || !mounted) return;
    final messenger = ScaffoldMessenger.of(context);
    Navigator.of(context).pop();
    widget.store.remove(widget.ticketId);
    messenger.showSnackBar(const SnackBar(content: Text('Ticket eliminado')));
  }

  void _onMenu(String value) {
    if (value == 'delete') {
      _delete();
    } else {
      widget.store
          .setStatus(widget.ticketId, TicketStatus.fromName(value));
    }
  }

  @override
  Widget build(BuildContext context) {
    return ListenableBuilder(
      listenable: widget.store,
      builder: (context, _) {
        final t = widget.store.byId(widget.ticketId);
        if (t == null) return const Scaffold(); // ya fue eliminado
        return Scaffold(
          appBar: AppBar(
            backgroundColor: kBg,
            surfaceTintColor: Colors.transparent,
            leading: IconButton(
              icon: const Icon(Icons.arrow_back),
              tooltip: 'Volver',
              onPressed: () => Navigator.of(context).pop(),
            ),
            title: const Text('Detalle del Ticket',
                style: TextStyle(fontSize: 20, fontWeight: FontWeight.w600)),
            actions: [
              PopupMenuButton<String>(
                tooltip: 'Más opciones',
                color: kSurface,
                onSelected: _onMenu,
                itemBuilder: (_) => [
                  if (!t.completed) ...[
                    for (final s in const [
                      TicketStatus.nuevo,
                      TicketStatus.enProceso,
                      TicketStatus.enEspera,
                    ])
                      PopupMenuItem(
                        value: s.name,
                        enabled: t.status != s,
                        child: Text('Marcar como ${s.label}'),
                      ),
                  ],
                  const PopupMenuItem(
                      value: 'delete', child: Text('Eliminar ticket')),
                ],
              ),
            ],
            bottom: const PreferredSize(
              preferredSize: Size.fromHeight(1),
              child: Divider(height: 1, color: kBorder),
            ),
          ),
          body: SingleChildScrollView(
            padding: const EdgeInsets.fromLTRB(16, 18, 16, 16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _codeRow(t),
                const SizedBox(height: 18),
                _infoRow(Icons.person_outline, 'Cliente', t.client),
                const SizedBox(height: 18),
                _infoRow(Icons.calendar_today_outlined, 'Fecha de creación',
                    t.dateLabel),
                const SizedBox(height: 18),
                _faultBox(t),
                const SizedBox(height: 18),
                _deviceField(t),
                const SizedBox(height: 18),
                _classifyList(),
                const SizedBox(height: 18),
                _notesField(),
              ],
            ),
          ),
          bottomNavigationBar: SafeArea(
            child: Padding(
              padding: const EdgeInsets.fromLTRB(16, 8, 16, 16),
              child: SizedBox(
                height: 54,
                child: t.completed
                    ? OutlinedButton.icon(
                        onPressed: _reopen,
                        style: OutlinedButton.styleFrom(
                          foregroundColor: kText,
                          side: const BorderSide(color: kRed, width: 1.5),
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(10)),
                          textStyle: const TextStyle(
                              fontSize: 16, fontWeight: FontWeight.w600),
                        ),
                        icon: const Icon(Icons.refresh),
                        label: const Text('Reabrir ticket'),
                      )
                    : FilledButton.icon(
                        onPressed: _complete,
                        style: FilledButton.styleFrom(
                          backgroundColor: kRed,
                          foregroundColor: Colors.white,
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(10)),
                          textStyle: const TextStyle(
                              fontSize: 16, fontWeight: FontWeight.w600),
                        ),
                        icon: const Icon(Icons.check),
                        label: const Text('Cerrar / Completar Ticket'),
                      ),
              ),
            ),
          ),
        );
      },
    );
  }

  Widget _codeRow(Ticket t) {
    return Row(
      children: [
        const Icon(Icons.confirmation_number_outlined, size: 34, color: kRed),
        const SizedBox(width: 14),
        Expanded(
          child: Text(t.code,
              style: const TextStyle(
                  fontSize: 32, fontWeight: FontWeight.w800, color: kRedText)),
        ),
        StatusBadge(status: t.status, large: true),
      ],
    );
  }

  Widget _infoRow(IconData icon, String label, String value) {
    return Row(
      children: [
        Icon(icon, size: 30, color: kMuted),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(label, style: const TextStyle(fontSize: 14, color: kMuted)),
              Text(value,
                  style: const TextStyle(
                      fontSize: 17, fontWeight: FontWeight.w500)),
            ],
          ),
        ),
      ],
    );
  }

  Widget _faultBox(Ticket t) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
      decoration: BoxDecoration(
        color: const Color(0xFF2A0C0F),
        border: Border.all(color: kRedDark),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Padding(
            padding: EdgeInsets.only(top: 1),
            child: Icon(Icons.sms_outlined, size: 22, color: kRedText),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Falla reportada',
                    style: TextStyle(
                        fontSize: 14,
                        fontWeight: FontWeight.w600,
                        color: kRedText)),
                const SizedBox(height: 4),
                Text(t.fault,
                    style: const TextStyle(
                        fontSize: 16,
                        height: 1.4,
                        color: Color(0xFFE6E6E6))),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _deviceField(Ticket t) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Padding(
          padding: EdgeInsets.only(top: 2),
          child: Icon(Icons.smartphone_outlined, size: 30, color: kMuted),
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('Equipo',
                  style: TextStyle(fontSize: 15, fontWeight: FontWeight.w600)),
              const SizedBox(height: 8),
              Container(
                constraints: const BoxConstraints(minHeight: 48),
                padding: const EdgeInsets.symmetric(horizontal: 16),
                decoration: BoxDecoration(
                  color: kSurface,
                  border: Border.all(color: kRed, width: 1.5),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Row(
                  children: [
                    Expanded(
                        child:
                            Text(t.device, style: const TextStyle(fontSize: 16))),
                    const Icon(Icons.keyboard_arrow_down),
                  ],
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _classifyList() {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Padding(
          padding: EdgeInsets.only(top: 2),
          child: Icon(Icons.search, size: 30),
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('Clasificar equipo',
                  style: TextStyle(fontSize: 15, fontWeight: FontWeight.w600)),
              const SizedBox(height: 8),
              Container(
                decoration: BoxDecoration(
                  color: kSurface,
                  border: Border.all(color: const Color(0xFF2B3036)),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Column(
                  children: [
                    for (final o in kDeviceOptions) _optionRow(o),
                  ],
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _optionRow(DeviceOption o) {
    final on = _highlight == o.label;
    return InkWell(
      borderRadius: BorderRadius.circular(8),
      onTap: () => _pick(o.label),
      child: Container(
        constraints: const BoxConstraints(minHeight: 46),
        padding: const EdgeInsets.symmetric(horizontal: 14),
        decoration: BoxDecoration(
          border: Border.all(color: on ? kRed : Colors.transparent),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Row(
          children: [
            Icon(o.icon, size: 22, color: const Color(0xFFC9CDD3)),
            const SizedBox(width: 14),
            Expanded(
                child: Text(o.label, style: const TextStyle(fontSize: 15))),
            if (on) const Icon(Icons.chevron_right, size: 20, color: kMuted),
          ],
        ),
      ),
    );
  }

  Widget _notesField() {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Padding(
          padding: EdgeInsets.only(top: 2),
          child: Icon(Icons.edit_outlined, size: 30, color: kMuted),
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('Notas internas (opcional)',
                  style: TextStyle(fontSize: 15, color: Color(0xFFC9CDD3))),
              const SizedBox(height: 8),
              TextField(
                controller: _notes,
                maxLength: 200,
                maxLines: 3,
                onChanged: (v) => widget.store.setNotes(widget.ticketId, v),
                style: const TextStyle(fontSize: 15),
                buildCounter: (context,
                        {required currentLength,
                        required isFocused,
                        required maxLength}) =>
                    Text('$currentLength/$maxLength',
                        style: const TextStyle(fontSize: 12, color: kMuted)),
                decoration: InputDecoration(
                  hintText: 'Agregar alguna observación...',
                  hintStyle: const TextStyle(color: kMuted),
                  filled: true,
                  fillColor: kSurface,
                  contentPadding: const EdgeInsets.all(14),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: const BorderSide(color: Color(0xFF2B3036)),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: const BorderSide(color: kRedText, width: 1.5),
                  ),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}
