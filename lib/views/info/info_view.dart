import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/global/consts.dart';
import 'package:olydorf/providers/auth_provider.dart';

class InfoView extends HookConsumerWidget {
  const InfoView({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final auth = ref.watch(authProvider);

    final currentUser = ref.watch(currentUserProvider);

    return Scaffold(
      appBar: AppBar(toolbarHeight: 0),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(children: [
          const Text("Home"),
          if (currentUser == null) ...[
            ElevatedButton(
                onPressed: () =>
                    Navigator.of(context).pushReplacementNamed(Routes.login),
                child: const Text("login"))
          ] else ...[
            Text(currentUser.name),
            Text(currentUser.email),
            ElevatedButton(
                onPressed: () => auth.logout(context),
                child: const Text("logout"))
          ],
        ]),
      ),
    );
  }
}
