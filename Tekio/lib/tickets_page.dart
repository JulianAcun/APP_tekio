import 'package:flutter/material.dart';

import 'detail_screen.dart';
import 'theme.dart';
import 'ticket_store.dart';
import 'widgets.dart';

/// Pestaña "Tickets": cabecera, buscador, pestañas Activos/Completados y lista.
class TicketsPage extends StatefulWidget {
  const TicketsPage({super.key, required this.store});

  final TicketStore store;

  @override
  State<TicketsPage> createState() => _TicketsPageState();
}

class _TicketsPageState extends State<TicketsPage> {
  final TextEditingController _search = TextEditingController();
  bool _searching = false;

  @override
  void dispose() {
    _search.dispose();
    super.dispose();
  }

  void _closeSearch() {
    _search.clear();
    widget.store.setQuery('');
    setState(() => _searching = false);
  }

  @override
  Widget build(BuildContext context) {
    final store = widget.store;
    return ListenableBuilder(
      listenable: store,
      builder: (context, _) {
        final tickets = store.visible;
        return Column(
          children: [
            _searching ? _searchBar() : _header(),
            _Tabs(store: store),
            Expanded(
              child: tickets.isEmpty
                  ? EmptyState(
                      icon: store.query.isEmpty
                          ? Icons.inbox_outlined
                          : Icons.search_off,
                      text: store.query.isEmpty
                          ? (store.showCompleted
                              ? 'Aún no hay tickets completados'
                              : 'No hay tickets activos.\nToca + para crear uno.')
                          : 'Sin resultados para "${store.query}"',
                    )
                  : ListView.separated(
                      padding: const EdgeInsets.fromLTRB(12, 12, 12, 96),
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

  Widget _header() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 12, 4, 12),
      child: Row(
        children: [
          Container(
            width: 46,
            height: 46,
            alignment: Alignment.center,
            decoration: BoxDecoration(
              color: kRed,
              borderRadius: BorderRadius.circular(12),
            ),
            child: const Text(
              'T',
              style: TextStyle(
                  color: Colors.white, fontSize: 28, fontWeight: FontWeight.w900),
            ),
          ),
          const SizedBox(width: 12),
          const Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('TEKIO',
                    style: TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.w800,
                        letterSpacing: 3,
                        height: 1.1)),
                SizedBox(height: 2),
                Text('Gestión de Tickets',
                    style: TextStyle(fontSize: 13, color: kMuted)),
              ],
            ),
          ),
          IconButton(
            onPressed: () => setState(() => _searching = true),
            tooltip: 'Buscar',
            icon: const Icon(Icons.search),
          ),
          PopupMenuButton<String>(
            tooltip: 'Más opciones',
            color: kSurface,
            onSelected: (value) {
              if (value == 'reset') confirmReset(context, widget.store);
              if (value == 'about') {
                showAboutDialog(
                  context: context,
                  applicationName: 'Tekio',
                  applicationVersion: '1.0.0',
                  children: const [Text('Gestión de tickets de soporte.')],
                );
              }
            },
            itemBuilder: (_) => const [
              PopupMenuItem(value: 'reset', child: Text('Restablecer datos')),
              PopupMenuItem(value: 'about', child: Text('Acerca de Tekio')),
            ],
          ),
        ],
      ),
    );
  }

  Widget _searchBar() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(4, 12, 12, 12),
      child: Row(
        children: [
          IconButton(
            onPressed: _closeSearch,
            tooltip: 'Cerrar búsqueda',
            icon: const Icon(Icons.arrow_back),
          ),
          Expanded(
            child: TextField(
              controller: _search,
              autofocus: true,
              onChanged: widget.store.setQuery,
              textInputAction: TextInputAction.search,
              decoration: InputDecoration(
                hintText: 'Buscar por cliente, #número o falla',
                hintStyle: const TextStyle(color: kMuted),
                filled: true,
                fillColor: kSurface,
                prefixIcon: const Icon(Icons.search, color: kMuted),
                contentPadding: const EdgeInsets.symmetric(vertical: 12),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(10),
                  borderSide: const BorderSide(color: kBorder),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(10),
                  borderSide: const BorderSide(color: kRedText, width: 1.5),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _Tabs extends StatelessWidget {
  const _Tabs({required this.store});

  final TicketStore store;

  @override
  Widget build(BuildContext context) {
    final done = store.showCompleted;
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 12),
      padding: const EdgeInsets.all(4),
      decoration: BoxDecoration(
        color: kSurface,
        border: Border.all(color: kBorder),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Row(
        children: [
          Expanded(
            child: _TabButton(
              selected: !done,
              onTap: () => store.setShowCompleted(false),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Text('Tickets Activos'),
                  const SizedBox(width: 8),
                  Container(
                    constraints: const BoxConstraints(minWidth: 22),
                    height: 22,
                    alignment: Alignment.center,
                    decoration: BoxDecoration(
                      color: Colors.black.withOpacity(0.55),
                      borderRadius: BorderRadius.circular(11),
                    ),
                    child: Text('${store.activeCount}',
                        style: const TextStyle(
                            fontSize: 12,
                            color: Colors.white,
                            fontWeight: FontWeight.w700)),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(width: 6),
          Expanded(
            child: _TabButton(
              selected: done,
              onTap: () => store.setShowCompleted(true),
              child: const Text('Completados'),
            ),
          ),
        ],
      ),
    );
  }
}

class _TabButton extends StatelessWidget {
  const _TabButton(
      {required this.selected, required this.onTap, required this.child});

  final bool selected;
  final VoidCallback onTap;
  final Widget child;

  @override
  Widget build(BuildContext context) {
    return Material(
      color: selected ? kRed : Colors.transparent,
      borderRadius: BorderRadius.circular(8),
      child: InkWell(
        borderRadius: BorderRadius.circular(8),
        onTap: onTap,
        child: SizedBox(
          height: 44,
          child: DefaultTextStyle(
            style: TextStyle(
              fontSize: 14,
              fontWeight: FontWeight.w600,
              color: selected ? Colors.white : kMuted,
            ),
            child: Center(child: child),
          ),
        ),
      ),
    );
  }
}
