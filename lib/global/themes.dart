import 'package:flutter/material.dart';

const Color accentColor = Color.fromRGBO(93, 117, 223, 1); // #5d75df
const Color primaryColor = Color.fromRGBO(136, 193, 123, 1); // #88c17b
const Color altColor = Color.fromRGBO(197, 219, 203, 1); // #c5dbcb

ThemeData lightTheme = ThemeData(
  brightness: Brightness.light,
  scaffoldBackgroundColor: altColor,
  toggleableActiveColor: accentColor,
  colorScheme: const ColorScheme.light()
      .copyWith(primary: primaryColor, secondary: accentColor),
);
ThemeData darkTheme = ThemeData(
  brightness: Brightness.dark,
  toggleableActiveColor: accentColor,
  colorScheme: const ColorScheme.dark()
      .copyWith(primary: primaryColor, secondary: accentColor),
);
