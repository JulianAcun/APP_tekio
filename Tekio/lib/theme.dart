import 'package:flutter/material.dart';

const kRed = Color(0xFFE50914);
const kRedText = Color(0xFFFF4D55); // rojo legible sobre fondo oscuro
const kRedDark = Color(0xFF8B0F14);
const kBg = Color(0xFF0B0D10);
const kSurface = Color(0xFF14171B);
const kBorder = Color(0xFF22262C);
const kMuted = Color(0xFF9AA0A6);
const kText = Color(0xFFF2F2F2);

ThemeData buildTekioTheme() {
  return ThemeData(
    useMaterial3: true,
    brightness: Brightness.dark,
    scaffoldBackgroundColor: kBg,
    colorScheme: const ColorScheme.dark(
      primary: kRed,
      onPrimary: Colors.white,
      surface: kSurface,
      onSurface: kText,
    ),
    navigationBarTheme: NavigationBarThemeData(
      backgroundColor: const Color(0xFF101317),
      indicatorColor: Colors.transparent,
      iconTheme: WidgetStateProperty.resolveWith((states) => IconThemeData(
            color: states.contains(WidgetState.selected) ? kRedText : kMuted,
          )),
      labelTextStyle: WidgetStateProperty.resolveWith((states) => TextStyle(
            fontSize: 12,
            fontWeight: FontWeight.w600,
            color: states.contains(WidgetState.selected) ? kRedText : kMuted,
          )),
    ),
  );
}
