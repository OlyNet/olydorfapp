import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/api/auth.dart';
import 'package:olydorf/global/consts.dart';
import 'package:olydorf/providers/auth_provider.dart';

class LoginView extends HookConsumerWidget {
  LoginView({Key? key}) : super(key: key);

  final GlobalKey<FormState> _formKey = GlobalKey();

  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();

  bool _loading = false;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final Auth auth = ref.watch(authProvider);

    return Scaffold(
      appBar: AppBar(toolbarHeight: 0),
      body: Form(
          child: Column(
        children: [
          TextFormField(
            controller: _emailController,
            keyboardType: TextInputType.emailAddress,
            decoration: const InputDecoration(hintText: "Email"),
            validator: (text) {
              if (text!.isEmpty || !text.contains('@')) {
                return "Invalid email";
              }
            },
          ),
          TextFormField(
            controller: _passwordController,
            obscureText: true,
            decoration: const InputDecoration(hintText: "Password"),
            validator: (text) {
              if (text!.isEmpty || text.length < 8) {
                return "Passwort too short";
              }
            },
          ),
          _loading
              ? const Center(child: CircularProgressIndicator())
              : ElevatedButton(
                  onPressed: (() async {
                    if (!_formKey.currentState!.validate()) {
                      return;
                    }
                    _loading = true;
                    await auth.login(_emailController.text,
                        _passwordController.text, context);
                    _loading = false;
                  }),
                  child: const Text("login")),
          TextButton(
              onPressed: () =>
                  Navigator.of(context).pushReplacementNamed(Routes.signUp),
              child: const Text('sign up'))
        ],
      )),
    );
  }
}
