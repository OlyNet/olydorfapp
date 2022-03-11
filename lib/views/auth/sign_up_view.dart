import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/global/consts.dart';
import 'package:olydorf/providers/auth_provider.dart';

class SignUpView extends HookConsumerWidget {
  SignUpView({Key? key}) : super(key: key);

  final GlobalKey<FormState> _formKey = GlobalKey();

  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _nameController = TextEditingController();

  bool _loading = false;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      appBar: AppBar(toolbarHeight: 0),
      body: Form(
          key: _formKey,
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
                controller: _nameController,
                decoration: const InputDecoration(hintText: "name"),
                validator: (text) {
                  if (text!.isEmpty || text.length < 3) {
                    return "name too short (min 3)";
                  }
                },
              ),
              TextFormField(
                controller: _passwordController,
                obscureText: true,
                decoration: const InputDecoration(hintText: "Password"),
                validator: (text) {
                  if (text!.isEmpty || text.length < 8) {
                    return "Passwort too short (min 8)";
                  }
                },
              ),
              TextFormField(
                obscureText: true,
                decoration: const InputDecoration(hintText: "Password"),
                validator: (text) {
                  if (text != _passwordController.text) {
                    return "Passwords don't match";
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
                        await ref.read(authProvider.notifier).signUp(
                              _emailController.text,
                              _passwordController.text,
                              _nameController.text,
                              context,
                            );
                        _loading = false;
                      }),
                      child: const Text("sign up")),
              TextButton(
                  onPressed: () =>
                      Navigator.of(context).pushReplacementNamed(Routes.login),
                  child: const Text('login'))
            ],
          )),
    );
  }
}
