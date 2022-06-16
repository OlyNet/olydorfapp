import 'dart:typed_data';

import 'package:appwrite/appwrite.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/models/event_model.dart';

class EventsState extends StateNotifier<List<Event>> {
  final Client client;
  late Database database;
  late Storage storage;

  EventsState(this.client) : super([]) {
    database = Database(client);
    storage = Storage(client);
  }

  Future<void> getEvents() async {
    try {
      final res = await database.listDocuments(collectionId: 'events');

      List<Event> events =
          res.documents.map((doc) => Event.fromMap(doc.data)).toList();
      print(events);
      for (Event event in events) {
        if (event.imgId != null) {
          event.image = await _getEventImage(event.imgId!);
        }
      }
      print(events);
      state = events;
    } on AppwriteException catch (e) {
      print(e.message);
    }
  }

  Future<Uint8List> _getEventImage(String imgId) async {
    try {
      return await storage.getFilePreview(fileId: imgId);
    } on AppwriteException {
      rethrow;
    }
  }

  Future<void> createEvent(Event event) async {
    try {
      await database.createDocument(
          collectionId: 'events',
          documentId: "unique()",
          data: {
            'name': event.name,
          },
          read: [
            'role:all',
          ]);
    } on AppwriteException catch (e) {
      print(e.message);
    }
  }
}
