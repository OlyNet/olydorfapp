import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';

class InfoView extends HookConsumerWidget {
  const InfoView({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Info"),
        backgroundColor: Colors.transparent,
        elevation: 0,
      ),
      body: Container(),
    );
  }
}
