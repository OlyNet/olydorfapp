import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/api/events.dart';
import 'package:olydorf/models/event_model.dart';
import 'package:olydorf/providers/auth_provider.dart';

final eventsListProvider = StateNotifierProvider<EventsState, List<Event>>(
    (ref) => EventsState(ref.watch(clientProvider)));
