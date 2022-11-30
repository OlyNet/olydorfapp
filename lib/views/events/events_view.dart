import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/models/user_model.dart';
import 'package:olydorf/providers/auth_provider.dart';
import 'package:olydorf/providers/events_provider.dart';
import 'package:olydorf/views/events/create_event_view.dart';
import 'package:olydorf/views/events/events_calendar.dart';

class EventsView extends HookConsumerWidget {
  const EventsView({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final events = ref.watch(eventsListProvider);
    final AppUser? currentUser = ref.watch(authProvider);
    return Scaffold(
      appBar: AppBar(
        title: const Text("Events"),
        backgroundColor: Colors.transparent,
        elevation: 0,
      ),
      body: RefreshIndicator(
        onRefresh: () => ref.read(eventsListProvider.notifier).getEvents(),
        child: EventsCalendar(events: events),
      ),
      floatingActionButton:
          currentUser?.teams.map(((e) => e.$id)).contains('admin') ?? false
              ? FloatingActionButton(
                  onPressed: () => Navigator.of(context).push(MaterialPageRoute(
                      builder: (context) => const CreateEventView())),
                  child: const Icon(Icons.add),
                )
              : Container(),
    );
  }
}
