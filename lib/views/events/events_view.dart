import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/providers/events_provider.dart';
import 'package:olydorf/views/events/event_card.dart';

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
            EventCard(event: events[i]),
          ],
        ]),
      ),
    );
  }
}
