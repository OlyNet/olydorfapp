import 'dart:developer';

import 'package:appwrite/appwrite.dart';
import 'package:appwrite/models.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/models/user_model.dart';
import 'package:olydorf/providers/auth_provider.dart';

class ProfileHelper {
  static Future<String?> saveBungalow(WidgetRef ref, String id) async {
    Client client = ref.watch(clientProvider);
    Database database = Database(client);
    Account account = Account(client);
    User res = await account.get();
    try {
      Document bungalow =
          await database.getDocument(collectionId: 'bungalows', documentId: id);
      final user = await account.get();
      Document userData = await database.getDocument(
          collectionId: 'users', documentId: user.$id);
      String? oldBungalowId = userData.data["address"];

      if (bungalow.data["current"] == null) {
        log(id.toString());
        await database
            .updateDocument(collectionId: 'bungalows', documentId: id, data: {
          'current': res.$id,
        }, read: [
          'role:all',
        ]);
        await database.updateDocument(
            collectionId: 'users', documentId: user.$id, data: {"address": id});
        if (oldBungalowId != null) {
          // delete old bungalow entry
          await database.updateDocument(
              collectionId: 'bungalows',
              documentId: oldBungalowId,
              data: {"current": null});
        }
        // refresh user model
        ref.read(authProvider.notifier).getCurrentUser();
      } else {
        log("bungalow occupied");
        return "Bungalow occupied!";
      }
    } on AppwriteException catch (e) {
      log(e.message.toString());
      if (e.message.toString() == "No document found") {
        return "Bungalow does not exist!";
      }
      return e.message.toString();
    }
    return null;
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
      log(e.message.toString());
    }
    return null;
  }
}
