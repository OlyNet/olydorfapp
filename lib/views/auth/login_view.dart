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
    return Scaffold(
      appBar: AppBar(
        title: const Text("Login"),
      ),
      body: Form(
          key: _formKey,
          child: Center(
            child: SingleChildScrollView(
              child: Padding(
                padding: const EdgeInsets.all(38.0),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Icon(
                      Icons.account_circle,
                      size: 72,
                    ),
                    const SizedBox(
                      height: 8,
                    ),
                    TextFormField(
                      controller: _emailController,
                      keyboardType: TextInputType.emailAddress,
                      decoration: const InputDecoration(
                        hintText: "example@email.com",
                        labelText: "Email",
                        border: OutlineInputBorder(),
                      ),
                      validator: (text) {
                        if (text!.isEmpty || !text.contains('@')) {
                          return "Invalid email";
                        }
                      },
                    ),
                    const SizedBox(
                      height: 8,
                    ),
                    TextFormField(
                      controller: _passwordController,
                      obscureText: true,
                      decoration: const InputDecoration(
                        labelText: "Password",
                        hintText: "********",
                        border: OutlineInputBorder(),
                      ),
                      validator: (text) {
                        if (text!.isEmpty || text.length < 8) {
                          return "Passwort too short";
                        }
                      },
                    ),
                    const SizedBox(
                      height: 8,
                    ),
                    _loading
                        ? const Center(child: CircularProgressIndicator())
                        : ElevatedButton(
                            onPressed: (() async {
                              if (!_formKey.currentState!.validate()) {
                                return;
                              }
                              _loading = true;
                              await ref.read(authProvider.notifier).login(
                                  _emailController.text,
                                  _passwordController.text,
                                  context);
                              _loading = false;
                            }),
                            child: const Text("Login")),
                    ElevatedButton(
                        onPressed: () => ref
                            .read(authProvider.notifier)
                            .loginGoogle(context),
                        child: const Text("Login with Google")),
                    TextButton(
                        onPressed: () => Navigator.of(context)
                            .pushReplacementNamed(Routes.signUp),
                        child: const Text('Sign up'))
                  ],
                ),
              ),
            ),
          )),
    );
  }
}
