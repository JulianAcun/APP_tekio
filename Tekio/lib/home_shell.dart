import 'package:flutter/material.dart';

import 'history_page.dart';
import 'new_ticket_screen.dart';
import 'profile_page.dart';
import 'theme.dart';
import 'ticket_store.dart';
import 'tickets_page.dart';

/// Estructura principal: barra inferior con Tickets, Historial y Perfil.
class HomeShell extends StatefulWidget {
  const HomeShell({super.key, required this.store});

  final TicketStore store;

  @override
  State<HomeShell> createState() => _HomeShellState();
}

class _HomeShellState extends State<HomeShell> {
  int _index = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: IndexedStack(
          index: _index,
          children: [
            TicketsPage(store: widget.store),
            HistoryPage(store: widget.store),
            ProfilePage(store: widget.store),
          ],
        ),
      ),
      floatingActionButton: _index == 0
          ? FloatingActionButton(
              onPressed: () => Navigator.of(context).push(
                MaterialPageRoute(
                  builder: (_) => NewTicketScreen(store: widget.store),
                ),
              ),
              backgroundColor: kRed,
              foregroundColor: Colors.white,
              shape: const CircleBorder(),
              tooltip: 'Nuevo ticket',
              child: const Icon(Icons.add, size: 28),
            )
          : null,
      bottomNavigationBar: NavigationBar(
        selectedIndex: _index,
        onDestinationSelected: (i) => setState(() => _index = i),
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.confirmation_number_outlined),
            label: 'Tickets',
          ),
          NavigationDestination(icon: Icon(Icons.history), label: 'Historial'),
          NavigationDestination(
            icon: Icon(Icons.person_outline),
            label: 'Perfil',
          ),
        ],
      ),
    );
  }
}
