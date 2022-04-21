import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/models/user_model.dart';
import 'package:olydorf/providers/auth_provider.dart';
import 'package:olydorf/providers/events_provider.dart';
import 'package:olydorf/views/events/event_card.dart';

class EventsView extends HookConsumerWidget {
  const EventsView({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final events = ref.watch(eventsListProvider);
    final AppUser? currentUser = ref.watch(authProvider);
    return Scaffold(
      body: RefreshIndicator(
        onRefresh: () => ref.read(eventsListProvider.notifier).getEvents(),
        child: ListView(children: [
          for (var i = 0; i < events.length; i++) ...[
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: EventCard(event: events[i]),
            ),
          ],
        ]),
      ),
      floatingActionButton:
          currentUser?.teams.map(((e) => e.$id)).contains('admin') ?? false
              ? FloatingActionButton(
                  onPressed: () {},
                  child: const Icon(Icons.add),
                )
              : Container(),
    );
  }
}
