import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/global/consts.dart';
import 'package:olydorf/providers/auth_provider.dart';
import 'package:olydorf/providers/events_provider.dart';
import 'package:olydorf/views/auth/login_view.dart';
import 'package:olydorf/views/auth/sign_up_view.dart';
import 'package:olydorf/views/bottom_navigation_bar/bottom_navigation_bar_view.dart';
import 'package:olydorf/views/profile/edit_profile_view.dart';
import 'package:olydorf/views/welcome/welcome_view.dart';

class App extends StatefulHookConsumerWidget {
  const App({Key? key}) : super(key: key);

  @override
  _AppState createState() => _AppState();
}

class _AppState extends ConsumerState<App> {
  final _navigatorKey = GlobalKey<NavigatorState>();

  Future<void> _init(WidgetRef ref) async {
    // load auth state
    ref.read(authProvider.notifier).getCurrentUser();

    // load events
    ref.read(eventsListProvider.notifier).getEvents();
  }

  @override
  void initState() {
    super.initState();
    _init(ref);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: const WelcomeView(),
      navigatorKey: _navigatorKey,
      debugShowCheckedModeBanner: false,
      routes: {
        Routes.login: (context) => LoginView(),
        Routes.signUp: (context) => SignUpView(),
        Routes.editProfile: (context) => EditProfileView(),
        Routes.bottomNavigationBar: (context) =>
            const BottomNavigationBarView(),
      },
    );
  }
}
