import 'package:appwrite/appwrite.dart';
import 'package:appwrite/models.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/models/user_model.dart';
import 'package:olydorf/providers/auth_provider.dart';

class ProfileHelper {
  static void saveBungalow(WidgetRef ref, String id) async {
    Client client = ref.watch(clientProvider);
    Database database = Database(client);
    Account account = Account(client);
    User res = await account.get();
    try {
      await database
          .createDocument(collectionId: 'bungalows', documentId: id, data: {
        'current': res.$id,
      }, read: [
        'role:all',
      ]);
    } on AppwriteException catch (e) {
      print(e.message);
    }
  }

  static Future<AppUser?> getBungalow(WidgetRef ref, String id) async {
    Client client = ref.watch(clientProvider);
    Database database = Database(client);
    try {
      Document result = await database.getDocument(
        collectionId: 'bungalows',
        documentId: id,
      );
      String userId = result.data['current'];
      Document userResult =
          await database.getDocument(collectionId: 'users', documentId: userId);

      return AppUser.fromMap(userResult.data);
    } on AppwriteException catch (e) {
      print(e.message);
    }
    return null;
  }
}
