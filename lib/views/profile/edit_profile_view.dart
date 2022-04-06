import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/api/profile_helper.dart';

class EditProfileView extends HookConsumerWidget {
  EditProfileView({Key? key}) : super(key: key);

  final bungalowController = TextEditingController();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Edit profile"),
      ),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(children: [
          TextFormField(
            controller: bungalowController,
            decoration: const InputDecoration(
              labelText: "Bungalow address",
              hintText: "A01",
              border: OutlineInputBorder(),
            ),
          ),
          ElevatedButton(
              onPressed: () => ProfileHelper.saveBungalow(
                    ref,
                    bungalowController
                        .text, // TODO security !!! :) restrict creation of new documents
                  ),
              child: const Text("save")),
        ]),
      ),
    );
  }
}
