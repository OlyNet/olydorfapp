import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/global/consts.dart';
import 'package:olydorf/providers/auth_provider.dart';
import 'package:olydorf/views/auth/login_view.dart';
import 'package:olydorf/views/auth/sign_up_view.dart';
import 'package:olydorf/views/bottom_navigation_bar/bottom_navigation_bar_view.dart';
import 'package:olydorf/views/chat/chat_view.dart';
import 'package:olydorf/views/welcome/welcome_view.dart';

class App extends HookConsumerWidget {
  const App({Key? key}) : super(key: key);

  Future<void> _init(WidgetRef ref) async {
    final user = await ref.read(userProvider.future);
    if (user != null) {
      final userData = await ref.read(userDataClassProvider).getCurrentUser();
      ref.read(currentUserProvider.state).update((user) => user = userData);

      ref.read(userLoggedInProvider.state).state = true;
    } else {
      ref.read(userLoggedInProvider.state).state = false;
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    _init(ref);
    return MaterialApp(
      home: const WelcomeView(),
      debugShowCheckedModeBanner: false,
      routes: {
        Routes.login: (context) => LoginView(),
        Routes.signUp: (context) => SignUpView(),
        Routes.bottomNavigationBar: (context) =>
            const BottomNavigationBarView(),
      },
    );
  }
}
