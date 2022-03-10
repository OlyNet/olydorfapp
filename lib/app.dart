import 'package:flutter/material.dart';
import 'package:olydorf/views/bottom_navigation_bar/bottom_navigation_bar_view.dart';
import 'package:olydorf/views/welcome/welcome_view.dart';

class App extends StatelessWidget {
  const App({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: WelcomeView(),
      debugShowCheckedModeBanner: false,
    );
  }
}
