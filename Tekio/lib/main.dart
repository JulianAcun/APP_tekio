import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'home_shell.dart';
import 'theme.dart';
import 'ticket_store.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  SystemChrome.setSystemUIOverlayStyle(const SystemUiOverlayStyle(
    statusBarColor: Colors.transparent,
    statusBarIconBrightness: Brightness.light,
    systemNavigationBarColor: Color(0xFF101317),
    systemNavigationBarIconBrightness: Brightness.light,
  ));
  final store = await TicketStore.load();
  runApp(TekioApp(store: store));
}

class TekioApp extends StatelessWidget {
  const TekioApp({super.key, required this.store});

  final TicketStore store;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Tekio',
      debugShowCheckedModeBanner: false,
      theme: buildTekioTheme(),
      home: HomeShell(store: store),
    );
  }
}
