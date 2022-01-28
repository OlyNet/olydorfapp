import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/models/event_model.dart';
import 'package:appwrite/appwrite.dart';

final eventsListProvider = FutureProvider<List<Event>>((ref) async {
  try {
    Client client = Client(endPoint: 'http://appwrite.960.eu:5500/v1');
    client.setProject('61f3034d10e3bbe97441').setSelfSigned();
    Database database = Database(client);
    final res =
        await database.listDocuments(collectionId: '61f329309b6e2c4d7954');

    return res.documents.map((doc) => Event.fromMap(doc.data)).toList();
  } on AppwriteException catch (e) {
    print(e.message);
  }
  return [];
});
