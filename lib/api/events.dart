import 'dart:developer';
import 'dart:typed_data';
import 'package:appwrite/models.dart' as appwriteModels;
import 'package:appwrite/appwrite.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:image_picker/image_picker.dart';
import 'package:olydorf/models/event_model.dart';

class EventsState extends StateNotifier<List<Event>> {
  final Client client;
  late Databases databases;
  late Storage storage;

  EventsState(this.client) : super([]) {
    databases = Databases(client);
    storage = Storage(client);
  }

  Future<void> getEvents() async {
    try {
      final res = await databases.listDocuments(
          databaseId: 'olydorf', collectionId: 'events');

      List<Event> events =
          res.documents.map((doc) => Event.fromMap(doc.data)).toList();
      for (Event event in events) {
        if (event.imgId != null) {
          event.image = await _getEventImage(event.imgId!);
        }
      }
      state = events;
    } on AppwriteException catch (e) {
      log(e.message.toString());
    }
  }

  Future<Uint8List> _getEventImage(String imgId) async {
    try {
      return await storage.getFilePreview(
          bucketId: 'eventImages', fileId: imgId);
    } on AppwriteException {
      rethrow;
    }
  }

  Future<String?> uploadEventPicture(XFile file, String imgName) async {
    try {
      appwriteModels.File? result = await storage.createFile(
        bucketId: 'eventImages',
        file: InputFile(bytes: await file.readAsBytes(), filename: imgName),
        fileId: 'unique()',
        permissions: [
          Permission.read(Role.any()),
        ],
      );
      return result.$id;
    } catch (e) {
      log('$e');
      rethrow;
    }
  }

  Future<void> createEvent(Event event) async {
    try {
      await databases.createDocument(
          databaseId: 'olydorf',
          collectionId: 'events',
          documentId: "unique()",
          data: event.toMap(),
          permissions: [
            Permission.read(Role.any()),
          ]);
    } on AppwriteException catch (e) {
      log(e.message.toString());
    }
  }
}
