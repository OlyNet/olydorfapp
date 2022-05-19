import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/api/profile_helper.dart';
import 'package:olydorf/models/user_model.dart';
import 'package:olydorf/providers/auth_provider.dart';

class EditProfileView extends HookConsumerWidget {
  EditProfileView({Key? key}) : super(key: key);

  final bungalowController = TextEditingController();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final AppUser? currentUser = ref.watch(authProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text("Edit profile"),
      ),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(children: [
          const Text("home:"),
          Text(currentUser != null ? currentUser.address : ""),
          TextFormField(
            controller: bungalowController,
            decoration: const InputDecoration(
              labelText: "Bungalow address",
              hintText: "A01",
              border: OutlineInputBorder(),
            ),
          ),
          ElevatedButton(
              onPressed: () async {
                String? error = await ProfileHelper.saveBungalow(
                  ref,
                  bungalowController.text,
                );

                if (error != null) {
                  showDialog(
                      context: context,
                      builder: (_) => AlertDialog(
                            title: const Text("Error"),
                            content: Text(error),
                          ));
                }
              },
              child: const Text("save")),
        ]),
      ),
    );
  }
}
