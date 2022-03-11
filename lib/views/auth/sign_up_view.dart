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
      appBar: AppBar(
        title: const Text("Sign up"),
      ),
      body: Form(
          key: _formKey,
          child: Center(
            child: SingleChildScrollView(
              child: Padding(
                padding: const EdgeInsets.all(38.0),
                child: Column(
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
                      controller: _nameController,
                      decoration: const InputDecoration(
                        hintText: "Your Name",
                        labelText: "Name",
                        border: OutlineInputBorder(),
                      ),
                      validator: (text) {
                        if (text!.isEmpty || text.length < 3) {
                          return "name too short (min 3)";
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
                          return "Passwort too short (min 8)";
                        }
                      },
                    ),
                    const SizedBox(
                      height: 8,
                    ),
                    TextFormField(
                      obscureText: true,
                      decoration: const InputDecoration(
                        labelText: "Repeat password",
                        hintText: "********",
                        border: OutlineInputBorder(),
                      ),
                      validator: (text) {
                        if (text != _passwordController.text) {
                          return "Passwords don't match";
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
                              await ref.read(authProvider.notifier).signUp(
                                    _emailController.text,
                                    _passwordController.text,
                                    _nameController.text,
                                    context,
                                  );
                              _loading = false;
                            }),
                            child: const Text("Sign up")),
                    TextButton(
                        onPressed: () => Navigator.of(context)
                            .pushReplacementNamed(Routes.login),
                        child: const Text('Login'))
                  ],
                ),
              ),
            ),
          )),
    );
  }
}
