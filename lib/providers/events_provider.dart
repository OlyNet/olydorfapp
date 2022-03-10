import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/global/consts.dart';
import 'package:olydorf/models/event_model.dart';
import 'package:appwrite/appwrite.dart';

final eventsListProvider = FutureProvider<List<Event>>((ref) async {
  try {
    Client client = Client(endPoint: AppwriteConfig.endpointUrl);
    client.setProject(AppwriteConfig.projectId).setSelfSigned();
    Database database = Database(client);
    final res = await database.listDocuments(collectionId: 'events');

    return res.documents.map((doc) => Event.fromMap(doc.data)).toList();
  } on AppwriteException catch (e) {
    print(e.message);
  }
  return [];
});
