import 'dart:developer';
import 'dart:typed_data';

import 'package:appwrite/appwrite.dart';
import 'package:appwrite/models.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:image_picker/image_picker.dart';
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
      log(events.toString());
      for (Event event in events) {
        if (event.imgId != null) {
          event.image = await _getEventImage(event.imgId!);
        }
      }
      log(events.toString());
      state = events;
    } on AppwriteException catch (e) {
      log(e.message.toString());
    }
  }

  Future<Uint8List> _getEventImage(String imgId) async {
    try {
      return await storage.getFilePreview(fileId: imgId);
    } on AppwriteException {
      rethrow;
    }
  }

  Future<String?> uploadEventPicture(XFile file, String imgName) async {
    try {
      File? result = await storage.createFile(
        file: MultipartFile.fromBytes('file', await file.readAsBytes(),
            filename: imgName),
        fileId: 'unique()',
        read: ['role:all'],
      );
      return result.$id;
    } catch (e) {
      log('$e');
      rethrow;
    }
  }

  Future<void> createEvent(Event event) async {
    try {
      await database.createDocument(
          collectionId: 'events',
          documentId: "unique()",
          data: event.toMap(),
          read: [
            'role:all',
          ]);
    } on AppwriteException catch (e) {
      log(e.message.toString());
    }
  }
}
