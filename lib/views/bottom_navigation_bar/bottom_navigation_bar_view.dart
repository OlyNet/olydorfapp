import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/views/events/events_view.dart';
import 'package:olydorf/views/info/info_view.dart';

import '../../providers/bottom_navigation_bar_provider.dart';

class BottomNavigationBarView extends HookConsumerWidget {
  const BottomNavigationBarView({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final int pageIndex = ref.watch(pageIndexProvider);

    return Scaffold(
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        currentIndex: pageIndex,
        onTap: (index) {
          ref.read(pageIndexProvider.state).state = index;
        },
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.info), label: 'Info'),
          BottomNavigationBarItem(icon: Icon(Icons.event), label: 'Events'),
          // BottomNavigationBarItem(icon: Icon(Icons.map), label: 'Map'),
        ],
      ),
      body: IndexedStack(
        index: pageIndex,
        children: const [
          InfoView(),
          EventsView(),
          // MapView(),
        ],
      ),
    );
  }
}
