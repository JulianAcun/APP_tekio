import 'package:flutter/material.dart';

import 'models.dart';
import 'theme.dart';
import 'ticket_store.dart';

/// Formulario para crear un ticket nuevo (botón "+").
class NewTicketScreen extends StatefulWidget {
  const NewTicketScreen({super.key, required this.store});

  final TicketStore store;

  @override
  State<NewTicketScreen> createState() => _NewTicketScreenState();
}

class _NewTicketScreenState extends State<NewTicketScreen> {
  final _form = GlobalKey<FormState>();
  final _client = TextEditingController();
  final _fault = TextEditingController();
  String _device = kPendiente;

  @override
  void dispose() {
    _client.dispose();
    _fault.dispose();
    super.dispose();
  }

  void _save() {
    if (!_form.currentState!.validate()) return;
    final ticket = widget.store.add(
      client: _client.text.trim(),
      fault: _fault.text.trim(),
      device: _device,
    );
    final messenger = ScaffoldMessenger.of(context);
    Navigator.of(context).pop();
    messenger.showSnackBar(
      SnackBar(content: Text('Ticket ${ticket.code} creado')),
    );
  }

  InputDecoration _decoration(String label, {String? hint}) {
    OutlineInputBorder border(Color c, [double w = 1]) => OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
          borderSide: BorderSide(color: c, width: w),
        );
    return InputDecoration(
      labelText: label,
      hintText: hint,
      labelStyle: const TextStyle(color: kMuted),
      hintStyle: const TextStyle(color: kMuted),
      filled: true,
      fillColor: kSurface,
      contentPadding: const EdgeInsets.all(14),
      enabledBorder: border(const Color(0xFF2B3036)),
      focusedBorder: border(kRedText, 1.5),
      errorBorder: border(kRedText),
      focusedErrorBorder: border(kRedText, 1.5),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: kBg,
        surfaceTintColor: Colors.transparent,
        title: const Text('Nuevo ticket',
            style: TextStyle(fontSize: 20, fontWeight: FontWeight.w600)),
        bottom: const PreferredSize(
          preferredSize: Size.fromHeight(1),
          child: Divider(height: 1, color: kBorder),
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _form,
          child: Column(
            children: [
              TextFormField(
                controller: _client,
                textCapitalization: TextCapitalization.words,
                textInputAction: TextInputAction.next,
                decoration: _decoration('Cliente', hint: 'Nombre del cliente'),
                validator: (v) => (v == null || v.trim().isEmpty)
                    ? 'Escribe el nombre del cliente'
                    : null,
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _fault,
                maxLines: 4,
                maxLength: 300,
                textCapitalization: TextCapitalization.sentences,
                decoration: _decoration('Falla reportada',
                    hint: 'Describe qué le pasa al equipo'),
                validator: (v) => (v == null || v.trim().isEmpty)
                    ? 'Describe la falla'
                    : null,
              ),
              const SizedBox(height: 8),
              DropdownButtonFormField<String>(
                value: _device,
                dropdownColor: kSurface,
                decoration: _decoration('Equipo'),
                items: [
                  const DropdownMenuItem(
                      value: kPendiente, child: Text(kPendiente)),
                  for (final o in kDeviceOptions)
                    DropdownMenuItem(value: o.label, child: Text(o.label)),
                ],
                onChanged: (v) => setState(() => _device = v ?? kPendiente),
              ),
            ],
          ),
        ),
      ),
      bottomNavigationBar: SafeArea(
        child: Padding(
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 16),
          child: SizedBox(
            height: 54,
            child: FilledButton.icon(
              onPressed: _save,
              style: FilledButton.styleFrom(
                backgroundColor: kRed,
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10)),
                textStyle:
                    const TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
              ),
              icon: const Icon(Icons.add),
              label: const Text('Crear ticket'),
            ),
          ),
        ),
      ),
    );
  }
}
