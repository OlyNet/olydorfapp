import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/providers/theme_provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

class SettingsView extends StatefulHookConsumerWidget {
  const SettingsView({Key? key}) : super(key: key);

  @override
  ConsumerState<ConsumerStatefulWidget> createState() => _SettingsViewState();
}

class _SettingsViewState extends ConsumerState<SettingsView> {
  bool isDarkMode = true;

  @override
  void initState() {
    loadSettings();
    super.initState();
  }

  void loadSettings() async {
    final prefs = await SharedPreferences.getInstance();
    isDarkMode = prefs.getBool("isDarkMode") ?? true;
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(),
        body: Column(
          children: [
            SwitchListTile(
              value: isDarkMode,
              onChanged: (value) async {
                final prefs = await SharedPreferences.getInstance();
                await prefs.setBool("isDarkMode", value);
                ref.read(themeStateNotifier).setIsDarkMode(value);
                setState(() {
                  isDarkMode = value;
                });
              },
              title: const Text("Dark mode"),
            ),
          ],
        ));
  }
}
