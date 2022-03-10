import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:introduction_screen/introduction_screen.dart';
import 'package:olydorf/views/bottom_navigation_bar/bottom_navigation_bar_view.dart';

class WelcomeView extends HookConsumerWidget {
  const WelcomeView({Key? key}) : super(key: key);
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return IntroductionScreen(
      isTopSafeArea: true,
      showDoneButton: true,
      done: const Text('Done'),
      onDone: () => Navigator.of(context).pushReplacement(MaterialPageRoute(
          builder: ((context) => const BottomNavigationBarView()))),
      next: const Text('Next'),
      skip: const Text('Skip'),
      showSkipButton: true,
      showNextButton: true,
      onSkip: () => Navigator.of(context).pushReplacement(MaterialPageRoute(
          builder: ((context) => const BottomNavigationBarView()))),
      pages: [
        PageViewModel(
          image: const Icon(
            Icons.face,
            size: 150,
          ),
          body: "The App for Olydorf residents",
          title: "Welcome to Olydorf App",
        ),
        PageViewModel(
          image: const Icon(
            Icons.chat_bubble,
            size: 150,
          ),
          body: "Connect with your neighbors",
          title: "Chat with your alley",
        ),
        PageViewModel(
          image: const Icon(
            Icons.people,
            size: 150,
          ),
          body: "Find Events",
          title: "BBQ events & the next big party",
        ),
      ],
    );
  }
}
