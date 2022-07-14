import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/global/consts.dart';
import 'package:olydorf/models/user_model.dart';
import 'package:olydorf/providers/auth_provider.dart';
import 'package:olydorf/views/settings/settings_view.dart';

class ProfileView extends HookConsumerWidget {
  const ProfileView({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final AppUser? currentUser = ref.watch(authProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text("Profile"),
        backgroundColor: Colors.transparent,
        elevation: 0,
        actions: [
          IconButton(
              onPressed: () {
                Navigator.of(context).push(MaterialPageRoute(
                    builder: (context) => const SettingsView()));
              },
              icon: const Icon(Icons.settings))
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(children: [
          if (currentUser == null) ...[
            ElevatedButton(
                onPressed: () => Navigator.of(context).pushNamed(Routes.login),
                child: const Text("login"))
          ] else ...[
            Card(
              child: ListTile(
                title: Text("Hello ${currentUser.name}!"),
                subtitle: Text(currentUser.email),
                leading: const Icon(Icons.person),
                trailing: IconButton(
                  icon: const Icon(Icons.edit),
                  onPressed: () {
                    Navigator.of(context).pushNamed(Routes.editProfile);
                  },
                ),
              ),
            ),
            ElevatedButton(
                onPressed: () =>
                    ref.read(authProvider.notifier).logout(context),
                child: const Text("logout"))
          ],
        ]),
      ),
    );
  }
}
