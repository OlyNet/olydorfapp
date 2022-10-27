import 'dart:developer';

import 'package:appwrite/appwrite.dart';
import 'package:appwrite/models.dart' as appwriteModels;
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/models/user_model.dart';
import 'package:olydorf/providers/auth_provider.dart';

class ProfileHelper {
  static void saveBungalow(WidgetRef ref, String id) async {
    Client client = ref.watch(clientProvider);
    Databases databases = Databases(client);
    Account account = Account(client);
    appwriteModels.Account res = await account.get();
    try {
      await databases.createDocument(
          databaseId: 'olydorf',
          collectionId: 'bungalows',
          documentId: id,
          data: {
            'current': res.$id,
          },
          permissions: [
            Permission.read(Role.any()),
          ]);
    } on AppwriteException catch (e) {
      log(e.message.toString());
    }
  }

  static Future<AppUser?> getBungalow(WidgetRef ref, String id) async {
    Client client = ref.watch(clientProvider);
    Databases database = Databases(client);
    try {
      appwriteModels.Document result = await database.getDocument(
        databaseId: 'olydorf',
        collectionId: 'bungalows',
        documentId: id,
      );
      String userId = result.data['current'];
      appwriteModels.Document userResult = await database.getDocument(
          databaseId: 'olydorf', collectionId: 'users', documentId: userId);

      return AppUser.fromMap(userResult.data);
    } on AppwriteException catch (e) {
      log(e.message.toString());
    }
    return null;
  }
}
