import 'package:appwrite/appwrite.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/global/consts.dart';
import 'package:olydorf/models/event_model.dart';

class EventsState extends StateNotifier<List<Event>> {
  final Client client;
  late Database database;

  EventsState(this.client) : super([]) {
    database = Database(client);
  }

  Future<void> getEvents() async {
    try {
      final res = await database.listDocuments(collectionId: 'events');

      state = res.documents.map((doc) => Event.fromMap(doc.data)).toList();
    } on AppwriteException catch (e) {
      print(e.message);
    }
  }
}
