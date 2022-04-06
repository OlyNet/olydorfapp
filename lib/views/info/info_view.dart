import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/global/consts.dart';
import 'package:olydorf/models/user_model.dart';
import 'package:olydorf/providers/auth_provider.dart';

class InfoView extends HookConsumerWidget {
  const InfoView({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final AppUser? currentUser = ref.watch(authProvider);

    return Scaffold(
      appBar: AppBar(toolbarHeight: 0),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(children: [
          const Text("Home"),
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
