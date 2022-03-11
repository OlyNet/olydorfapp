import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/api/events.dart';
import 'package:olydorf/providers/auth_provider.dart';
import 'package:olydorf/providers/events_provider.dart';

class EventsView extends HookConsumerWidget {
  const EventsView({Key? key}) : super(key: key);
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final events = ref.watch(eventsListProvider);
    return Scaffold(
      body: RefreshIndicator(
        onRefresh: () => ref.read(eventsListProvider.notifier).getEvents(),
        child: ListView(children: [
          for (var i = 0; i < events.length; i++) ...[
            ListTile(
              title: Text(events[i].name),
            ),
          ],
        ]),
      ),
    );
  }
}
