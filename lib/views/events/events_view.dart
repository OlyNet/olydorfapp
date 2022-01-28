import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/providers/events_provider.dart';

class EventsView extends HookConsumerWidget {
  const EventsView({Key? key}) : super(key: key);
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final eventsProv = ref.watch(eventsListProvider);
    return Scaffold(
        body: eventsProv.when(
            loading: () => const Center(child: CircularProgressIndicator()),
            error: (err, stack) => Text('Error: $err'),
            data: (events) {
              return ListView.builder(
                  itemCount: events.length,
                  itemBuilder: (BuildContext context, int index) {
                    return ListTile(
                      title: Text(events[index].name),
                    );
                  });
            }));
  }
}
